/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import org.rococoa.AlreadyRetained;
import org.rococoa.cocoa.foundation.NSObject;

import com.ochafik.lang.jnaerator.JNAeratorConfig.GenFeatures;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.Enum;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.Statement.Block;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;
import com.ochafik.lang.jnaerator.parser.TypeRef.*;
import com.ochafik.lang.jnaerator.parser.Expression.*;
import com.ochafik.lang.jnaerator.parser.Function.Type;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder.ListWrapper;
import com.ochafik.lang.jnaerator.parser.Declarator.*;
import com.ochafik.util.CompoundCollection;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import static com.ochafik.lang.jnaerator.TypeConversion.*;
import com.ochafik.lang.jnaerator.parser.Function.SignatureType;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;

public class JNADeclarationsConverter extends DeclarationsConverter {
	private static final Pattern manglingCommentPattern = Pattern.compile("@mangling (.*)$", Pattern.MULTILINE);
    public JNADeclarationsConverter(Result result) {
		super(result);
	}
    final JNATypeConversion typeConverter() {
        return (JNATypeConversion)result.typeConverter;
    };
	
    //protected abstract SimpleTypeRef getCallbackType(FunctionSignature functionSignature, Identifier name);
    @Override
    public Struct convertCallback(FunctionSignature functionSignature, Signatures signatures, Identifier callerLibraryName) {
        Struct decl = super.convertCallback(functionSignature, signatures, callerLibraryName);
        if (decl != null) {
            List<Modifier> mods = functionSignature.getFunction().getModifiers();
            decl.setParents(Arrays.asList((SimpleTypeRef)typeRef(
                functionSignature.getFunction().hasModifier(ModifierType.__stdcall) ?
                    StdCallLibrary.StdCallCallback.class :
                    result.config.runtime.callbackClass
            )));
        }
        return decl;
    }

	static Map<Class<?>, Pair<List<Pair<Function, String>>, Set<String>>> cachedForcedMethodsAndTheirSignatures;
	
	public static synchronized Pair<List<Pair<Function,String>>,Set<String>> getMethodsAndTheirSignatures(Class<?> originalLib) {
		if (cachedForcedMethodsAndTheirSignatures == null)
			cachedForcedMethodsAndTheirSignatures = new HashMap<Class<?>, Pair<List<Pair<Function, String>>,Set<String>>>();

		Pair<List<Pair<Function, String>>, Set<String>> pair = cachedForcedMethodsAndTheirSignatures.get(originalLib);
		if (pair == null) {
			pair = new Pair<List<Pair<Function, String>>, Set<String>>(new ArrayList<Pair<Function, String>>(), new HashSet<String>());
			for (Method m : originalLib.getDeclaredMethods()) {
				Function f = Function.fromMethod(m);
				String sig = f.computeSignature(SignatureType.JavaStyle);
				//if (m.getDeclaringClass().equals(NSObject.class) && f.getName().equals("as")) {
				//	Declaration
				//}
				pair.getFirst().add(new Pair<Function, String>(f, sig));
				pair.getSecond().add(sig);
			}
		}
		return pair;
	}
	
	public void addMissingMethods(Class<?> originalLib, Signatures existingSignatures, Struct outputLib) {
		for (Pair<Function, String> f : getMethodsAndTheirSignatures(originalLib).getFirst())
			if (existingSignatures.addMethod(f.getSecond()))
				outputLib.addDeclaration(f.getFirst().clone());
	}
	
	public EmptyDeclaration skipDeclaration(Element e, String... preMessages) {
		if (result.config.limitComments)
			return null;
		
		List<String> mess = new ArrayList<String>();
		if (preMessages != null)
			mess.addAll(Arrays.asList(preMessages));
		mess.addAll(Arrays.asList("SKIPPED:", new Printer(null).formatComments(e, true, true, false).toString(), getFileCommentContent(e), e.toString().replace("*/", "* /")));
		return new EmptyDeclaration(mess.toArray(new String[0]));
	}
	
	public void convertEnum(Enum e, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName) {
		if (e.isForwardDeclaration())
			return;
		
		Identifier rawEnumName = getActualTaggedTypeName(e);
        List<EnumItemResult> results = getEnumValuesAndCommentsByName(e, signatures, libraryClassName);


        boolean hasEnumClass = false;
        if (rawEnumName != null && rawEnumName.resolveLastSimpleIdentifier().getName() != null) {
            Identifier enumName = result.typeConverter.getValidJavaIdentifier(rawEnumName);
            if (!signatures.addClass(enumName))
                return;

            hasEnumClass = true;

            Struct struct = publicStaticClass(enumName, null, Struct.Type.JavaInterface, e);
            out.addDeclaration(new TaggedTypeRefDeclaration(struct));
            if (!result.config.noComments)
                struct.addToCommentBefore("enum values");

            out = struct;
            signatures = new Signatures();
        }

        outputEnumItemsAsConstants(results, out, signatures, libraryClassName, hasEnumClass);
	}


	Map<String, Pair<Function, List<Function>>> functionAlternativesByNativeSignature = new LinkedHashMap<String, Pair<Function, List<Function>>>();

    @Override
    protected void convertFunction(Function function, Signatures signatures, boolean isCallback, DeclarationsHolder out, Identifier libraryClassName, String sig, Identifier functionName, String library, int iConstructor) {
        Pair<Function, List<Function>> alternativesPair = functionAlternativesByNativeSignature.get(sig);
		if (alternativesPair != null) {
			if (result.config.choicesInputFile != null) {
				for (Function alt : alternativesPair.getValue())
					out.addDeclaration(alt.clone());
				return;
			}
		} else {
			functionAlternativesByNativeSignature.put(
				sig,
				alternativesPair = new Pair<Function, List<Function>>(
					cleanClone(function),
					new ArrayList<Function>()
				)
			);
		}
		List<Function> alternatives = alternativesPair.getValue();

		Function natFunc = new Function();

		Element parent = function.getParentElement();
		List<String> ns = new ArrayList<String>(function.getNameSpace());
		boolean isMethod = parent instanceof Struct;
		if (isMethod) {
			ns.clear();
			ns.addAll(parent.getNameSpace());
			switch (((Struct)parent).getType()) {
			case ObjCClass:
			case ObjCProtocol:
				break;
			case CPPClass:
				if (!result.config.genCPlusPlus && !function.hasModifier(ModifierType.Static))
					return;
				ns.add(((Struct)parent).getTag().toString());
				break;
			}
		}

		if (!isMethod && library != null) {
			Boolean alreadyRetained = Result.getMap(result.retainedRetValFunctions, library).get(functionName.toString());
			if (alreadyRetained != null && alreadyRetained) {
				natFunc.addAnnotation(new Annotation(AlreadyRetained.class, expr(alreadyRetained)));
			}
		}
		//String namespaceArrayStr = "{\"" + StringUtils.implode(ns, "\", \"") + "\"}";
		//if (!ns.isEmpty())
		//	natFunc.addAnnotation(new Annotation(Namespace.class, "(value=" + namespaceArrayStr + (isMethod ? ", isClass=true" : "") + ")"));
		boolean isObjectiveC = function.getType() == Type.ObjCMethod;

		natFunc.setType(Function.Type.JavaMethod);
		if (result.config.synchronizedMethods && !isCallback && result.config.useJNADirectCalls)
			natFunc.addModifiers(ModifierType.Synchronized);
		if (result.config.useJNADirectCalls && !isCallback && !isObjectiveC) {
			natFunc.addModifiers(ModifierType.Public, ModifierType.Static, ModifierType.Native);
		}

		try {
			//StringBuilder outPrefix = new StringBuilder();
			TypeRef returnType = null;

			if (!isObjectiveC) {
				returnType = function.getValueType();
				if (returnType == null)
					returnType = new TypeRef.Primitive("int");
				if (returnType != null)
					returnType.addModifiers(function.getModifiers());
			} else {
				returnType = RococoaUtils.fixReturnType(function);
				functionName = ident(RococoaUtils.getMethodName(function));
			}

			Identifier modifiedMethodName;
			if (isCallback) {
				modifiedMethodName = ident(result.config.callbackInvokeMethodName);
			} else {
				modifiedMethodName = result.typeConverter.getValidJavaMethodName(ident(StringUtils.implode(ns, result.config.cPlusPlusNameSpaceSeparator) + (ns.isEmpty() ? "" : result.config.cPlusPlusNameSpaceSeparator) + functionName));
			}
			Set<String> names = new LinkedHashSet<String>();
			//if (ns.isEmpty())

			if (function.getName() != null && !modifiedMethodName.equals(function.getName().toString()) && ns.isEmpty())
				names.add(function.getName().toString());
			if (function.getAsmName() != null)
				names.add(function.getAsmName());

			if (!isCallback && !modifiedMethodName.equals(functionName))
				annotateActualName(natFunc, functionName);

			natFunc.setName(modifiedMethodName);
			natFunc.setValueType(typeConverter().convertTypeToJNA(returnType, TypeConversionMode.ReturnType, libraryClassName));
			if (!result.config.noComments)
				natFunc.importComments(function, isCallback ? null : getFileCommentContent(function));
			
            if (function.getName() != null) {
                Object[] name = new Object[] {function.getName().toString()};
                for (Pair<MessageFormat, MessageFormat> mf : result.config.onlineDocumentationURLFormats) {
                    try {
                        MessageFormat urlFormat = mf.getSecond();
                        URL url = new URL(urlFormat.format(name));
                        URLConnection con = url.openConnection();
                        con.getInputStream().close();
                        MessageFormat displayFormat = mf.getFirst();
                        natFunc.addToCommentBefore("@see <a href=\"" + url + "\">" + displayFormat.format(name) + "</a>");
                        break;
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                    }
                }
            }

			boolean alternativeOutputs = !isCallback;

			Function primOrBufFunc = alternativeOutputs ? natFunc.clone() : null;
			Function natStructFunc = alternativeOutputs ? natFunc.clone() : null;

			Set<String> argNames = new TreeSet<String>();
//			for (Arg arg : function.getArgs())
//				if (arg.getName() != null)
//					argNames.add(arg.getName());

			int iArg = 1;
			for (Arg arg : function.getArgs()) {
				if (arg.isVarArg() && arg.getValueType() == null) {
					//TODO choose vaname dynamically !
					Identifier vaType = ident(isObjectiveC ? NSObject.class : Object.class);
					String argName = chooseJavaArgName("varargs", iArg, argNames);
					natFunc.addArg(new Arg(argName, typeRef(vaType.clone()))).setVarArg(true);
					if (alternativeOutputs) {
						primOrBufFunc.addArg(new Arg(argName, typeRef(vaType.clone()))).setVarArg(true);
						natStructFunc.addArg(new Arg(argName, typeRef(vaType.clone()))).setVarArg(true);
					}
				} else {
					String argName = chooseJavaArgName(arg.getName(), iArg, argNames);

					TypeRef mutType = arg.createMutatedType();
					if (mutType == null)
						throw new UnsupportedConversionException(function, "Argument " + arg.getName() + " cannot be converted");

					if (mutType.toString().contains("NSOpenGLContextParameter")) {
						argName = argName.toString();
					}
					natFunc.addArg(new Arg(argName, typeConverter().convertTypeToJNA(mutType, TypeConversionMode.NativeParameter, libraryClassName)));
					if (alternativeOutputs) {
						primOrBufFunc.addArg(new Arg(argName, typeConverter().convertTypeToJNA(mutType, TypeConversionMode.PrimitiveOrBufferParameter, libraryClassName)));
						natStructFunc.addArg(new Arg(argName, typeConverter().convertTypeToJNA(mutType, TypeConversionMode.NativeParameterWithStructsPtrPtrs, libraryClassName)));
					}
				}
				iArg++;
			}

			String natSign = natFunc.computeSignature(SignatureType.JavaStyle),
				primOrBufSign = alternativeOutputs ? primOrBufFunc.computeSignature(SignatureType.JavaStyle) : null,
				bufSign = alternativeOutputs ? natStructFunc.computeSignature(SignatureType.JavaStyle) : null;

			if (signatures == null || signatures.addMethod(natSign)) {
				if (alternativeOutputs && !primOrBufSign.equals(natSign)) {
					if (!result.config.noComments) {
						if (primOrBufSign.equals(bufSign))
							natFunc.addToCommentBefore(Arrays.asList("@deprecated use the safer method {@link #" + primOrBufSign + "} instead"));
						else
							natFunc.addToCommentBefore(Arrays.asList("@deprecated use the safer methods {@link #" + primOrBufSign + "} and {@link #" + bufSign + "} instead"));
					}
					natFunc.addAnnotation(new Annotation(Deprecated.class));
				}
				collectParamComments(natFunc);
				out.addDeclaration(natFunc);
				alternatives.add(cleanClone(natFunc));
			}

			if (alternativeOutputs) {
				if (signatures == null || signatures.addMethod(primOrBufSign)) {
					collectParamComments(primOrBufFunc);
					out.addDeclaration(primOrBufFunc);
					alternatives.add(cleanClone(primOrBufFunc));
				}
				if (signatures == null || signatures.addMethod(bufSign)) {
					collectParamComments(natStructFunc);
					out.addDeclaration(natStructFunc);
					alternatives.add(cleanClone(natStructFunc));
				}
			}
		} catch (UnsupportedConversionException ex) {
			if (!result.config.limitComments)
				out.addDeclaration(new EmptyDeclaration(getFileCommentContent(function), ex.toString()));
		}
    }

    @Override
	public Struct convertStruct(Struct struct, Signatures signatures, Identifier callerLibraryClass, String callerLibrary, boolean onlyFields) throws IOException {
		Identifier structName = getActualTaggedTypeName(struct);
		if (structName == null)
			return null;
		
		//if (structName.toString().contains("MonoSymbolFile"))
		//	structName.toString();
		
		if (struct.isForwardDeclaration())// && !result.structsByName.get(structName).isForwardDeclaration())
			return null;
		
		if (!signatures.addClass(structName))
			return null;

		boolean isUnion = struct.getType() == Struct.Type.CUnion;
		boolean inheritsFromStruct = false;
		Identifier baseClass = null;
		if (!onlyFields) {
			if (!struct.getParents().isEmpty()) {
				for (SimpleTypeRef parentName : struct.getParents()) {
					Struct parent = result.structsByName.get(parentName.getName());
					if (parent == null) {
						// TODO report error
						continue;
					}
					baseClass = result.typeConverter.getTaggedTypeIdentifierInJava(parent);
					if (baseClass != null) {
						inheritsFromStruct = true;
						break; // TODO handle multiple and virtual inheritage
					}
				}
			}
			if (baseClass == null) {
				Class<?> c = isUnion ? result.config.runtime.unionClass : result.config.runtime.structClass;
                if (result.config.runtime != JNAeratorConfig.Runtime.JNA) {
					baseClass = ident(
						c, 
						expr(typeRef(structName.clone())), 
						expr(typeRef(ident(structName.clone(), "ByValue"))), 
						expr(typeRef(ident(structName.clone(), "ByReference")))
					);
				} else
					baseClass = ident(c);
			}
		}
		Struct structJavaClass = publicStaticClass(structName, baseClass, Struct.Type.JavaClass, struct);
		
		final int iChild[] = new int[] {0};
		
		//cl.addDeclaration(new EmptyDeclaration())
		Signatures childSignatures = new Signatures();
		
//		if (isVirtual(struct) && !onlyFields) {
//			String vptrName = DEFAULT_VPTR_NAME;
//			VariablesDeclaration vptr = new VariablesDeclaration(typeRef(VirtualTablePointer.class), new Declarator.DirectDeclarator(vptrName));
//            //VariablesDeclaration vptr = new VariablesDeclaration(typeRef(result.config.runtime.pointerClass), new Declarator.DirectDeclarator(vptrName));
//			vptr.addModifiers(ModifierType.Public);
//			structJavaClass.addDeclaration(vptr);
//			childSignatures.variablesSignatures.add(vptrName);
//			// TODO add vptr grabber to constructor !
//		}
		
		//List<Declaration> children = new ArrayList<Declaration>();
		for (Declaration d : struct.getDeclarations()) {
			if (d instanceof VariablesDeclaration) {
				convertVariablesDeclaration((VariablesDeclaration)d, childSignatures, structJavaClass, iChild, false, structName, callerLibraryClass, callerLibrary);
			} else if (!onlyFields) {
				if (d instanceof TaggedTypeRefDeclaration) {
					TaggedTypeRef tr = ((TaggedTypeRefDeclaration) d).getTaggedTypeRef();
					if (tr instanceof Struct) {
						outputConvertedStruct((Struct)tr, childSignatures, structJavaClass, callerLibraryClass, callerLibrary, false);
					} else if (tr instanceof Enum) {
						convertEnum((Enum)tr, childSignatures, structJavaClass, callerLibraryClass);
					}
				} else if (d instanceof TypeDef) {
					TypeDef td = (TypeDef)d;
					TypeRef tr = td.getValueType();
					if (tr instanceof Struct) {
						outputConvertedStruct((Struct)tr, childSignatures, structJavaClass, callerLibraryClass, callerLibrary, false);
					} else if (tr instanceof FunctionSignature) {
						convertCallback((FunctionSignature)tr, childSignatures, structJavaClass, callerLibraryClass);
					}
				} else if (result.config.genCPlusPlus && d instanceof Function) {
					Function f = (Function) d;
					String library = result.getLibrary(struct);
					if (library == null)
						continue;
					List<Declaration> decls = new ArrayList<Declaration>();
					convertFunction(f, childSignatures, false, new ListWrapper(decls), callerLibraryClass, -1);
					for (Declaration md : decls) {
						if (!(md instanceof Function))
							continue;
						Function method = (Function) md;
						Identifier methodImplName = method.getName().clone();
						Identifier methodName = result.typeConverter.getValidJavaMethodName(f.getName());
						method.setName(methodName);
						List<Expression> args = new ArrayList<Expression>();
						
						boolean isStatic = f.hasModifier(ModifierType.Static);
						int iArg = 0;
						for (Arg arg : new ArrayList<Arg>(method.getArgs())) {
							if (iArg == 0 && !isStatic) {
								arg.replaceBy(null);
								args.add(thisRef());
							} else
								args.add(varRef(arg.getName()));
							iArg++;
						}
						Expression implCall = methodCall(result.getLibraryInstanceReferenceExpression(library), MemberRefStyle.Dot, methodImplName.toString(), args.toArray(new Expression[args.size()]));
						method.setBody(block(
							"void".equals(String.valueOf(method.getValueType())) ?
								stat(implCall) : 
								new Statement.Return(implCall)
						));
						method.addModifiers(ModifierType.Public, isStatic ? ModifierType.Static : null);
						structJavaClass.addDeclaration(method);
					}
				}
			}
		}
		
		if (!onlyFields) {
			if (result.config.features.contains(GenFeatures.StructConstructors))
				addStructConstructors(structName, structJavaClass/*, byRef, byVal*/, struct);
			
			Struct byRef = publicStaticClass(ident("ByReference"), structName, Struct.Type.JavaClass, null, ident(ident(result.config.runtime.structClass), "ByReference"));
			Struct byVal = publicStaticClass(ident("ByValue"), structName, Struct.Type.JavaClass, null, ident(ident(result.config.runtime.structClass), "ByValue"));
			
			if (result.config.runtime != JNAeratorConfig.Runtime.JNA) {
				if (!inheritsFromStruct) {
					structJavaClass.addDeclaration(createNewStructMethod("newByReference", byRef));
					structJavaClass.addDeclaration(createNewStructMethod("newByValue", byVal));
				}
				structJavaClass.addDeclaration(createNewStructMethod("newInstance", structJavaClass));
	
				structJavaClass.addDeclaration(createNewStructArrayMethod(structJavaClass, isUnion));
			}

			structJavaClass.addDeclaration(decl(byRef));
			structJavaClass.addDeclaration(decl(byVal));
		}
		return structJavaClass;
	}
	
	protected Function createNewStructMethod(String name, Struct byRef) {
		TypeRef tr = typeRef(byRef.getTag().clone());
		Function f = new Function(Function.Type.JavaMethod, ident(name), tr);
		String varName = "s";

		f.addModifiers(ModifierType.Protected);
		if (result.config.runtime != JNAeratorConfig.Runtime.JNA) {
			f.setBody(block(
				//new Statement.Return(methodCall("setupClone", new Expression.New(tr.clone(), methodCall(null))))
					new Statement.Return(new Expression.New(tr.clone(), methodCall(null)))
			).setCompact(true));
		} else {
			f.setBody(block(
				stat(tr.clone(), varName, new Expression.New(tr.clone(), methodCall(null))),
				stat(methodCall(varRef(varName), MemberRefStyle.Dot, "useMemory", methodCall("getPointer"))),
				stat(methodCall("write")),
				stat(methodCall(varRef(varName), MemberRefStyle.Dot, "read")),
				new Statement.Return(varRef(varName))
			));
		}
		return f;
	}
	protected Function createNewStructArrayMethod(Struct struct, boolean isUnion) {
		if (result.config.runtime == JNAeratorConfig.Runtime.JNA)
			return null;

		TypeRef tr = typeRef(struct.getTag().clone());
		TypeRef ar = new TypeRef.ArrayRef(tr);
		String varName = "arrayLength";
		Function f = new Function(Function.Type.JavaMethod, ident("newArray"), ar, new Arg(varName, typeRef(Integer.TYPE)));
		
		f.addModifiers(ModifierType.Public, ModifierType.Static);
		f.setBody(block(
			new Statement.Return(
				methodCall(
					expr(typeRef(isUnion ? result.config.runtime.unionClass : result.config.runtime.structClass)),
					MemberRefStyle.Dot,
					"newArray",
					result.typeConverter.typeLiteral(tr),
					varRef(varName)
				)
			)
		));
		return f;
	}
    
	public int countFieldsInStruct(Struct s) throws UnsupportedConversionException {
		int count = 0;
		for (Declaration declaration : s.getDeclarations()) {
			if (declaration instanceof VariablesDeclaration) {
				count += ((VariablesDeclaration)declaration).getDeclarators().size();
			}
		}
		for (SimpleTypeRef parentName : s.getParents()) {
			Struct parent = result.structsByName.get(parentName.getName());
			if (parent == null)
				throw new UnsupportedConversionException(s, "Cannot find parent " + parentName + " of struct " + s);
			
			count += countFieldsInStruct(parent);
		}
		return count;
	}

	public VariablesDeclaration convertVariablesDeclarationToJNA(String name, TypeRef mutatedType, int[] iChild, Identifier callerLibraryName, Element... toImportDetailsFrom) throws UnsupportedConversionException {
		name = result.typeConverter.getValidJavaArgumentName(ident(name)).toString();
		//convertVariablesDeclaration(name, mutatedType, out, iChild, callerLibraryName);

		Expression initVal = null;
		TypeRef  javaType = typeConverter().convertTypeToJNA(
			mutatedType, 
			TypeConversion.TypeConversionMode.FieldType,
			callerLibraryName
		);
		mutatedType = result.typeConverter.resolveTypeDef(mutatedType, callerLibraryName, true, false);
		
		VariablesDeclaration convDecl = new VariablesDeclaration();
		convDecl.addModifiers(ModifierType.Public);
		
		if (javaType instanceof ArrayRef && mutatedType instanceof ArrayRef) {
			ArrayRef mr = (ArrayRef)mutatedType;
			ArrayRef jr = (ArrayRef)javaType;
			Expression mul = null;
			List<Expression> dims = mr.flattenDimensions();
			for (int i = dims.size(); i-- != 0;) {
				Expression x = dims.get(i);
			
				if (x == null || x instanceof EmptyArraySize) {
					javaType = jr = new ArrayRef(typeRef(Pointer.class));
					break;
				} else {
					Pair<Expression, TypeRef> c = result.typeConverter.convertExpressionToJava(x, callerLibraryName, false);
					c.getFirst().setParenthesis(dims.size() != 1);
					if (mul == null)
						mul = c.getFirst();
					else
						mul = expr(c.getFirst(), BinaryOperator.Multiply, mul);
				}
			}
			initVal = new Expression.NewArray(jr.getTarget(), new Expression[] { mul }, new Expression[0]);
		}
		if (javaType == null) {
			throw new UnsupportedConversionException(mutatedType, "failed to convert type to Java");
		} else if (javaType.toString().equals("void")) {
			throw new UnsupportedConversionException(mutatedType, "void type !");
			//out.add(new EmptyDeclaration("SKIPPED:", v.formatComments("", true, true, false), v.toString()));
		} else {
			for (Element e : toImportDetailsFrom)
				convDecl.importDetails(e, false);
			convDecl.importDetails(mutatedType, true);
			convDecl.importDetails(javaType, true);
			
//			convDecl.importDetails(v, false);
//			convDecl.importDetails(vs, false);
//			convDecl.importDetails(valueType, false);
//			valueType.stripDetails();
			convDecl.moveAllCommentsBefore();
			convDecl.setValueType(javaType);
			convDecl.addDeclarator(new DirectDeclarator(name, initVal));
			
			return convDecl;//out.addDeclaration(convDecl);
		}
	}
    int nextAnonymousFieldId;
    @Override
	public void convertVariablesDeclaration(VariablesDeclaration v, Signatures signatures, DeclarationsHolder out, int[] iChild, boolean isGlobal, Identifier holderName, Identifier callerLibraryClass, String callerLibrary) {
		//List<Declaration> out = new ArrayList<Declaration>();
		try {
			TypeRef valueType = v.getValueType();
			for (Declarator vs : v.getDeclarators()) {
				String name = vs.resolveName();
				if (name == null || name.length() == 0) {
					name = "anonymous" + (nextAnonymousFieldId++);
				}
	
				TypeRef mutatedType = valueType;
				if (!(vs instanceof DirectDeclarator))
				{
					mutatedType = (TypeRef)vs.mutateTypeKeepingParent(valueType);
					vs = new DirectDeclarator(vs.resolveName());
				}
				VariablesDeclaration vd = convertVariablesDeclarationToJNA(name, mutatedType, iChild, callerLibraryClass, v, vs);
				if (vd != null) {
					Declarator d = v.getDeclarators().get(0);
					if (d.getBits() > 0) {
						int bits = d.getBits();
                                                if (!result.config.runtime.hasBitFields)
                                                    throw new UnsupportedConversionException(d, "This runtime does not support bit fields : " + result.config.runtime + " (please use BridJ instead)");
                                                
						vd.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Bits), expr(bits)));
						String st = vd.getValueType().toString(), mst = st;
						if (st.equals("int") || st.equals("long") || st.equals("short") || st.equals("long")) {
							if (bits <= 8)
								mst = "byte";
							else if (bits <= 16)
								mst = "short";
							else if (bits <= 32)
								mst = "int";
							else
								mst = "long"; // should not happen
						}
						if (!st.equals(mst))
							vd.setValueType(new Primitive(mst));
					}
					if (!(mutatedType instanceof Primitive) && !result.config.noComments)
						vd.addToCommentBefore("C type : " + mutatedType);
					out.addDeclaration(vd);
				}
				if (result.config.beanStructs) {
                    Function getMethod = new Function(Function.Type.JavaMethod, ident("get" + StringUtils.capitalize(name)), vd.getValueType().clone()).setBody(block(
						new Statement.Return(varRef(name))
					)).addModifiers(ModifierType.Public);
                    if (signatures.addMethod(getMethod))
                        out.addDeclaration(getMethod);
                    
                    Function setMethod = new Function(Function.Type.JavaMethod, ident("set" + StringUtils.capitalize(name)), typeRef(Void.TYPE), new Arg(name, vd.getValueType().clone())).setBody(block(
						stat(expr(memberRef(thisRef(), MemberRefStyle.Dot, ident(name)), AssignmentOperator.Equal, varRef(name)))
					)).addModifiers(ModifierType.Public);
                    if (signatures.addMethod(setMethod))
                        out.addDeclaration(setMethod);
				}
				iChild[0]++;
			}
		} catch (UnsupportedConversionException e) {
			if (!result.config.limitComments)
				out.addDeclaration(new EmptyDeclaration(e.toString()));
		}
	}
	TaggedTypeRefDeclaration publicStaticClassDecl(Identifier name, Identifier parentName, Struct.Type type, Element toCloneCommentsFrom, Identifier... interfaces) {
		return decl(publicStaticClass(name, parentName, type, toCloneCommentsFrom, interfaces));
	}
	Struct publicStaticClass(Identifier name, Identifier parentName, Struct.Type type, Element toCloneCommentsFrom, Identifier... interfaces) {
		Struct cl = new Struct();
		cl.setType(type);
		cl.setTag(name);
		if (parentName != null)
			cl.setParents(typeRef(parentName));
		if (type == Struct.Type.JavaInterface)
			for (Identifier inter : interfaces)
				cl.addParent(typeRef(inter));
		else
            for (Identifier inter : interfaces)
                cl.addProtocol(typeRef(inter));
		
		if (!result.config.noComments)
            cl.importComments(toCloneCommentsFrom, getFileCommentContent(toCloneCommentsFrom));
        
		cl.addModifiers(ModifierType.Public, ModifierType.Static);
		return cl;
	}
	public Pair<List<VariablesDeclaration>, List<VariablesDeclaration>> getParentAndOwnDeclarations(Struct structJavaClass, Struct nativeStruct) throws IOException {
		Pair<List<VariablesDeclaration>, List<VariablesDeclaration>> ret = 
			new Pair<List<VariablesDeclaration>, List<VariablesDeclaration>>(
				new ArrayList<VariablesDeclaration>(), 
				new ArrayList<VariablesDeclaration>()
			)
		;
		if (!nativeStruct.getParents().isEmpty()) {
			for (SimpleTypeRef parentName : nativeStruct.getParents()) {
				Struct parent = result.structsByName.get(parentName.getName());
				if (parent == null) {
					// TODO report error
					continue;
				}
				Struct parentJavaClass = convertStruct(parent, new Signatures(), null, null, true);
				Pair<List<VariablesDeclaration>, List<VariablesDeclaration>> parentDecls = getParentAndOwnDeclarations(parentJavaClass, parent);
				ret.getFirst().addAll(parentDecls.getFirst());
				ret.getFirst().addAll(parentDecls.getSecond());
			}
		}
		for (Declaration d : structJavaClass.getDeclarations()) {
			if (!(d instanceof VariablesDeclaration))
				continue;
			VariablesDeclaration vd = (VariablesDeclaration)d;
			if (vd.getDeclarators().size() != 1)
				continue; // should not happen !
			if (!isField(vd))
				continue;
			
			ret.getSecond().add(vd);
		}
				
		return ret;
	}
	@SuppressWarnings("unchecked")
	private void addStructConstructors(Identifier structName, Struct structJavaClass/*, Struct byRef,
			Struct byVal*/, Struct nativeStruct) throws IOException {
		
		List<Declaration> initialMembers = new ArrayList<Declaration>(structJavaClass.getDeclarations());
		Set<String> signatures = new TreeSet<String>();
		
		Function emptyConstructor = new Function(Function.Type.JavaMethod, structName.clone(), null).addModifiers(ModifierType.Public);
		emptyConstructor.setBody(block(stat(methodCall("super"))));
		addConstructor(structJavaClass, emptyConstructor);
		
		
		boolean isUnion = nativeStruct.getType() == Struct.Type.CUnion;
		if (isUnion) {
			Map<String, Pair<TypeRef, List<Pair<String, String>>>> fieldsAndCommentsByTypeStr = new HashMap<String, Pair<TypeRef, List<Pair<String, String>>>>();
			for (Declaration d : initialMembers) {
				if (!(d instanceof VariablesDeclaration))
					continue;
					
				VariablesDeclaration vd = (VariablesDeclaration)d;
				if (vd.getDeclarators().size() != 1)
					continue; // should not happen !
				String name = vd.getDeclarators().get(0).resolveName();
				TypeRef tr = vd.getValueType();
				if (!isField(vd))
					continue;
				
				String trStr = tr.toString();
				Pair<TypeRef, List<Pair<String, String>>> pair = fieldsAndCommentsByTypeStr.get(trStr);
				if (pair == null)
					fieldsAndCommentsByTypeStr.put(trStr, pair = new Pair<TypeRef, List<Pair<String, String>>>(tr, new ArrayList<Pair<String, String>>()));
				
				pair.getSecond().add(new Pair<String, String>(vd.getCommentBefore(), name));
			}
			for (Pair<TypeRef, List<Pair<String, String>>> pair : fieldsAndCommentsByTypeStr.values()) {
				List<String> commentBits = new ArrayList<String>(), nameBits = new ArrayList<String>();
				for (Pair<String, String> p : pair.getValue()) {
					if (p.getFirst() != null)
						commentBits.add(p.getFirst());
					nameBits.add(p.getValue());
				}
				String name = StringUtils.implode(nameBits, "_or_");
				TypeRef tr = pair.getFirst();
				Function unionValConstr = new Function(Function.Type.JavaMethod, structName.clone(), null, new Arg(name, tr.clone()));
				if (!result.config.noComments)
					if (!commentBits.isEmpty())
						unionValConstr.addToCommentBefore("@param " + name + " " + StringUtils.implode(commentBits, ", or "));
				
				unionValConstr.addModifiers(ModifierType.Public);
				
				Expression assignmentExpr = varRef(name);
				for (Pair<String, String> p : pair.getValue())
					assignmentExpr = new Expression.AssignmentOp(memberRef(thisRef(), MemberRefStyle.Dot, ident(p.getValue())), AssignmentOperator.Equal, assignmentExpr);
				
				unionValConstr.setBody(block(
					stat(methodCall("super")),
					tr instanceof TypeRef.ArrayRef ? throwIfArraySizeDifferent(name) : null,
					stat(assignmentExpr),
					stat(methodCall("setType", result.typeConverter.getJavaClassLitteralExpression(tr)))
				));
				
				if (signatures.add(unionValConstr.computeSignature(SignatureType.JavaStyle))) {
					structJavaClass.addDeclaration(unionValConstr);
//					byRef.addDeclaration(unionValConstr.clone().setName(byRef.getTag().clone()));
//					byVal.addDeclaration(unionValConstr.clone().setName(byVal.getTag().clone()));
				}
			}
		} else {
			Function fieldsConstr = new Function(Function.Type.JavaMethod, structName.clone(), null);
			fieldsConstr.setBody(new Block()).addModifiers(ModifierType.Public);
			
			Pair<List<VariablesDeclaration>, List<VariablesDeclaration>> decls = getParentAndOwnDeclarations(structJavaClass, nativeStruct);
			Map<Integer, String> namesById = new TreeMap<Integer, String>();
			Set<String> names = new HashSet<String>();
			List<Expression> orderedFieldNames = new ArrayList<Expression>();
			int iArg = 0;
			for (VariablesDeclaration vd : new CompoundCollection<VariablesDeclaration>(decls.getFirst(), decls.getSecond())) {
				String name = chooseJavaArgName(vd.getDeclarators().get(0).resolveName(), iArg, names);
				namesById.put(vd.getId(), name);
				fieldsConstr.addArg(new Arg(name, vd.getValueType().clone()));
				iArg++;
			}
            
			FunctionCall superCall = methodCall("super");
            // Adding parent fields
			for (VariablesDeclaration vd : decls.getFirst()) {
				String name = vd.getDeclarators().get(0).resolveName(), uname = namesById.get(vd.getId());
				Struct parent = (Struct)vd.getParentElement();
				Identifier parentTgName = typeConverter().getTaggedTypeIdentifierInJava(parent);
				if (!result.config.noComments)
					fieldsConstr.addToCommentBefore("@param " + name + " @see " + parentTgName + "#" + vd.getDeclarators().get(0).resolveName());
				superCall.addArgument(varRef(uname));
                //orderedFieldNames.add(expr(name));
			}
			fieldsConstr.getBody().addStatement(stat(superCall));
			
			// Adding class' own fields
			for (VariablesDeclaration vd : decls.getSecond()) {
				String name = vd.getDeclarators().get(0).resolveName(), uname = namesById.get(vd.getId());
				if (!result.config.noComments)
					if (vd.getCommentBefore() != null)
						fieldsConstr.addToCommentBefore("@param " + uname + " " + vd.getCommentBefore());
				if (vd.getValueType() instanceof TypeRef.ArrayRef)
					fieldsConstr.getBody().addStatement(throwIfArraySizeDifferent(uname));
				fieldsConstr.getBody().addStatement(stat(
						new Expression.AssignmentOp(memberRef(thisRef(), MemberRefStyle.Dot, ident(name)), AssignmentOperator.Equal, varRef(uname))));
                
                orderedFieldNames.add(expr(name));
			}
            
            String getFieldOrderName = "getFieldOrder";
            Expression selfList = methodCall(
                expr(typeRef(Arrays.class)), 
                "asList",
                orderedFieldNames.toArray(new Expression[orderedFieldNames.size()])
            );
            Block getFieldOrderImpl;
            if (nativeStruct.getParents().isEmpty()) {
                getFieldOrderImpl = block(new Statement.Return(selfList));
            } else {
                String fieldOrderName = "fieldOrder";
                VariablesDeclaration vd = 
                    new VariablesDeclaration(
                        typeRef(List.class), 
                        new DirectDeclarator(
                            fieldOrderName, 
                            new Expression.New(
                                typeRef(ArrayList.class),
                                (Expression)methodCall(varRef("super"), getFieldOrderName))));
                getFieldOrderImpl = block(
                    vd,
                    stat(methodCall(varRef(fieldOrderName), "addAll", selfList)),
                    new Statement.Return(varRef(fieldOrderName))
                );
            }
            Function getFieldOrder = new Function(Type.JavaMethod, ident(getFieldOrderName), typeRef(List.class)).setBody(getFieldOrderImpl).addModifiers(ModifierType.Protected);
            
            if (signatures.add(getFieldOrder.computeSignature(SignatureType.JavaStyle))) {
                structJavaClass.addDeclaration(getFieldOrder);
            }
            
			int nArgs = fieldsConstr.getArgs().size();
			if (nArgs == 0)
				System.err.println("Struct with no field : " + structName);
			
			if (nArgs > 0 && nArgs < result.config.maxConstructedFields) {
				if (signatures.add(fieldsConstr.computeSignature(SignatureType.JavaStyle))) {
					structJavaClass.addDeclaration(fieldsConstr);
				}
			}
		}
    }

    @Override
    protected void configureCallbackStruct(Struct callbackStruct) {
        callbackStruct.setType(Struct.Type.JavaInterface);
        callbackStruct.addModifiers(ModifierType.Public);
    }

    @Override
    protected Struct createFakePointerClass(Identifier fakePointer) {
        Struct ptClass = result.declarationsConverter.publicStaticClass(fakePointer, ident(PointerType.class), Struct.Type.JavaClass, null);

        String pointerVarName = "address";
        ptClass.addDeclaration(new Function(Function.Type.JavaMethod, fakePointer, null,
            new Arg(pointerVarName, typeRef(com.sun.jna.Pointer.class))
        ).addModifiers(ModifierType.Public).setBody(
            block(stat(methodCall("super", varRef(pointerVarName)))))
        );
        ptClass.addDeclaration(new Function(Function.Type.JavaMethod, fakePointer, null)
        .addModifiers(ModifierType.Public)
        .setBody(
            block(stat(methodCall("super")))
        ));
        return ptClass;
    }

    private void annotateActualName(ModifiableElement e, Identifier name) {
        e.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Name), expr(name.toString())));
    }
}
