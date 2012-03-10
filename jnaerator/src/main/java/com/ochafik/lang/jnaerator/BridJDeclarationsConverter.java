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

import org.bridj.ann.Name;
import org.bridj.BridJ;
import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.StructObject;
import org.bridj.cpp.CPPObject;
import org.bridj.cpp.com.IUnknown;

import com.ochafik.lang.jnaerator.BridJTypeConversion.NL4JConversion;
//import org.bridj.structs.StructIO;
//import org.bridj.structs.Array;

import java.io.IOException;
import java.util.*;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.Enum;
import com.ochafik.lang.jnaerator.parser.Statement.Block;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;
import com.ochafik.lang.jnaerator.parser.Struct.MemberVisibility;
import com.ochafik.lang.jnaerator.parser.TypeRef.*;
import com.ochafik.lang.jnaerator.parser.Expression.*;
import com.ochafik.lang.jnaerator.parser.Function.Type;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder.ListWrapper;
import com.ochafik.lang.jnaerator.parser.Declarator.*;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.sun.jna.win32.StdCallLibrary;
import org.bridj.ann.Convention;
import org.bridj.objc.NSObject;

public class BridJDeclarationsConverter extends DeclarationsConverter {
	public BridJDeclarationsConverter(Result result) {
        super(result);
	}

    @Override
    public Struct convertCallback(FunctionSignature functionSignature, Signatures signatures, Identifier callerLibraryName) {
        Struct decl = super.convertCallback(functionSignature, signatures, callerLibraryName);
        if (decl != null) {
            decl.setParents(Arrays.asList((SimpleTypeRef)(
                FunctionSignature.Type.ObjCBlock.equals(functionSignature.getType()) ?
                    result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.ObjCBlock) :
                    (SimpleTypeRef)typeRef(ident(result.config.runtime.callbackClass, expr(typeRef(decl.getTag().clone()))))
            )));
            //addCallingConventionAnnotation(functionSignature.getFunction(), decl);
        }
        
        return decl;
    }
    
	public void convertEnum(Enum e, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName) {
		if (e.isForwardDeclaration())
			return;
		
		Identifier enumName = getActualTaggedTypeName(e);
        List<EnumItemResult> results = getEnumValuesAndCommentsByName(e, signatures, libraryClassName);
        
        boolean hasEnumClass = false;
        
        if (enumName != null && enumName.resolveLastSimpleIdentifier().getName() != null) {
            if (!signatures.classSignatures.add(enumName))
                return;

            signatures = new Signatures();

            Enum en = new Enum();
            en.setType(Enum.Type.Java);
            en.setTag(enumName.clone());
            en.addModifiers(ModifierType.Public);
            out.addDeclaration(new TaggedTypeRefDeclaration(en));
            Struct body = new Struct();
            en.setBody(body);
            boolean hasValidItem = false;
            for (EnumItemResult er : results) {
                if (er.errorElement != null) {
                    out.addDeclaration(er.errorElement);
                    continue;
                }
                Enum.EnumItem item = new Enum.EnumItem(er.originalItem.getName(), er.value);
                en.addItem(item);
                hasValidItem = true;
                if (!result.config.noComments)
                    if (item != null && hasEnumClass) {
                        String c = item.getCommentBefore();
                        item.setCommentBefore(er.originalItem.getCommentBefore());
                        item.addToCommentBefore(c);
                    }
            }
            if (hasValidItem) {
                en.addInterface(ident(IntValuedEnum.class, expr(typeRef(enumName.clone()))));
                String valueArgName = "value";
                body.addDeclaration(new Function(Type.JavaMethod, enumName.clone(), null, new Arg(valueArgName, typeRef(Long.TYPE))).setBody(block(
                    stat(expr(memberRef(thisRef(), MemberRefStyle.Dot, valueArgName), AssignmentOperator.Equal, varRef(valueArgName)))
                )));
                body.addDeclaration(new VariablesDeclaration(typeRef(Long.TYPE), new DirectDeclarator(valueArgName)).addModifiers(ModifierType.Public, ModifierType.Final));
                body.addDeclaration(new Function(Type.JavaMethod, ident(valueArgName), typeRef(Long.TYPE)).setBody(block(
                    new Statement.Return(memberRef(thisRef(), MemberRefStyle.Dot, valueArgName))
                )).addModifiers(ModifierType.Public));


                body.addDeclaration(new Function(Type.JavaMethod, ident("iterator"), typeRef(ident(Iterator.class, expr(typeRef(enumName.clone()))))).setBody(block(
                    new Statement.Return(
                        methodCall(
                            methodCall(
                                expr(typeRef(Collections.class)),
                                MemberRefStyle.Dot,
                                "singleton",
                                thisRef()
                            ),
                            MemberRefStyle.Dot,
                            "iterator"
                        )
                    )
                )).addModifiers(ModifierType.Public));

                body.addDeclaration(new Function(Type.JavaMethod, ident("fromValue"), typeRef(ident(IntValuedEnum.class, expr(typeRef(enumName.clone())))), new Arg(valueArgName, typeRef(Integer.TYPE))).setBody(block(
                    new Statement.Return(
                        methodCall(
                            expr(typeRef(FlagSet.class)),
                            MemberRefStyle.Dot,
                            "fromValue",
                            varRef(valueArgName),
                            methodCall(
                                "values"
                            )
                        )
                    )
                )).addModifiers(ModifierType.Public, ModifierType.Static));
            }
        } else {
            outputEnumItemsAsConstants(results, out, signatures, libraryClassName, hasEnumClass);
        }
	}

    void addCallingConventionAnnotation(Function originalFunction, ModifiableElement target) {
        Convention.Style cc = null;
        if (originalFunction.hasModifier(ModifierType.__stdcall))
            cc = Convention.Style.StdCall;
        else if (originalFunction.hasModifier(ModifierType.__fastcall))
            cc = Convention.Style.FastCall;
        else if (originalFunction.hasModifier(ModifierType.__thiscall))
            cc = Convention.Style.ThisCall;
        else if (originalFunction.hasModifier(ModifierType.__pascal))
            cc = Convention.Style.Pascal;
        
        if (cc != null) {
            target.addAnnotation(new Annotation(typeRef(ident(Convention.class, enumRef(cc)))));
        }
    }
    
    @Override
    public void convertFunction(Function function, Signatures signatures, boolean isCallback, DeclarationsHolder out, Identifier libraryClassName, String sig, Identifier functionName, String library, int iConstructor) throws UnsupportedConversionException {
		Element parent = function.getParentElement();
    	MemberVisibility visibility = function.getVisibility();
    	boolean isPublic = visibility == MemberVisibility.Public || function.hasModifier(ModifierType.Public);
    	boolean isPrivate = visibility == MemberVisibility.Private || function.hasModifier(ModifierType.Private);
    	boolean isProtected = visibility == MemberVisibility.Protected || function.hasModifier(ModifierType.Protected);
		boolean isInStruct = parent instanceof Struct;
    	if (isInStruct && result.config.skipPrivateMembers && (isPrivate || !isPublic && !isProtected))
        	return;
        boolean isStatic = function.hasModifier(ModifierType.Static);
		boolean isConstructor = iConstructor != -1;
            
        Function nativeMethod = new Function(Type.JavaMethod, ident(functionName), null);
        
        nativeMethod.addModifiers(
            isProtected ? ModifierType.Protected : ModifierType.Public, 
            isStatic || !isCallback && !isInStruct ? ModifierType.Static : null
        );
        if (result.config.synchronizedMethods && !isCallback)
			nativeMethod.addModifiers(ModifierType.Synchronized);
		
        addCallingConventionAnnotation(function, nativeMethod);
        
		if (function.getName() != null && !functionName.toString().equals(function.getName().toString()) && !isCallback) {
        	TypeRef mgc = result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Name);
			if (mgc != null) {
				nativeMethod.addAnnotation(new Annotation(mgc, "(\"" + function.getName() + "\")"));
			}
		}
        if (!isConstructor) {
            NL4JConversion retType = ((BridJTypeConversion)result.typeConverter).convertTypeToNL4J(function.getValueType(), libraryClassName, null, null, -1, -1);
            retType.annotateTypedType(nativeMethod);//.getTypedTypeRef())));
            nativeMethod.setValueType(retType.typeRef);
        }

        Map<String, NL4JConversion> argTypes = new LinkedHashMap<String, NL4JConversion>();

        boolean isObjectiveC = function.getType() == Type.ObjCMethod;
        int iArg = 1;
        Set<String> argNames = new TreeSet<String>();

        List<Expression> superConstructorArgs = null;
        if (isConstructor) {
            superConstructorArgs = new ArrayList<Expression>();
            superConstructorArgs.add(cast(typeRef(Void.class), nullExpr()));
            superConstructorArgs.add(expr(iConstructor));
        }
        
        for (Arg arg : function.getArgs()) {

            if (arg.isVarArg()) {
            		assert arg.getValueType() == null;
                // TODO choose vaname dynamically !
                Identifier vaType = ident(isObjectiveC ? NSObject.class : Object.class);
                String argName = chooseJavaArgName("varargs", iArg, argNames);
                nativeMethod.addArg(new Arg(argName, typeRef(vaType.clone()))).setVarArg(true);
            } else {
                String argName = chooseJavaArgName(arg.getName(), iArg, argNames);
                NL4JConversion argType = ((BridJTypeConversion)result.typeConverter).convertTypeToNL4J(arg.getValueType(), libraryClassName, null, null, -1, -1);
                argTypes.put(argName, argType);
                nativeMethod.addArg(argType.annotateTypedType(new Arg(argName, argType.typeRef)));//.getTypedTypeRef())));
                
                if (isConstructor) {
                    superConstructorArgs.add(varRef(argName));
                }
            }
            iArg++;
        }
        String natSig = nativeMethod.computeSignature(false);

        Identifier javaMethodName = signatures.findNextMethodName(natSig, functionName);
        if (!javaMethodName.equals(functionName)) {
            nativeMethod.setName(javaMethodName);
        }
        if (!isCallback && !javaMethodName.equals(functionName))
            nativeMethod.addAnnotation(new Annotation(Name.class, expr(functionName.toString())));

        Block convertedBody = null;
        if (isConstructor) {
            convertedBody = block(stat(methodCall("super", superConstructorArgs.toArray(new Expression[superConstructorArgs.size()]))));
        } else if (result.config.convertBodies && function.getBody() != null)
        {
            try {
                Pair<Element, List<Declaration>> bodyAndExtraDeclarations = result.bridjer.convertToJava(function.getBody(), libraryClassName);
                convertedBody = (Block)bodyAndExtraDeclarations.getFirst();
                for (Declaration d : bodyAndExtraDeclarations.getSecond())
                    out.addDeclaration(d);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
                nativeMethod.addToCommentBefore("TRANSLATION OF BODY FAILED: " + ex);
            }
        }
        
        if (convertedBody == null)
            nativeMethod.addModifiers(isCallback ? ModifierType.Abstract : ModifierType.Native);
        else
            nativeMethod.setBody(convertedBody);
        
        out.addDeclaration(nativeMethod);
    }
	
    public Struct convertStruct(Struct struct, Signatures signatures, Identifier callerLibraryClass, String callerLibrary, boolean onlyFields) throws IOException {
		Identifier structName = getActualTaggedTypeName(struct);
		if (structName == null)
			return null;

		//if (structName.toString().contains("MonoObject"))
		//	structName.toString();

		if (struct.isForwardDeclaration())// && !result.structsByName.get(structName).isForwardDeclaration())
			return null;

		if (!signatures.classSignatures.add(structName))
			return null;

		boolean isUnion = struct.getType() == Struct.Type.CUnion;
		boolean inheritsFromStruct = false;
		Identifier baseClass = null;
		int parentFieldsCount = 0;
		List<String> preComments = new ArrayList<String>();
		for (SimpleTypeRef parentName : struct.getParents()) {
			Struct parent = result.structsByName.get(parentName.getName());
			if (parent == null) {
				// TODO report error
				continue;
			}
			try {
				parentFieldsCount += countFieldsInStruct(parent);
			} catch (UnsupportedConversionException ex) {
				preComments.add("Error: " + ex);
			}
			baseClass = result.getTaggedTypeIdentifierInJava(parent);
			if (baseClass != null) {
				inheritsFromStruct = true;
				break; // TODO handle multiple and virtual inheritage
			}
		}
		boolean hasMemberFunctions = false;
		for (Declaration d : struct.getDeclarations()) {
			if (d instanceof Function) {
				hasMemberFunctions = true;
				break;
			}
		}
		Constant uuid = (Constant)struct.getModifierValue(ModifierType.UUID);
        if (baseClass == null) {
			switch (struct.getType()) {
			case CStruct:
			case CUnion:
				if (!hasMemberFunctions) {
					baseClass = ident(StructObject.class);
					break;
				}
			case CPPClass:
				baseClass = ident(uuid == null ? CPPObject.class : IUnknown.class);
				result.hasCPlusPlus = true;
				break;
			default:
				throw new UnsupportedOperationException();
			}
		}
		Struct structJavaClass = publicStaticClass(structName, baseClass, Struct.Type.JavaClass, struct);
        //if (result.config.microsoftCOM) {
        if (uuid != null) {
            structJavaClass.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.IID), uuid));
        }
		structJavaClass.addToCommentBefore(preComments);
		//System.out.println("parentFieldsCount(structName = " + structName + ") = " + parentFieldsCount);
		final int iChild[] = new int[] { parentFieldsCount };

		//cl.addDeclaration(new EmptyDeclaration())
		Signatures childSignatures = new Signatures();

		/*if (isVirtual(struct) && !onlyFields) {
			String vptrName = DEFAULT_VPTR_NAME;
			VariablesDeclaration vptr = new VariablesDeclaration(typeRef(VirtualTablePointer.class), new Declarator.DirectDeclarator(vptrName));
			vptr.addModifiers(ModifierType.Public);
			structJavaClass.addDeclaration(vptr);
			childSignatures.variablesSignatures.add(vptrName);
			// TODO add vptr grabber to constructor !
		}*/

        //    private static StructIO<MyStruct> io = StructIO.getInstance(MyStruct.class);
        
        Function defaultConstructor = new Function(Type.JavaMethod, ident(structName), null).setBody(block(stat(methodCall("super")))).addModifiers(ModifierType.Public);
        if (childSignatures.methodsSignatures.add(defaultConstructor.computeSignature(false)))
            structJavaClass.addDeclaration(defaultConstructor);
        
        //todo remove this :
		//String ptrName = "pointer";
		//structJavaClass.addDeclaration(new Function(Type.JavaMethod, ident(structName), null, new Arg(ptrName, typeRef(result.config.runtime.pointerClass))).setBody(block(stat(methodCall("super", varRef(ptrName))))).addModifiers(ModifierType.Public));

        if (isUnion)
            structJavaClass.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Union)));

        int iVirtual = 0, iConstructor = 0;
		//List<Declaration> children = new ArrayList<Declaration>();
		for (Declaration d : struct.getDeclarations()) {
            //if (isUnion)
            //    iChild[0] = 0;

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
				} else if ((result.config.runtime == JNAeratorConfig.Runtime.BridJ || result.config.genCPlusPlus) && d instanceof Function) {
					Function f = (Function) d;
                    
					boolean isVirtual = f.hasModifier(ModifierType.Virtual);
                    boolean isConstructor = f.getName().equals(structName) && (f.getValueType() == null || f.getValueType().toString().equals("void"));
                    if (isConstructor && f.getArgs().isEmpty())
                        continue; // default constructor was already generated
                    
					String library = result.getLibrary(struct);
					if (library == null)
						continue;
					List<Declaration> decls = new ArrayList<Declaration>();
					convertFunction(f, childSignatures, false, new ListWrapper(decls), callerLibraryClass, isConstructor ? iConstructor : -1);
                    for (Declaration md : decls) {
						if (!(md instanceof Function))
							continue;
						Function method = (Function) md;
                        boolean commentOut = false;
						if (isVirtual)
							method.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Virtual), expr(iVirtual)));
                        else if (method.getValueType() == null) {
                            method.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Constructor), expr(iConstructor)));
                            isConstructor = true;
                        }
                        if (method.getName().toString().equals("operator"))
                            commentOut = true;
                        
                        if (commentOut)
                            structJavaClass.addDeclaration(new EmptyDeclaration(method.toString()));
                        else
                            structJavaClass.addDeclaration(method);
					}
					if (isVirtual)
						iVirtual++;
                    if (isConstructor)
                        iConstructor++;
				}
			}
		}
		return structJavaClass;
	}
	Map<Identifier, Boolean> structsVirtuality = new HashMap<Identifier, Boolean>();
	public boolean isVirtual(Struct struct) {
		Identifier name = getActualTaggedTypeName(struct);
		Boolean bVirtual = structsVirtuality.get(name);
		if (bVirtual == null) {
			boolean hasVirtualParent = false, hasVirtualMembers = false;
			for (SimpleTypeRef parentName : struct.getParents()) {
				Struct parentStruct = result.structsByName.get(parentName.getName());
				if (parentStruct == null) {
					if (result.config.verbose)
						System.out.println("Failed to resolve parent '" + parentName + "' for struct '" + name + "'");
					continue;
				}
				if (isVirtual(parentStruct)) {
					hasVirtualParent = true;
					break;
				}
			}

			for (Declaration mb : struct.getDeclarations()) {
				if (mb.hasModifier(ModifierType.Virtual)) {
					hasVirtualMembers = true;
					break;
				}
			}
			bVirtual = hasVirtualMembers && !hasVirtualParent;
			structsVirtuality.put(name, bVirtual);
		}
		return bVirtual;
	}

    protected String ioVarName = "io", ioStaticVarName = "IO";
	public List<Declaration> convertVariablesDeclarationToBridJ(String name, TypeRef mutatedType, int[] iChild, int bits, boolean isGlobal, Identifier holderName, Identifier callerLibraryName, String callerLibrary, Element... toImportDetailsFrom) throws UnsupportedConversionException {
		name = result.typeConverter.getValidJavaArgumentName(ident(name)).toString();
		//convertVariablesDeclaration(name, mutatedType, out, iChild, callerLibraryName);

		//Expression initVal = null;
		int fieldIndex = iChild[0];
		//convertTypeToNL4J(TypeRef valueType, Identifier libraryClassName, Expression structPeerExpr, Expression structIOExpr, Expression valueExpr, int fieldIndex, int bits) throws UnsupportedConversionException {
			
        NL4JConversion conv = ((BridJTypeConversion)result.typeConverter).convertTypeToNL4J(
    		mutatedType, 
    		callerLibraryName,
    		thisField("io"),
    		varRef(name),
    		fieldIndex,
    		bits
		);

        if (conv == null) {
			throw new UnsupportedConversionException(mutatedType, "failed to convert type to Java");
		} else if ("void".equals(String.valueOf(conv.typeRef))) {
			throw new UnsupportedConversionException(mutatedType, "void type !");
			//out.add(new EmptyDeclaration("SKIPPED:", v.formatComments("", true, true, false), v.toString()));
		}

        Function convDecl = new Function();
        conv.annotateTypedType(convDecl);
        convDecl.setType(Type.JavaMethod);
		convDecl.addModifiers(ModifierType.Public);

		if (conv.arrayLengths != null)
            convDecl.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Length), "({" + StringUtils.implode(conv.arrayLengths, ", ") + "})"));
        if (conv.bits != null)
            convDecl.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Bits), conv.bits));
        if (conv.byValue)
            convDecl.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.ByValue)));

        for (Element e : toImportDetailsFrom)
            convDecl.importDetails(e, false);
        convDecl.importDetails(mutatedType, true);
        //convDecl.importDetails(javaType, true);

//			convDecl.importDetails(v, false);
//			convDecl.importDetails(vs, false);
//			convDecl.importDetails(valueType, false);
//			valueType.stripDetails();
        convDecl.moveAllCommentsBefore();

        convDecl.setName(ident(name));

        if (!isGlobal)
            convDecl.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Field), expr(fieldIndex)));
        convDecl.setValueType(conv.typeRef);

        TypeRef javaType = convDecl.getValueType();
        String pointerGetSetMethodSuffix = StringUtils.capitalize(javaType.toString());

        Expression getGlobalPointerExpr = null;
        if (isGlobal) {
            getGlobalPointerExpr = methodCall(methodCall(methodCall(expr(typeRef(BridJ.class)), "getNativeLibrary", expr(callerLibrary)), "getSymbolPointer", expr(name)), "as", result.typeConverter.typeLiteral(javaType.clone()));
        }
        List<Declaration> out = new ArrayList<Declaration>();
        if (conv.getExpr != null) {
	        Function getter = convDecl.clone();
            if (isGlobal) {
                getter.setBody(block(
                    tryRethrow(new Statement.Return(cast(javaType.clone(), methodCall(getGlobalPointerExpr, "get"))))
                ));
            } else {
                getter.setBody(block(
                    new Statement.Return(conv.getExpr)
                ));
            }
	        out.add(getter);
        }
        
        if (!conv.readOnly && conv.setExpr != null) {
            Function setter = convDecl.clone();
            setter.setValueType(typeRef(holderName.clone()));//Void.TYPE));
            setter.addArg(new Arg(name, javaType));
            //setter.addModifiers(ModifierType.Native);
            if (isGlobal) {
                setter.setBody(block(
                    tryRethrow(block(
                        stat(methodCall(getGlobalPointerExpr, "set", varRef(name))),
                        new Statement.Return(thisRef())
                    ))
                ));
            } else {
                setter.setBody(block(
                    stat(conv.setExpr),
                    new Statement.Return(thisRef())
                ));
            }
            out.add(setter);
            
            if (result.config.scalaStructSetters) {
                setter = new Function();
                setter.setType(Type.JavaMethod);
                setter.setName(ident(name + "_$eq"));
                setter.setValueType(javaType.clone());
                setter.addArg(new Arg(name, javaType.clone()));
                setter.addModifiers(ModifierType.Public, ModifierType.Final);
                setter.setBody(block(
                    stat(methodCall(name, varRef(name))),
                    new Statement.Return(varRef(name))
                ));
                out.add(setter);
            }
        }
        return out;
    }
	public void convertVariablesDeclaration(VariablesDeclaration v, Signatures signatures, DeclarationsHolder out, int[] iChild, boolean isGlobal, Identifier holderName, Identifier callerLibraryClass, String callerLibrary) {
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
					mutatedType = (TypeRef)vs.mutateType(valueType);
					vs = new DirectDeclarator(vs.resolveName());
				}
				Declarator d = v.getDeclarators().get(0);
                List<Declaration> vds = convertVariablesDeclarationToBridJ(name, mutatedType, iChild, d.getBits(), isGlobal, holderName, callerLibraryClass, callerLibrary, v, vs);
                if (d.getBits() > 0)
					for (Declaration vd : vds)
                        vd.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Bits), expr(d.getBits())));
				
                for (Declaration vd : vds) {
                    if (vd instanceof Function) {
                        if (!signatures.methodsSignatures.add(((Function)vd).computeSignature(false)))
                            continue;
                    }
                    
                	vd.importDetails(mutatedType, true);
                	vd.moveAllCommentsBefore();
        			if (!(mutatedType instanceof Primitive) && !result.config.noComments)
                        vd.addToCommentBefore("C type : " + mutatedType);
                    
                    out.addDeclaration(vd);
                }
				//}
				iChild[0]++;
			}
		} catch (Throwable e) {
            if (!(e instanceof UnsupportedConversionException))
                e.printStackTrace();
			if (!result.config.limitComments)
				out.addDeclaration(new EmptyDeclaration(e.toString()));
		}
    }
    int nextAnonymousFieldId;
	
    @Override
    protected void configureCallbackStruct(Struct callbackStruct) {
        callbackStruct.setType(Struct.Type.JavaClass);
        callbackStruct.addModifiers(ModifierType.Public, ModifierType.Static, ModifierType.Abstract);
    }

}
