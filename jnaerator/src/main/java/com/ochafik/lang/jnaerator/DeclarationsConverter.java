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


import static com.ochafik.lang.SyntaxUtils.as;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.Enum;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;
import com.ochafik.lang.jnaerator.parser.TypeRef.*;
import com.ochafik.lang.jnaerator.parser.Expression.*;
import com.ochafik.lang.jnaerator.parser.Function.Type;
import com.ochafik.lang.jnaerator.parser.Declarator.*;
import com.ochafik.lang.jnaerator.runtime.VirtualTablePointer;
import com.ochafik.util.listenable.Pair;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import static com.ochafik.lang.jnaerator.TypeConversion.*;

public abstract class DeclarationsConverter {
	protected static final String DEFAULT_VPTR_NAME = "_vptr";

	public DeclarationsConverter(Result result) {
		this.result = result;
	}

	protected final Result result;
	
    
    public void convertCallback(FunctionSignature functionSignature, Signatures signatures, DeclarationsHolder out, Identifier callerLibraryName) {
        Struct decl = convertCallback(functionSignature, signatures, callerLibraryName);
        if (decl != null)
            out.addDeclaration(new TaggedTypeRefDeclaration(decl));
    }
	public Struct convertCallback(FunctionSignature functionSignature, Signatures signatures, Identifier callerLibraryName) {
		Identifier name = result.typeConverter.inferCallBackName(functionSignature, true, false, callerLibraryName);
		if (name == null)
			return null;
		
		name = result.typeConverter.getValidJavaArgumentName(name);
		
		Function function = functionSignature.getFunction();
		
		int i = 1;
		Identifier chosenName = name;
		while (!(signatures.addClass(chosenName))) {
			chosenName = ident(name.toString() + (++i));
		}
		
		Element parent = functionSignature.getParentElement();
		Element comel = parent != null && parent instanceof TypeDef ? parent : functionSignature;
		
		Struct callbackStruct = new Struct();
        configureCallbackStruct(callbackStruct);
		//callbackStruct.setParents(Arrays.asList(getCallbackType(functionSignature, chosenName)));
		callbackStruct.setTag(ident(chosenName));
		if (!result.config.noComments)
			callbackStruct.addToCommentBefore(comel.getCommentBefore(), comel.getCommentAfter(), getFileCommentContent(comel));
		convertFunction(function, new Signatures(), true, callbackStruct, callerLibraryName, -1);
		for (Declaration d : callbackStruct.getDeclarations()) {
			if (d instanceof Function) {
				callbackStruct.addAnnotations(callbackStruct.getAnnotations());
				callbackStruct.setAnnotations(null);
				break;
			}
		}
		return callbackStruct;
	}

	public void convertCallbacks(List<FunctionSignature> functionSignatures, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName) {
		if (functionSignatures != null) {
			for (FunctionSignature functionSignature : functionSignatures) {
				if (functionSignature.findParentOfType(Struct.class) != null)
					continue;
				
				Arg a = functionSignature.findParentOfType(Arg.class);
				if (a != null && a.getParentElement() == null)
					continue;//TODO understand why we end up having an orphan Arg here !!!!
					
				convertCallback(functionSignature, signatures, out, libraryClassName);
			}
		}
		
	}

    protected abstract void configureCallbackStruct(Struct callbackStruct);

    protected abstract void convertFunction(Function function, Signatures signatures, boolean callback, DeclarationsHolder objOut, Identifier libraryClassName, String sig, Identifier functionName, String library, int iConstructor);
    
    static class EnumItemResult {
        public Enum.EnumItem originalItem;
        public Expression value;
        public String comments;
        public String exceptionMessage;
        public Declaration errorElement;
    }
    protected List<EnumItemResult> getEnumValuesAndCommentsByName(Enum e, Signatures signatures, Identifier libraryClassName) {
        List<EnumItemResult> ret = new ArrayList<EnumItemResult>();
        Integer lastAdditiveValue = null;
		Expression lastRefValue = null;
		boolean failedOnceForThisEnum = false;
		for (com.ochafik.lang.jnaerator.parser.Enum.EnumItem item : e.getItems()) {
            EnumItemResult res = new EnumItemResult();
            res.originalItem = item;
			try {
				if (item.getArguments().isEmpty()) {
					// no explicit value
					if (lastRefValue == null) {
						if (lastAdditiveValue != null) {
							lastAdditiveValue++;
							res.value = expr(lastAdditiveValue);
						} else {
							if (item == e.getItems().get(0)) {
								lastAdditiveValue = 0;
								res.value = expr(lastAdditiveValue);
							} else
								res.value = null;
						}
					} else {
						// has a last reference value
						if (lastAdditiveValue != null)
							lastAdditiveValue++;
						else
							lastAdditiveValue = 1;

						res.value = //result.typeConverter.convertExpressionToJava(
							expr(
								lastRefValue.clone(),
								Expression.BinaryOperator.Plus,
								expr(lastAdditiveValue)
							//)
						);
					}
				} else {
					// has an explicit value
					failedOnceForThisEnum = false;// reset skipping
					lastAdditiveValue = null;
					lastRefValue = item.getArguments().get(0);
					res.value = lastRefValue;
					if (lastRefValue instanceof Expression.Constant) {
						try {
							lastAdditiveValue = ((Expression.Constant)lastRefValue).asInteger();
							lastRefValue = null;
						} catch (Exception ex) {}
					}
				}
				
				Expression convertedValue = result.typeConverter.convertExpressionToJava(res.value, libraryClassName, true).getFirst();
				res.value = convertedValue;
			} catch (Exception ex) {
                failedOnceForThisEnum = true;
                res.exceptionMessage = ex.toString();
			}
			failedOnceForThisEnum = failedOnceForThisEnum || res.errorElement != null;
			if (failedOnceForThisEnum)
				res.errorElement = skipDeclaration(item);

            ret.add(res);
		}
        return ret;
    }

	public void convertConstants(String library, List<Define> defines, Element sourcesRoot, final Signatures signatures, final DeclarationsHolder out, final Identifier libraryClassName) {
		//final List<Define> defines = new ArrayList<Define>();
		final Map<String, String> constants = Result.getMap(result.stringConstants, library);
//		
		sourcesRoot.accept(new Scanner() {
//			@Override
//			public void visitDefine(Define define) {
//				super.visitDefine(define);
//				if (elementsFilter.accept(define))
//					defines.add(define);
//			}
			@Override
			public void visitVariablesDeclaration(VariablesDeclaration v) {
				super.visitVariablesDeclaration(v);
				//if (!elementsFilter.accept(v))
				//	return;
				
				if (v.findParentOfType(Struct.class) != null)
					return;
				
				if (v.getValueType() instanceof FunctionSignature)
					return;
					
				for (Declarator decl : v.getDeclarators()) {
					if (!(decl instanceof DirectDeclarator))
						continue; // TODO provide a mapping of exported values
					
					TypeRef mutatedType = (TypeRef) decl.mutateType(v.getValueType());
					if (mutatedType == null || 
							!mutatedType.getModifiers().contains(ModifierType.Const) ||
							mutatedType.getModifiers().contains(ModifierType.Extern) ||
							decl.getDefaultValue() == null)
						continue;
					
					//TypeRef type = v.getValueType();
					String name = decl.resolveName();
					
					JavaPrim prim = result.typeConverter.getPrimitive(mutatedType, libraryClassName);
					if (prim == null) {
						if (mutatedType.toString().contains("NSString")) {
							String value = constants.get(name);
							if (value != null)
								outputNSString(name, value, out, signatures, v, decl);
						}
						continue;
					}
					
					try {
						
						//DirectDeclarator dd = (DirectDeclarator)decl;
						Pair<Expression, TypeRef> val = result.typeConverter.convertExpressionToJava(decl.getDefaultValue(), libraryClassName, true);
						
						if (!signatures.addVariable(name))
							continue;
						
                        
                        // TODO
						TypeRef tr = prim == JavaPrim.NativeLong || prim == JavaPrim.NativeSize ?
							typeRef("long") :
							result.typeConverter.convertTypeToJNA(mutatedType, TypeConversion.TypeConversionMode.FieldType, libraryClassName)
						;
						VariablesDeclaration vd = new VariablesDeclaration(tr, new DirectDeclarator(name, val.getFirst()));
						if (!result.config.noComments) {
							vd.setCommentBefore(v.getCommentBefore());
							vd.addToCommentBefore(decl.getCommentBefore());
							vd.addToCommentBefore(decl.getCommentAfter());
							vd.addToCommentBefore(v.getCommentAfter());
						}

                        if (result.config.runtime == JNAeratorConfig.Runtime.BridJ)
                            vd.addModifiers(ModifierType.Public, ModifierType.Static, ModifierType.Final);
						
						out.addDeclaration(vd);
					} catch (UnsupportedConversionException e) {
						out.addDeclaration(skipDeclaration(v, e.toString()));
					}
					
				}
			}

		});
		
		if (defines != null) {
			for (Define define : reorderDefines(defines)) {
				if (define.getValue() == null)
					continue;
				
				try {
					//System.out.println("Define " + define.getName() + " = " + define.getValue());
					out.addDeclaration(outputConstant(define.getName(), define.getValue(), signatures, define.getValue(), "define", libraryClassName, true, false, false));
				} catch (UnsupportedConversionException ex) {
					out.addDeclaration(skipDeclaration(define, ex.toString()));
				}
			}
		}
		for (Map.Entry<String, String> e : constants.entrySet()) {
			outputNSString(e.getKey(), e.getValue(), out, signatures);
		}
	}


	protected void outputNSString(String name, String value, DeclarationsHolder out, Signatures signatures, Element... elementsToTakeCommentsFrom) {

		if (!signatures.addVariable(name))
			return;
		
		TypeRef tr = typeRef(String.class);
		VariablesDeclaration vd = new VariablesDeclaration(tr, new DirectDeclarator(name, expr(value)));
		if (!result.config.noComments)
			for (Element e : elementsToTakeCommentsFrom) {
				vd.addToCommentBefore(e.getCommentBefore());
				vd.addToCommentBefore(e.getCommentAfter());
			}
		vd.addModifiers(ModifierType.Public);
		out.addDeclaration(vd);
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
				String sig = f.computeSignature(false);
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
	
	public abstract void convertEnum(Enum e, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName);

	protected void outputEnumItemsAsConstants(List<EnumItemResult> results,
			DeclarationsHolder out, Signatures signatures, Identifier libraryClassName,
			boolean hasEnumClass) {
		

    	for (EnumItemResult er : results) {
            try {
                if (er.errorElement != null) {
                    out.addDeclaration(er.errorElement);
                    continue;
                }
                Declaration ct = outputConstant(
                    er.originalItem.getName(),
                    result.typeConverter.convertExpressionToJava(er.value, libraryClassName, true),
                    signatures,
                    er.originalItem,
                    "enum item",
                    libraryClassName,
                    hasEnumClass,
                    true,
                    true,
                    true
                );
                if (!result.config.noComments)
                    if (ct != null && hasEnumClass) {
                        String c = ct.getCommentBefore();
                        ct.setCommentBefore(er.originalItem.getCommentBefore());
                        ct.addToCommentBefore(c);
                    }
                out.addDeclaration(ct);
            } catch (Exception ex) {
                out.addDeclaration(skipDeclaration(er.originalItem, ex.toString()));
            }
        }
	}

	protected Declaration outputConstant(String name, Expression x, Signatures signatures, Element element, String elementTypeDescription, Identifier libraryClassName, boolean addFileComment, boolean signalErrors, boolean forceInteger) throws UnsupportedConversionException {
		return outputConstant(name, pair(x, (TypeRef)null), signatures, element, elementTypeDescription, libraryClassName, addFileComment, signalErrors, forceInteger, false);
	}
	@SuppressWarnings("static-access")
	protected Declaration outputConstant(String name, Pair<Expression, TypeRef> x, Signatures signatures, Element element, String elementTypeDescription, Identifier libraryClassName, boolean addFileComment, boolean signalErrors, boolean forceInteger, boolean alreadyConverted) throws UnsupportedConversionException {
		try {
			if (result.typeConverter.isJavaKeyword(name))
				throw new UnsupportedConversionException(element, "The name '" + name + "' is invalid for a Java field.");
			
			Pair<Expression, TypeRef> converted = alreadyConverted ? x : result.typeConverter.convertExpressionToJava(x.getFirst(), libraryClassName, true);
			//TypeRef tr = result.typeConverter.inferJavaType(converted);
			JavaPrim prim = result.typeConverter.getPrimitive(converted.getValue(), libraryClassName);
			
			if (forceInteger && prim == JavaPrim.Boolean) {
				prim = JavaPrim.Int;
				//tr = typeRef("int");
				converted = pair(expr("true".equals(String.valueOf(converted.toString())) ? 1 : 0), typeRef(Integer.TYPE));
			}
			
			if ((prim == null || converted.getValue() == null) && signalErrors) {
				if (result.config.limitComments)
					return null;
				
				return new EmptyDeclaration("Failed to infer type of " + converted);
			} else if (prim != JavaPrim.Void && converted.getValue() != null) {
//				if (prim == JavaPrim.Int)
//					tr = typeRef("long");
				
				if (signatures.addVariable(name)) {
					String t = converted.toString();
					if (t.contains("sizeof")) {
						converted = alreadyConverted ? x : result.typeConverter.convertExpressionToJava(x.getFirst(), libraryClassName, false);
					}
                    //TypeRef tr = new TypeRef.SimpleTypeRef(result.typeConverter.typeToJNA(type, vs, TypeConversion.TypeConversionMode.FieldType, callerLibraryClass));
                    TypeRef tr = converted.getValue();
                    Expression value = new Cast(tr, converted.getFirst());

					Declaration declaration = new VariablesDeclaration(tr, new DirectDeclarator(name, value));
					declaration.addModifiers(ModifierType.Public, ModifierType.Static, ModifierType.Final);
					declaration.importDetails(element, false);
					declaration.moveAllCommentsBefore();
					if (!result.config.noComments)
						if (addFileComment)
							declaration.addToCommentBefore(getFileCommentContent(element));
					return declaration;
				}
			}
			return skipDeclaration(element, elementTypeDescription);
		} catch (UnsupportedConversionException e) {
			return skipDeclaration(element, elementTypeDescription, e.toString());
		}	
		
	} 

	public void convertEnums(List<Enum> enums, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName) {
		if (enums != null) {
			//out.println("public static class ENUMS {");
			for (com.ochafik.lang.jnaerator.parser.Enum e : enums) {
				if (e.findParentOfType(Struct.class) != null)
					continue;
				
				convertEnum(e, signatures, out, libraryClassName);
			}
			//out.println("}");
		}
	}

	Map<String, Pair<Function, List<Function>>> functionAlternativesByNativeSignature = new LinkedHashMap<String, Pair<Function, List<Function>>>();

	@SuppressWarnings("unchecked")
	static <E extends Element> E cleanClone(E e) {
		E c = (E)e.clone();
		c.setCommentBefore(null);
		c.setCommentAfter(null);
		if (c instanceof Declaration) {
			Declaration d = (Declaration)c;
			d.setAnnotations(null);
		}
		return c;
	}
	void throwBadRuntime() {
        throw new RuntimeException("Unhandled runtime : " + result.config.runtime.name());
    }
	public void convertFunction(Function function, Signatures signatures, boolean isCallback, final DeclarationsHolder out, final Identifier libraryClassName, int iConstructor) {
		if (result.config.functionsAccepter != null && !result.config.functionsAccepter.adapt(function))
			return;
		
		//if (function.findParentOfType(Template))
		String library = result.getLibrary(function);
		Identifier functionName = function.getName();
        boolean isMethod = function.getParentElement() instanceof Struct;
		if (functionName == null || isCallback) {
			if (function.getParentElement() instanceof FunctionSignature)
				functionName = ident(result.config.callbackInvokeMethodName);
			else
				return;
		}
		if (function.getParentElement() instanceof FriendDeclaration)
			return;

        String n = functionName.toString();
        if (n.contains("<") || n.startsWith("~"))
            return;
                
		functionName = result.typeConverter.getValidJavaMethodName(functionName);
		if (functionName == null)
			return;
        
        //if (functionName.equals("operator"))
        //    functionName

		String sig = function.computeSignature(false);

        DeclarationsHolder objOut = 
            result.config.reification &&
            !isCallback &&
            !isMethod ?
            new DeclarationsHolder() {

                @Override
                public void addDeclaration(Declaration d) {
                    out.addDeclaration(d);
                    if (d instanceof Function) {
                        Function f = (Function)d;
                        List<Arg> args = f.getArgs();
                        List<TypeRef> trs = new ArrayList<TypeRef>(2);
                        trs.add(f.getValueType());
                        if (!args.isEmpty())
                            trs.add(args.get(0).getValueType());

                        for (TypeRef tr: trs) {
                            if (tr instanceof SimpleTypeRef) {
                                Identifier id = ((SimpleTypeRef)tr).getName();
                                if (result.isFakePointer(id)) {
                                    result.addFunctionReifiableInFakePointer(id, libraryClassName, f);
                                }
                            }
                        }
                    }
                }

                @Override
                public List<Declaration> getDeclarations() {
                    return out.getDeclarations();
                }

            } :
            out
        ;
        
        try {
            convertFunction(function, signatures, isCallback, objOut, libraryClassName, sig, functionName, library, iConstructor);
        } catch (UnsupportedConversionException ex) {
            Declaration d = skipDeclaration(function);
            if (d != null) {
	            d.addToCommentBefore(ex.toString());
	            out.addDeclaration(d);
            }
        }
	}

    protected boolean isCPlusPlusFileName(String file) {
		if (file == null)
			return true;
			
		file = file.toLowerCase();
		return !file.endsWith(".c") && !file.endsWith(".m");
	}
	
	protected void collectParamComments(Function f) {
		for (Arg arg : f.getArgs()) {
			arg.moveAllCommentsBefore();
			TypeRef argType = arg.getValueType();
			if (argType != null) {
				if (!result.config.noComments) {
					argType.moveAllCommentsBefore();
					arg.addToCommentBefore(argType.getCommentBefore());
				}
				argType.stripDetails();
			}
			if (arg.getCommentBefore() != null) {
				if (!result.config.noComments)
					f.addToCommentBefore("@param " + arg.getName() + " " + Element.cleanComment(arg.getCommentBefore()));
				arg.stripDetails();
			}
		}
	}

	public void convertFunctions(List<Function> functions, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName) {
		if (functions != null) {
			//System.err.println("FUNCTIONS " + functions);
			for (Function function : functions) {
				convertFunction(function, signatures, false, out, libraryClassName, -1);
			}
		}
	}

	public Identifier getActualTaggedTypeName(TaggedTypeRef struct) {
		Identifier structName = null;
		Identifier tag = struct.getTag();
		if (tag == null || tag.isPlain() && tag.toString().startsWith("_")) {
			TypeDef parentDef = as(struct.getParentElement(), TypeDef.class);
			if (parentDef != null) {
				structName = new Identifier.SimpleIdentifier(JNAeratorUtils.findBestPlainStorageName(parentDef));
			} else if (tag != null) {
				String better = tag.toString().substring(1);
				Pair<TypeDef, Declarator> pair = result.typeDefs.get(better);
				if (pair != null && pair.getFirst().getValueType() != null && pair.getSecond() instanceof DirectDeclarator) {
					TypeRef tr = pair.getFirst().getValueType();
					DirectDeclarator dd = (DirectDeclarator) pair.getSecond();
					
					if (tr instanceof SimpleTypeRef) {
						if (tag.equals(((SimpleTypeRef)tr).getName()))
							structName = ident(dd.resolveName());
					} else if (tr instanceof TaggedTypeRef) {
						if (tag.equals(((TaggedTypeRef)tr).getTag()))
							structName = ident(dd.resolveName());
					}
				}
			}
		}
		if (structName == null || structName.toString().equals(""))
			structName = tag;
		return structName == null ? null : structName.clone();
	}
	public abstract Struct convertStruct(Struct struct, Signatures signatures, Identifier callerLibraryClass, String callerLibrary, boolean onlyFields) throws IOException;
    
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

	protected void outputConvertedStruct(Struct struct, Signatures signatures, DeclarationsHolder out, Identifier callerLibraryClass, String callerLibrary, boolean onlyFields) throws IOException {
		Struct structJavaClass = convertStruct(struct, signatures, callerLibraryClass, callerLibrary, onlyFields);
		if (structJavaClass == null)
			return;
		
		if (result.config.putTopStructsInSeparateFiles && struct.findParentOfType(Struct.class) == null) {
			String library = result.getLibrary(struct);
			Identifier javaPackage = result.getLibraryPackage(library);
			Identifier fullClassName = ident(javaPackage, structJavaClass.getTag().clone());
			
			if (result.config.runtime == JNAeratorConfig.Runtime.BridJ)
				structJavaClass.addAnnotation(new Annotation(org.bridj.ann.Library.class, expr(library)));
			structJavaClass.removeModifiers(ModifierType.Static);
			structJavaClass = result.notifyBeforeWritingClass(fullClassName, structJavaClass, signatures, library);
			if (structJavaClass != null) {
				PrintWriter pout = result.classOutputter.getClassSourceWriter(fullClassName.toString());
				result.printJavaClass(javaPackage, structJavaClass, pout);
				pout.close();
			}
		} else
			out.addDeclaration(decl(structJavaClass));
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

	public void convertStructs(List<Struct> structs, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName, String library) throws IOException {
		if (structs != null) {
			for (Struct struct : structs) {
				if (struct.findParentOfType(Struct.class) != null)
					continue;
					
                if (!result.config.genCPlusPlus && struct.getType().isCpp())
                    continue;

				outputConvertedStruct(struct, signatures, out, libraryClassName, library, false);
			}
		}
	}

	public abstract void convertVariablesDeclaration(VariablesDeclaration v, Signatures signatures, DeclarationsHolder out, int[] iChild, boolean isGlobal, Identifier holderName, Identifier callerLibraryClass, String callerLibrary);
	
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
			if (toCloneCommentsFrom != null ) {
				cl.importDetails(toCloneCommentsFrom, false);
				cl.moveAllCommentsBefore();
				cl.addToCommentBefore(getFileCommentContent(toCloneCommentsFrom));
			}
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
	protected boolean isField(VariablesDeclaration vd) {
		List<Modifier> mods = vd.getModifiers();
		if (vd.hasModifier(ModifierType.Final))
			return false;
		if (vd.getValueType() == null || vd.getValueType().toString().equals(VirtualTablePointer.class.getName()))
			return false;
		return true;
	}

	Statement throwIfArraySizeDifferent(String varAndFieldName) {
		return new Statement.If(
			expr(
				memberRef(varRef(varAndFieldName), MemberRefStyle.Dot, "length"), 
				BinaryOperator.IsDifferent,
				memberRef(memberRef(thisRef(), MemberRefStyle.Dot, varAndFieldName), MemberRefStyle.Dot, "length")
			),
			new Statement.Throw(new Expression.New(typeRef(IllegalArgumentException.class), expr("Wrong array size !"))),
			null
		);
	}
	void addConstructor(Struct s, Function f) {
		Identifier structName = getActualTaggedTypeName(s);
		
		f.setName(structName);
		s.addDeclaration(f);
	}
	
	String getFileCommentContent(File file, Element e) {
		if (file != null) {
			String path = result.config.relativizeFileForSourceComments(file.getAbsolutePath());
			String inCategoryStr = "";
			if (e instanceof Function) {
				Function fc = (Function)e;
				Struct parent;
				if (fc.getType() == Type.ObjCMethod && ((parent = as(fc.getParentElement(), Struct.class)) != null) && (parent.getCategoryName() != null)) {
					inCategoryStr = "from " + parent.getCategoryName() + " ";
				}
			}
			return "<i>" + inCategoryStr + "native declaration : " + path + (e == null || e.getElementLine() < 0 ? "" : ":" + e.getElementLine()) + "</i>";
		} else if (e != null && e.getElementLine() >= 0) {
			return "<i>native declaration : <input>:" + e.getElementLine() + "</i>";
		}
		return null;
	}
	String getFileCommentContent(Element e) {
		if (result.config.limitComments)
			return null;
		
		String f = Element.getFileOfAscendency(e);
		if (f == null && e != null && e.getElementLine() >= 0)
			return "<i>native declaration : line " + e.getElementLine() + "</i>";
		
		return f == null ? null : getFileCommentContent(new File(f), e);
	}
	

	public List<Define> reorderDefines(Collection<Define> defines) {
		List<Define> reordered = new ArrayList<Define>(defines.size());
		HashSet<Identifier> added = new HashSet<Identifier>(), all = new HashSet<Identifier>();
		
		
		Map<String, Pair<Define, Set<Identifier>>> pending = new HashMap<String, Pair<Define, Set<Identifier>>>();
		for (Define define : defines) {
			Set<Identifier> dependencies = new TreeSet<Identifier>();
			computeVariablesDependencies(define.getValue(), dependencies);
			all.add(ident(define.getName()));
			if (dependencies.isEmpty()) {
				reordered.add(define);
				added.add(ident(define.getName()));
			} else {
				pending.put(define.getName(), new Pair<Define, Set<Identifier>>(define, dependencies));
			}	
		}
		
        boolean progressed;
        do {
            progressed = false;
			for (Iterator<Map.Entry<String, Pair<Define, Set<Identifier>>>> it = pending.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Pair<Define, Set<Identifier>>> e = it.next(); 
				Set<Identifier> dependencies = e.getValue().getSecond();
				String name = e.getKey();
				boolean missesDep = false;
				for (Identifier dependency : dependencies) {
					if (!added.contains(dependency)) {
						missesDep = true;
						if (!all.contains(dependency)) {
							it.remove();
							all.remove(name);
						}
						
						break;
					}
				}
				if (missesDep)
					continue;
				
				it.remove();
				reordered.add(e.getValue().getFirst());
                added.add(ident(name));
                progressed = true;
			}
		} while (!pending.isEmpty() && progressed);
		
		return reordered;
	}
	public void computeVariablesDependencies(Element e, final Set<Identifier> names) {
		e.accept(new Scanner() {

			@Override
			public void visitVariableRef(VariableRef variableRef) {
				names.add(variableRef.getName());
			}
		});
	}
	
	protected String chooseJavaArgName(String name, int iArg, Set<String> names) {
		Identifier jan = result.typeConverter.getValidJavaArgumentName(ident(name));
		String baseArgName = jan == null ? null : jan.toString();
		int i = 1;
		if (baseArgName == null)
			baseArgName = "arg";
		
		String argName;
		do {
			argName = baseArgName + (i == 1 ? "" : i + "");
			i++;
		} while (names.contains(argName) || result.typeConverter.isJavaKeyword(argName));
		names.add(argName);
		return argName;
	}

}
