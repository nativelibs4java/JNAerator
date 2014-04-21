/*
 Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
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
import com.ochafik.lang.jnaerator.TypeConversion.ConvType;
import com.ochafik.lang.jnaerator.TypeConversion.EnumItemResult;
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
import com.ochafik.lang.jnaerator.parser.Function.SignatureType;
import org.bridj.*;
import org.bridj.ann.Convention;
import org.bridj.ann.Optional;
import org.bridj.cpp.CPPRuntime;
import org.bridj.objc.NSObject;

public class BridJDeclarationsConverter extends DeclarationsConverter {

    public BridJDeclarationsConverter(Result result) {
        super(result);
    }

    public void annotateActualName(ModifiableElement e, Identifier name) {
        e.addAnnotation(new Annotation(org.bridj.ann.Name.class, expr(name.toString())));
    }

    @Override
    public Struct convertCallback(FunctionSignature functionSignature, Signatures signatures, Identifier callerLibraryName) {
        Struct decl = super.convertCallback(functionSignature, signatures, callerLibraryName);
        if (decl != null) {
            decl.setParents(Arrays.asList((SimpleTypeRef) (FunctionSignature.Type.ObjCBlock.equals(functionSignature.getType())
                    ? result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.ObjCBlock)
                    : (SimpleTypeRef) typeRef(ident(result.config.runtime.callbackClass, expr(typeRef(decl.getTag().clone())))))));
            //addCallingConventionAnnotation(functionSignature.getFunction(), decl);
        }

        return decl;
    }

    @Override
    public void convertEnum(Enum e, Signatures signatures, DeclarationsHolder out, Identifier libraryClassName) {
        if (e.isForwardDeclaration()) {
            return;
        }

        Identifier rawEnumName = getActualTaggedTypeName(e);
        Map<String, EnumItemResult> results = result.typeConverter.getEnumValuesAndCommentsByName(e, libraryClassName);

        boolean hasEnumClass = false;

        if (rawEnumName != null && rawEnumName.resolveLastSimpleIdentifier().getName() != null) {
            hasEnumClass = true;

            Identifier enumName = result.typeConverter.getValidJavaIdentifier(rawEnumName);
            if (!signatures.addClass(enumName)) {
                return;
            }

            signatures = new Signatures();

            Enum en = new Enum();
            if (result.config.forceNames || !rawEnumName.equals(enumName))
                annotateActualName(en, rawEnumName);
            addParentNamespaceAnnotation(en, e.getParentNamespace());
            if (!result.config.noComments) {
                en.importComments(e, "enum values", getFileCommentContent(e));
            }

            en.setType(Enum.Type.Java);
            en.setTag(enumName.clone());
            en.addModifiers(ModifierType.Public);
            out.addDeclaration(new TaggedTypeRefDeclaration(en));
            Struct body = new Struct();
            en.setBody(body);
            boolean hasValidItem = false;
            for (EnumItemResult er : results.values()) {
                if (er.errorElement != null) {
                    out.addDeclaration(er.errorElement);
                    continue;
                }
                String itemName = result.typeConverter.getValidJavaIdentifierString(ident(er.originalItem.getName()));
                Enum.EnumItem item = new Enum.EnumItem(itemName, er.convertedValue);
                en.addItem(item);
                hasValidItem = true;
                if (!result.config.noComments) {
                    if (item != null) {// && hasEnumClass) {
                        item.importComments(er.originalItem);
                    }
                }
            }
            if (hasValidItem) {
                en.addInterface(ident(IntValuedEnum.class, expr(typeRef(enumName.clone()))));
                String valueArgName = "value";
                body.addDeclaration(new Function(Type.JavaMethod, enumName.clone(), null, new Arg(valueArgName, typeRef(Long.TYPE))).setBody(block(
                        stat(expr(memberRef(thisRef(), MemberRefStyle.Dot, valueArgName), AssignmentOperator.Equal, varRef(valueArgName))))));
                body.addDeclaration(new VariablesDeclaration(typeRef(Long.TYPE), new DirectDeclarator(valueArgName)).addModifiers(ModifierType.Public, ModifierType.Final));
                body.addDeclaration(new Function(Type.JavaMethod, ident(valueArgName), typeRef(Long.TYPE)).setBody(block(
                        new Statement.Return(memberRef(thisRef(), MemberRefStyle.Dot, valueArgName)))).addModifiers(ModifierType.Public));


                body.addDeclaration(new Function(Type.JavaMethod, ident("iterator"), typeRef(ident(Iterator.class, expr(typeRef(enumName.clone()))))).setBody(block(
                        new Statement.Return(
                        methodCall(
                        methodCall(
                        expr(typeRef(Collections.class)),
                        MemberRefStyle.Dot,
                        "singleton",
                        thisRef()),
                        MemberRefStyle.Dot,
                        "iterator")))).addModifiers(ModifierType.Public));

                body.addDeclaration(new Function(Type.JavaMethod, ident("fromValue"), typeRef(ident(IntValuedEnum.class, expr(typeRef(enumName.clone())))), new Arg(valueArgName, typeRef(Integer.TYPE))).setBody(block(
                        new Statement.Return(
                        methodCall(
                        expr(typeRef(FlagSet.class)),
                        MemberRefStyle.Dot,
                        "fromValue",
                        varRef(valueArgName),
                        methodCall(
                        "values"))))).addModifiers(ModifierType.Public, ModifierType.Static));
            }
        } else {
            outputEnumItemsAsConstants(results, out, signatures, libraryClassName, hasEnumClass);
        }
    }

    void addCallingConventionAnnotation(Function originalFunction, ModifiableElement target) {
        Convention.Style cc = null;
        if (originalFunction.hasModifier(ModifierType.__stdcall)) {
            cc = Convention.Style.StdCall;
        } else if (originalFunction.hasModifier(ModifierType.__fastcall)) {
            cc = Convention.Style.FastCall;
        } else if (originalFunction.hasModifier(ModifierType.__thiscall)) {
            cc = Convention.Style.ThisCall;
        } else if (originalFunction.hasModifier(ModifierType.__pascal)) {
            cc = Convention.Style.Pascal;
        }

        if (cc != null) {
            target.addAnnotation(new Annotation(typeRef(Convention.class), enumRef(cc)));
        }
    }

    @Override
    public void convertFunction(Function function, Signatures signatures, boolean isCallback, 
            DeclarationsHolder declarations, DeclarationsHolder implementations, Identifier libraryClassName, 
            String sig, Identifier functionName, String library, int iConstructor) throws UnsupportedConversionException {
        assert implementations != null;
        boolean extractingDeclarations = declarations != null && implementations != declarations;
        Element parent = function.getParentElement();
        MemberVisibility visibility = function.getVisibility();
        boolean isPublic = visibility == MemberVisibility.Public || function.hasModifier(ModifierType.Public);
        boolean isPrivate = visibility == MemberVisibility.Private || function.hasModifier(ModifierType.Private);
        boolean isProtected = visibility == MemberVisibility.Protected || function.hasModifier(ModifierType.Protected);
        boolean isInStruct = parent instanceof Struct;
        if (isInStruct && result.config.skipPrivateMembers && (isPrivate || !isPublic && !isProtected)) {
            return;
        }
        boolean isStatic = function.hasModifier(ModifierType.Static);
        boolean isConstructor = iConstructor != -1;

        Function nativeMethod = new Function(Type.JavaMethod, ident(functionName), null);

        if (result.config.synchronizedMethods && !isCallback) {
            nativeMethod.addModifiers(ModifierType.Synchronized);
        }

        addCallingConventionAnnotation(function, nativeMethod);

        if (function.getName() != null && !isCallback &&
            (!functionName.toString().equals(function.getName().toString()) || result.config.forceNames))
        {
            annotateActualName(nativeMethod, function.getName());
        }

        Function rawMethod = nativeMethod.clone();

        //Map<String, NL4JConversion> argTypes = new LinkedHashMap<String, NL4JConversion>();

        boolean isObjectiveC = function.getType() == Type.ObjCMethod;
        int iArg = 1;
        Set<String> argNames = new TreeSet<String>();

        List<Expression> superConstructorArgs = null;
        if (isConstructor) {
            superConstructorArgs = new ArrayList<Expression>();
            superConstructorArgs.add(cast(typeRef(Void.class), nullExpr()));
            superConstructorArgs.add(expr(iConstructor));
        }

        Identifier varArgType = null;
        String varArgName = null;
        NL4JConversion returnType = null;
        List<NL4JConversion> paramTypes = new ArrayList<NL4JConversion>();
        List<String> paramNames = new ArrayList<String>();
        if (!isConstructor) {
            returnType = ((BridJTypeConversion) result.typeConverter).convertTypeToNL4J(function.getValueType(), libraryClassName, null, null, -1, -1);
        }

        for (Arg arg : function.getArgs()) {
            String paramName;
            if (isVarArgs(arg)) {
//                assert arg.getValueType() == null;
                paramName = varArgName = chooseJavaArgName(arg.getName() == null ? "varargs" : arg.getName(), iArg, argNames);
                varArgType = ident(isObjectiveC ? NSObject.class : Object.class);
            } else {
                paramName = chooseJavaArgName(arg.getName(), iArg, argNames);
                paramTypes.add(((BridJTypeConversion) result.typeConverter).convertTypeToNL4J(arg.getValueType(), libraryClassName, null, null, -1, -1));
            }
            paramNames.add(paramName);
            if (isConstructor) {
                superConstructorArgs.add(varRef(paramName));
            }
            iArg++;
        }

        fillIn(signatures, functionName, nativeMethod, returnType, paramTypes, paramNames, varArgType, varArgName, isCallback, false);

        List<Declaration> extractibleDecls = new ArrayList<Declaration>();
        Block convertedBody = null;
        if (isConstructor) {
            convertedBody = block(stat(methodCall("super", superConstructorArgs.toArray(new Expression[superConstructorArgs.size()]))));
        } else if (result.config.convertBodies && function.getBody() != null) {
            try {
                Pair<Element, List<Declaration>> bodyAndExtraDeclarations = result.bridjer.convertToJava(function.getBody(), libraryClassName);
                convertedBody = (Block) bodyAndExtraDeclarations.getFirst();
                for (Declaration d : bodyAndExtraDeclarations.getSecond()) {
                    implementations.addDeclaration(d);
//                    extractibleDecls.add(d);
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
                nativeMethod.addToCommentBefore("TRANSLATION OF BODY FAILED: " + ex);
            }
        }

        if (!result.config.noComments) {
            nativeMethod.importComments(function, isCallback ? null : getFileCommentContent(function));
        }

        boolean generateStaticMethod = (isStatic || !isCallback && !isInStruct) &&
                (declarations == null || implementations == declarations);
        
        nativeMethod.addModifiers(
                isProtected && !extractingDeclarations && !result.config.publicRawBindings ?
                		ModifierType.Protected : ModifierType.Public,
                generateStaticMethod ? ModifierType.Static : null);
        
        boolean isOptional = isOptionalFunction(function.getName() + "");
        
        implementations.addDeclaration(nativeMethod);
        
        boolean forwardedToRaw = false;
        if (convertedBody == null) {
            if (result.config.genRawBindings) {// && !isCallback) {
//                Function rawMethod = nativeMethod.clone();
                rawMethod.setArgs(Collections.EMPTY_LIST);
                fillIn(signatures, functionName, rawMethod, returnType, paramTypes, paramNames, varArgType, varArgName, isCallback, true);
                rawMethod.addModifiers(
                    extractingDeclarations || isCallback || result.config.publicRawBindings ?
                    		ModifierType.Public : ModifierType.Protected, 
                    isCallback ? ModifierType.Abstract : ModifierType.Native);
                if (generateStaticMethod) {
                    rawMethod.addModifiers(ModifierType.Static);
                }

                if (!nativeMethod.computeSignature(SignatureType.ArgsAndRet).equals(rawMethod.computeSignature(SignatureType.ArgsAndRet))) {
                    implementations.addDeclaration(rawMethod);
                    extractibleDecls.add(rawMethod);
                    
                    if (isOptional && !isCallback) {
                        rawMethod.addAnnotation(new Annotation(typeRef(Optional.class)));
                    }

                    List<Expression> objectToRawFollowedArgs = new ArrayList<Expression>();
                    List<Expression> rawToObjectFollowedArgs = new ArrayList<Expression>();
                    for (int i = 0, n = paramTypes.size(); i < n; i++) {
                        NL4JConversion paramType = paramTypes.get(i);
                        String paramName = paramNames.get(i);

                        switch (paramType.type) {
                            case Pointer:
                                objectToRawFollowedArgs.add(createGetPeerExpression(varRef(paramName)));
                                rawToObjectFollowedArgs.add(createPointerToAddressExpression(paramType, varRef(paramName)));
                                break;
                            case Enum:
                                objectToRawFollowedArgs.add(createGetEnumValueExpression(varRef(paramName)));
                                rawToObjectFollowedArgs.add(createEnumExpression(paramType, varRef(paramName)));
                                break;
//                            case NativeSize:
//                            case NativeLong:
//                                followedArg = methodCall(varRef(paramName), "longValue");
//                                break;
                            default:
                                objectToRawFollowedArgs.add(varRef(paramName));
                                rawToObjectFollowedArgs.add(varRef(paramName));
                                break;
                        }
                    }
                    if (varArgType != null) {
                        objectToRawFollowedArgs.add(varRef(varArgName));
                        rawToObjectFollowedArgs.add(varRef(varArgName));
                    }

                    Expression objectToRawFollowedCall =
                        methodCall(rawMethod.getName().toString(), objectToRawFollowedArgs.toArray(new Expression[objectToRawFollowedArgs.size()]));
                    
                    Expression rawToObjectFollowedCall =
                        methodCall(nativeMethod.getName().toString(), rawToObjectFollowedArgs.toArray(new Expression[rawToObjectFollowedArgs.size()]));
                    
                    boolean isVoid = "void".equals(String.valueOf(nativeMethod.getValueType()));
                    if (isVoid) {
                        nativeMethod.setBody(block(stat(objectToRawFollowedCall)));
                        if (isCallback) {
                            rawMethod.removeModifiers(ModifierType.Abstract);
                            rawMethod.setBody(block(stat(rawToObjectFollowedCall)));
                        }
                    } else {
                        switch (returnType.type) {
                            case Pointer:
                                objectToRawFollowedCall = createPointerToAddressExpression(returnType, objectToRawFollowedCall);
                                rawToObjectFollowedCall = createGetPeerExpression(rawToObjectFollowedCall);
//                                if (returnType.isTypedPointer) {
//                                    objectToRawFollowedCall = new New(nativeMethod.getValueType(), objectToRawFollowedCall);
//                                } else {
//                                    Expression ptrExpr = expr(typeRef(org.bridj.Pointer.class));
//                                    Expression targetTypeExpr = result.typeConverter.typeLiteral(getSingleTypeParameter(nativeMethod.getValueType()));
//                                    if (targetTypeExpr == null || (returnType.targetTypeConversion != null && returnType.targetTypeConversion.type == ConvType.Void)) {
//                                        objectToRawFollowedCall = methodCall(ptrExpr, "pointerToAddress", objectToRawFollowedCall);
//                                    } else {
//                                        objectToRawFollowedCall = methodCall(ptrExpr, "pointerToAddress", objectToRawFollowedCall, targetTypeExpr);
//                                    }
//                                }
                                break;
                            case Enum:
                                objectToRawFollowedCall = createEnumExpression(returnType, objectToRawFollowedCall);
                                rawToObjectFollowedCall = createGetEnumValueExpression(rawToObjectFollowedCall);
//                                objectToRawFollowedCall = methodCall(expr(typeRef(org.bridj.FlagSet.class)), "fromValue", objectToRawFollowedCall, result.typeConverter.typeLiteral(getSingleTypeParameter(nativeMethod.getValueType())));
                                break;
                            case NativeLong:
                            case NativeSize:
                                if (!rawMethod.getValueType().toString().equals("long")) {
                                    objectToRawFollowedCall = new New(nativeMethod.getValueType().clone(), objectToRawFollowedCall);
                                    rawToObjectFollowedCall = createGetIntegralValueExpression(rawToObjectFollowedCall);
                                }
                            default:
                                break;
                        }
                        nativeMethod.setBody(block(new Statement.Return(objectToRawFollowedCall)));
                        if (isCallback) {
                            rawMethod.removeModifiers(ModifierType.Abstract);
                            rawMethod.setBody(block(new Statement.Return(rawToObjectFollowedCall)));
                        }
                    }
                    forwardedToRaw = true;
                }
            }

            if (!forwardedToRaw) {
                nativeMethod.addModifiers(isCallback ? ModifierType.Abstract : ModifierType.Native);
                if (isOptional && !isCallback) {
                    nativeMethod.addAnnotation(new Annotation(typeRef(Optional.class)));
                }
            }
        } else {
            nativeMethod.setBody(convertedBody);
        }

        if (!forwardedToRaw && convertedBody == null) {
            extractibleDecls.add(nativeMethod);
        }
        
        for (Declaration d : extractibleDecls) {
            if (extractingDeclarations) {
                if (d instanceof Function) {
                    Function m = (Function)d.clone();
                    m.setBody(null);
                    m.removeModifiers(ModifierType.Abstract, ModifierType.Final, 
                        ModifierType.Static, ModifierType.Native,
                        ModifierType.Public, ModifierType.Protected);
                    declarations.addDeclaration(m);
//                    d.addAnnotation(new Annotation(typeRef(Override.class)));
                }
            }
        }

    }
    
    private Expression createGetEnumValueExpression(Expression value) {
        return cast(typeRef(int.class), methodCall(value, "value"));
    }
    private Expression createGetIntegralValueExpression(Expression value) {
        return methodCall(value, "longValue");
    }
    
    private Expression createGetPeerExpression(Expression value) {
        return methodCall(expr(typeRef(org.bridj.Pointer.class)), "getPeer", value);
    }
    private Expression createPointerToAddressExpression(NL4JConversion pointerType, Expression value) {
        TypeRef tr = pointerType.getTypeRef(false);
        if (pointerType.isTypedPointer) {
            return new New(tr, value);
        } else {
            Expression ptrExpr = expr(typeRef(org.bridj.Pointer.class));
            Expression targetTypeExpr = result.typeConverter.typeLiteral(getSingleTypeParameter(tr));
            if (targetTypeExpr == null || (pointerType.targetTypeConversion != null && pointerType.targetTypeConversion.type == ConvType.Void)) {
                return methodCall(ptrExpr, "pointerToAddress", value);
            } else {
                // Cast to unparameterized Pointer to avoid weird cast cases
                return cast(typeRef(org.bridj.Pointer.class), methodCall(ptrExpr, "pointerToAddress", value, targetTypeExpr));
            }
        }
    }
    private Expression createEnumExpression(NL4JConversion enumType, Expression value) {
        TypeRef tr = enumType.getTypeRef(false);
        return methodCall(expr(typeRef(org.bridj.FlagSet.class)), "fromValue", value, result.typeConverter.typeLiteral(getSingleTypeParameter(tr)));
    }

    private void fillIn(Signatures signatures, Identifier functionName, Function nativeMethod, NL4JConversion returnType, List<NL4JConversion> paramTypes, List<String> paramNames, Identifier varArgType, String varArgName, boolean isCallback, boolean useRawTypes) {
        for (int i = 0, n = paramTypes.size(); i < n; i++) {
            NL4JConversion paramType = paramTypes.get(i);
            String paramName = paramNames.get(i);
            nativeMethod.addArg(paramType.annotateTypedType(new Arg(paramName, paramType.getTypeRef(useRawTypes)), useRawTypes));//.getTypedTypeRef())));
        }
        if (varArgType != null) {
            nativeMethod.addArg(new Arg(varArgName, typeRef(varArgType.clone()))).setVarArg(true);
        }

        if (returnType != null) {
            returnType.annotateTypedType(nativeMethod, useRawTypes);
            nativeMethod.setValueType(returnType.getTypeRef(useRawTypes));
        }

        String natSig = nativeMethod.computeSignature(SignatureType.JavaStyle);

        Identifier javaMethodName = signatures == null ? functionName : signatures.findNextMethodName(natSig, functionName);
        if (!javaMethodName.equals(functionName)) {
            nativeMethod.setName(javaMethodName);
        }
    }

    @Override
    public Struct convertStruct(Struct struct, Signatures signatures, Identifier callerLibraryClass, String callerLibrary, boolean onlyFields) throws IOException {
        Identifier structName = getActualTaggedTypeName(struct);
        if (structName == null) {
            return null;
        }

        //if (structName.toString().contains("MonoObject"))
        //	structName.toString();

        if (struct.isForwardDeclaration())// && !result.structsByName.get(structName).isForwardDeclaration())
        {
            return null;
        }

        if (!signatures.addClass(structName)) {
            return null;
        }

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
            baseClass = result.typeConverter.getTaggedTypeIdentifierInJava(parent);
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
        Constant uuid = (Constant) struct.getModifierValue(ModifierType.UUID);
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
        if (!result.config.noStaticInit)
            structJavaClass.addDeclaration(newStaticInit());
        //if (result.config.microsoftCOM) {
        if (uuid != null) {
            structJavaClass.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.IID), uuid));
        }
        structJavaClass.setResolvedJavaIdentifier(ident(structName));
        if (result.config.forceNames)
            annotateActualName(structJavaClass, structName);
        addParentNamespaceAnnotation(structJavaClass, struct.getParentNamespace());
        structJavaClass.addToCommentBefore(preComments);
        //System.out.println("parentFieldsCount(structName = " + structName + ") = " + parentFieldsCount);
        final int iChild[] = new int[]{parentFieldsCount};

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

        if (isUnion) {
            structJavaClass.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Union)));
        }

        int iVirtual = 0, iConstructor = 0;
        //List<Declaration> children = new ArrayList<Declaration>();
        boolean succeeded = true;
        for (Declaration d : struct.getDeclarations()) {
            //if (isUnion)
            //    iChild[0] = 0;

            if (d instanceof VariablesDeclaration) {
                succeeded = convertVariablesDeclaration((VariablesDeclaration) d, childSignatures, structJavaClass, iChild, false, structName, callerLibraryClass, callerLibrary) && succeeded;
            } else if (!onlyFields) {
                if (d instanceof TaggedTypeRefDeclaration) {
                    TaggedTypeRef tr = ((TaggedTypeRefDeclaration) d).getTaggedTypeRef();
                    if (tr instanceof Struct) {
                        outputConvertedStruct((Struct) tr, childSignatures, structJavaClass, callerLibrary, false);
                    } else if (tr instanceof Enum) {
                        convertEnum((Enum) tr, childSignatures, structJavaClass, callerLibraryClass);
                    }
                } else if (d instanceof TypeDef) {
                    TypeDef td = (TypeDef) d;
                    TypeRef tr = td.getValueType();
                    if (tr instanceof Struct) {
                        outputConvertedStruct((Struct) tr, childSignatures, structJavaClass, callerLibrary, false);
                    } else {
                        FunctionSignature fs = null;
                        if (tr instanceof FunctionSignature) {
                            fs = (FunctionSignature) tr;
                        } else if (tr instanceof TypeRef.Pointer) {
                            TypeRef target = ((TypeRef.Pointer) tr).getTarget();
                            if (target instanceof FunctionSignature) {
                                fs = (FunctionSignature) target;
                            }
                        }
                        if (fs != null) {
                            convertCallback(fs, childSignatures, structJavaClass, callerLibraryClass);
                        }
                    }
                } else if (result.config.genCPlusPlus && d instanceof Function) {
                    Function f = (Function) d;

                    boolean isVirtual = f.hasModifier(ModifierType.Virtual);
                    boolean isConstructor = f.getName().equals(structName) && (f.getValueType() == null || f.getValueType().toString().equals("void"));
                    if (isConstructor && f.getArgs().isEmpty()) {
                        continue; // default constructor was already generated
                    }
                    String library = result.getLibrary(struct);
                    if (library == null) {
                        continue;
                    }
                    List<Declaration> decls = new ArrayList<Declaration>();
                    DeclarationsHolder out = new ListWrapper(decls);
                    convertFunction(f, childSignatures, false, out, out, callerLibraryClass, isConstructor ? iConstructor : -1);
                    for (Declaration md : decls) {
                        if (!(md instanceof Function)) {
                            continue;
                        }
                        Function method = (Function) md;
                        boolean commentOut = false;
                        if (isVirtual) {
                            method.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Virtual), expr(iVirtual)));
                        } else if (method.getValueType() == null) {
                            method.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Constructor), expr(iConstructor)));
                            isConstructor = true;
                        }
                        if (method.getName().toString().equals("operator")) {
                            commentOut = true;
                        }

                        if (commentOut) {
                            structJavaClass.addDeclaration(new EmptyDeclaration(method.toString()));
                        } else {
                            structJavaClass.addDeclaration(method);
                        }
                    }
                    if (isVirtual) {
                        iVirtual++;
                    }
                    if (isConstructor) {
                        iConstructor++;
                    }
                }
            }
        }

        if (succeeded) {
            Function defaultConstructor = new Function(Type.JavaMethod, ident(structName), null).setBody(block(stat(methodCall("super")))).addModifiers(ModifierType.Public);
            if (childSignatures.addMethod(defaultConstructor)) {
                structJavaClass.addDeclaration(defaultConstructor);
            }

            String ptrName = "pointer";
            Function castConstructor = new Function(Type.JavaMethod, ident(structName), null, new Arg(ptrName, typeRef(result.config.runtime.pointerClass))).setBody(block(stat(methodCall("super", varRef(ptrName))))).addModifiers(ModifierType.Public);
            if (childSignatures.addMethod(castConstructor)) {
                structJavaClass.addDeclaration(castConstructor);
            }
        } else {
            structJavaClass.addModifiers(ModifierType.Abstract);
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
                    if (result.config.verbose) {
                        System.out.println("Failed to resolve parent '" + parentName + "' for struct '" + name + "'");
                    }
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

        final boolean useRawTypes = false;
        //Expression initVal = null;
        int fieldIndex = iChild[0];
        //convertTypeToNL4J(TypeRef valueType, Identifier libraryClassName, Expression structPeerExpr, Expression structIOExpr, Expression valueExpr, int fieldIndex, int bits) throws UnsupportedConversionException {

        NL4JConversion conv = ((BridJTypeConversion) result.typeConverter).convertTypeToNL4J(
                mutatedType,
                callerLibraryName,
                thisField("io"),
                varRef(name),
                fieldIndex,
                bits);

        if (conv == null) {
            throw new UnsupportedConversionException(mutatedType, "failed to convert type to Java");
        } else if (conv.isUndefined) {
            throw new UnsupportedConversionException(mutatedType, "failed to convert type to Java (undefined type)");
        } else if ("void".equals(String.valueOf(conv.getTypeRef(useRawTypes)))) {
            throw new UnsupportedConversionException(mutatedType, "void type !");
            //out.add(new EmptyDeclaration("SKIPPED:", v.formatComments("", true, true, false), v.toString()));
        }

        Function convDecl = new Function();
        conv.annotateTypedType(convDecl, useRawTypes);
        convDecl.setType(Type.JavaMethod);
        convDecl.addModifiers(ModifierType.Public);

        if (conv.arrayLengths != null) {
            convDecl.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Length), "({" + StringUtils.implode(conv.arrayLengths, ", ") + "})"));
        }
        if (conv.bits != null) {
            convDecl.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Bits), conv.bits));
        }
        if (conv.byValue) {
            convDecl.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.ByValue)));
        }

        for (Element e : toImportDetailsFrom) {
            convDecl.importDetails(e, false);
        }
        convDecl.importDetails(mutatedType, true);
        //convDecl.importDetails(javaType, true);

//			convDecl.importDetails(v, false);
//			convDecl.importDetails(vs, false);
//			convDecl.importDetails(valueType, false);
//			valueType.stripDetails();
        convDecl.moveAllCommentsBefore();

        convDecl.setName(ident(name));

        if (!isGlobal) {
            convDecl.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Field), expr(fieldIndex)));
        }
        convDecl.setValueType(conv.getTypeRef(useRawTypes));

        TypeRef javaType = convDecl.getValueType();
        String pointerGetSetMethodSuffix = StringUtils.capitalize(javaType.toString());

        Expression getGlobalPointerExpr = null;
        if (isGlobal) {
            getGlobalPointerExpr = methodCall(methodCall(methodCall(expr(typeRef(BridJ.class)), "getNativeLibrary", expr(callerLibrary)), "getSymbolPointer", expr(name)), "as", result.typeConverter.typeLiteral(javaType.clone()));
        }
        List<Declaration> out = new ArrayList<Declaration>();
        boolean addedGetterOrSetter = false;
        if (conv.getExpr != null) {
            Function getter = convDecl.clone();
            if (isGlobal) {
                getter.setBody(block(
                        tryRethrow(new Statement.Return(cast(javaType.clone(), methodCall(getGlobalPointerExpr, "get"))))));
            } else {
                getter.setBody(block(
                        new Statement.Return(conv.getExpr)));
            }
            out.add(getter);
            addedGetterOrSetter = true;
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
                        new Statement.Return(thisRef())))));
            } else {
                setter.setBody(block(
                        stat(conv.setExpr),
                        new Statement.Return(thisRef())));
            }
            out.add(setter);
            addedGetterOrSetter = true;

            if (result.config.scalaStructSetters) {
                setter = new Function();
                setter.setType(Type.JavaMethod);
                setter.setName(ident(name + "_$eq"));
                setter.setValueType(javaType.clone());
                setter.addArg(new Arg(name, javaType.clone()));
                setter.addModifiers(ModifierType.Public, ModifierType.Final);
                setter.setBody(block(
                        stat(methodCall(name, varRef(name))),
                        new Statement.Return(varRef(name))));
                out.add(setter);
            }
        }
        
        if (!addedGetterOrSetter) {
            out.add(new EmptyDeclaration("Failed to convert value " + name + " of type " + mutatedType));
        }
        return out;
    }

    @Override
    public boolean convertVariablesDeclaration(VariablesDeclaration v, Signatures signatures, DeclarationsHolder out, int[] iChild, boolean isGlobal, Identifier holderName, Identifier callerLibraryClass, String callerLibrary) {
        try {
            TypeRef valueType = v.getValueType();
            for (Declarator vs : v.getDeclarators()) {
                if (vs.getDefaultValue() != null) {
                    continue;
                }

                String name = vs.resolveName();
                if (name == null || name.length() == 0) {
                    name = "anonymous" + (nextAnonymousFieldId++);
                }

                TypeRef mutatedType = valueType;
                if (!(vs instanceof DirectDeclarator)) {
                    mutatedType = (TypeRef) vs.mutateTypeKeepingParent(valueType);
                    vs = new DirectDeclarator(vs.resolveName());
                }
                //Declarator d = v.getDeclarators().get(0);
                List<Declaration> vds = convertVariablesDeclarationToBridJ(name, mutatedType, iChild, vs.getBits(), isGlobal, holderName, callerLibraryClass, callerLibrary, v, vs);
                if (vs.getBits() > 0) {
                    for (Declaration vd : vds) {
                        vd.addAnnotation(new Annotation(result.config.runtime.typeRef(JNAeratorConfig.Runtime.Ann.Bits), expr(vs.getBits())));
                    }
                }

                for (Declaration vd : vds) {
                    if (vd instanceof Function) {
                        if (!signatures.addMethod((Function) vd)) {
                            continue;
                        }
                    }

                    vd.importDetails(mutatedType, true);
                    vd.moveAllCommentsBefore();
                    if (!(mutatedType instanceof Primitive) && !result.config.noComments) {
                        vd.addToCommentBefore("C type : " + mutatedType);
                    }

                    out.addDeclaration(vd);
                }
                //}

            }
            return true;
        } catch (Throwable e) {
            if (!(e instanceof UnsupportedConversionException)) {
                e.printStackTrace();
            }
//			if (!result.config.limitComments)
            out.addDeclaration(new EmptyDeclaration(e.toString()));
            return false;
        } finally {
            iChild[0]++;
        }
    }
    int nextAnonymousFieldId;

    @Override
    protected void configureCallbackStruct(Struct callbackStruct) {
        callbackStruct.setType(Struct.Type.JavaClass);
        callbackStruct.addModifiers(ModifierType.Public, ModifierType.Static, ModifierType.Abstract);
    }

    @Override
    protected Struct createFakePointerClass(Identifier fakePointer) {
        Struct ptClass = result.declarationsConverter.publicStaticClass(fakePointer, ident(TypedPointer.class), Struct.Type.JavaClass, null);

        String addressVarName = "address";
        ptClass.addDeclaration(new Function(Function.Type.JavaMethod, fakePointer, null,
                new Arg(addressVarName, typeRef(long.class))).addModifiers(ModifierType.Public).setBody(
                block(stat(methodCall("super", varRef(addressVarName))))));
        ptClass.addDeclaration(new Function(Function.Type.JavaMethod, fakePointer, null,
                new Arg(addressVarName, typeRef(org.bridj.Pointer.class))).addModifiers(ModifierType.Public).setBody(
                block(stat(methodCall("super", varRef(addressVarName))))));
        return ptClass;
    }

    @Override
    protected void fillLibraryMapping(Result result, SourceFiles sourceFiles, DeclarationsHolder declarations, DeclarationsHolder implementations, String library, Identifier javaPackage, Expression nativeLibFieldExpr) throws IOException {
        super.fillLibraryMapping(result, sourceFiles, declarations, implementations, library, javaPackage, nativeLibFieldExpr);

        if (implementations instanceof ModifiableElement) {
            ModifiableElement minterf = (ModifiableElement) implementations;
            minterf.addAnnotation(new Annotation(org.bridj.ann.Library.class, expr(library)));
            minterf.addAnnotation(new Annotation(org.bridj.ann.Runtime.class, classLiteral(result.hasCPlusPlus ? CPPRuntime.class : CRuntime.class)));
        }
    }

    @Override
    public void generateLibraryFiles(SourceFiles sourceFiles, Result result, JNAeratorConfig config) throws IOException {
        for (String library : result.libraries) {
            if (library == null) {
                continue; // to handle code defined in macro-expanded expressions
            }

            Identifier javaPackage = result.javaPackageByLibrary.get(library);
            Identifier implementationsSimpleClassName = result.getLibraryClassSimpleName(library);
            Identifier declarationsSimpleClassName = result.getLibraryDeclarationsClassSimpleName(library);

            Identifier implementationsFullClassName = result.getLibraryClassFullName(library);//ident(javaPackage, libraryClassName);
            Identifier declarationsFullClassName = result.getLibraryDeclarationsClassFullName(library);
            //if (!result.objCClasses.isEmpty())
            //	out.println("import org.rococoa.ID;");


            Struct implementations = new Struct();
            implementations.setType(Struct.Type.JavaClass);
            implementations.addToCommentBefore("Wrapper for library <b>" + library + "</b>",
                    result.declarationsConverter.getFileCommentContent(result.config.libraryProjectSources.get(library), null));
            implementations.addModifiers(ModifierType.Public);
            implementations.setTag(implementationsSimpleClassName);
            implementations.addParent(ident(config.runtime.libraryClass, expr(typeRef(implementationsSimpleClassName))));
            if (declarationsFullClassName != null) {
                implementations.addProtocol(declarationsFullClassName.clone());
            }
            if (!config.noStaticInit)
                implementations.addDeclaration(newStaticInit());
            implementations.setResolvedJavaIdentifier(implementationsFullClassName);

            Struct declarations;
            if (declarationsFullClassName != null) {
                declarations = new Struct();
                declarations.setType(Struct.Type.JavaInterface);
                declarations.addToCommentBefore("Interface for library <b>" + library + "</b>",
                        result.declarationsConverter.getFileCommentContent(result.config.libraryProjectSources.get(library), null));
                declarations.addModifiers(ModifierType.Public);
                declarations.setTag(declarationsSimpleClassName.clone());
                declarations.setResolvedJavaIdentifier(declarationsFullClassName);
            } else {
                declarations = implementations;
            }
//            String libFileOrDirArgName = "libraryFileOrDirectory";
//            Function constr = new Function(Function.Type.JavaMethod, fullLibraryClassName.resolveLastSimpleIdentifier().clone(), null, new Arg(libFileOrDirArgName, typeRef(File.class)));
//            constr.addModifiers(ModifierType.Public);
//            constr.setBody(block(stat(methodCall("super", varRef(libFileOrDirArgName)))));
//            interf.addDeclaration(constr);
//
//            constr = new Function(Function.Type.JavaMethod, fullLibraryClassName.resolveLastSimpleIdentifier().clone(), null);
//            constr.addModifiers(ModifierType.Public);
//            constr.addThrown(typeRef(FileNotFoundException.class));
//            constr.setBody(block(stat(methodCall("super", classLiteral(typeRef(fullLibraryClassName.clone()))))));
//            interf.addDeclaration(constr);

            fillLibraryMapping(result, sourceFiles, declarations, implementations, library, javaPackage, varRef("this"));
            writeLibraryInterface(result, sourceFiles, declarations, library, javaPackage);
            if (declarations != implementations) {
                writeLibraryInterface(result, sourceFiles, implementations, library, javaPackage);
            }
        }
    }

    private Function newStaticInit() {
        Function f = new Function(Function.Type.StaticInit, null, null).setBody(block(
            stat(methodCall(
            expr(typeRef(BridJ.class)),
            Expression.MemberRefStyle.Dot,
            "register")))).addModifiers(ModifierType.Static);
        return f;
    }

    private void addParentNamespaceAnnotation(ModifiableElement dest, Identifier parentNamespace) {
        if (parentNamespace != null) {
            dest.addAnnotation(new Annotation(typeRef(org.bridj.ann.Namespace.class), expr(parentNamespace.toString())));
        }
    }
}
