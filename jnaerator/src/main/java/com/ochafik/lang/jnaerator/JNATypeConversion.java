/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.SyntaxUtils;
import static com.ochafik.lang.jnaerator.TypeConversion.TypeConversionMode.FieldType;
import static com.ochafik.lang.jnaerator.TypeConversion.TypeConversionMode.PointedValue;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.Enum;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.util.listenable.Pair;
import com.sun.jna.WString;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.Buffer;
import java.util.Map;

/**
 *
 * @author ochafik
 */
public class JNATypeConversion extends TypeConversion {

    public JNATypeConversion(Result result) {
        super(result);
    }

    @Override
    public void initTypes() {
        super.initTypes();
        result.prim("BOOL", JavaPrim.Boolean);
    }

    public Expression getEnumItemValue(com.ochafik.lang.jnaerator.parser.Enum.EnumItem enumItem, boolean forceConstants) {
//        Enum e = (Enum)enumItem.getParentElement();
//        if (forceConstants) {
//            Map<String, EnumItemResult> values = getEnumValuesAndCommentsByName(e, null);
//            EnumItemResult enumResult = values.get(enumItem.getName());
//            if (enumResult != null) {
//                return enumResult.constantValue;
//            }
//        }
        return cast(typeRef(int.class), findEnumItem(enumItem));
    }

    @Override
    protected JavaPrim getCppBoolMappingType() {
        return JavaPrim.Byte;
    }

    public TypeRef convertTypeToJNA(TypeRef valueType, TypeConversionMode conversionMode, Identifier libraryClassName) throws UnsupportedConversionException {

//		if (String.valueOf(valueType).contains("MonoImageOpenStatus"))
//			valueType.toString();

        TypeRef original = valueType;
        TypeRef resolvedTypeRef = resolveTypeDef(valueType, libraryClassName, true, false);
        if (resolvedTypeRef != null) {
            valueType = resolvedTypeRef;
        }

//		if (String.valueOf(valueType).contains("MonoObject"))
//			valueType.toString();
        String valueTypeString = String.valueOf(valueType);

        if (valueTypeString.matches("void\\s*\\*") || valueTypeString.matches("const\\s*void\\s*\\*")) {
            //valueType = (TypeRef)valueType;
            if (original instanceof TypeRef.Pointer && result.config.features.contains(JNAeratorConfig.GenFeatures.TypedPointersForForwardDeclarations) && allowFakePointers) {
                TypeRef.Pointer p = (TypeRef.Pointer) original;
                if (p.getTarget() instanceof TypeRef.SimpleTypeRef) {
                    if (isResolved((TypeRef.SimpleTypeRef) p.getTarget())) {
                        return p.getTarget();
                    }

                    Identifier name = ((TypeRef.SimpleTypeRef) p.getTarget()).getName();
                    if (!"void".equals(name.toString()) && name.isPlain()) {
//						int i = name.lastIndexOf('.');
//						if (i >= 0)
//							name = name.substring(i + 1);
                        return typeRef(result.getFakePointer(libraryClassName, name));
                    }
                }
            }
        } else {
            if (conversionMode == TypeConversionMode.ReturnType && result.config.stringifyConstCStringReturnValues) {
                if (isString(valueTypeString, false)) {
                    return typeRef(String.class);
                } else if (isString(valueTypeString, true)) {
                    return typeRef(WString.class);
                }

            } else if (conversionMode == TypeConversionMode.PrimitiveOrBufferParameter) {
                if (isString(valueTypeString, false)) {
                    return typeRef(String.class);
                } else if (isString(valueTypeString, true)) {
                    return typeRef(WString.class);
                } else if (isStringPtrPtr(valueTypeString, false)) {
                    return arrayRef(typeRef(String.class));
                } else if (isStringPtrPtr(valueTypeString, true)) {
                    return arrayRef(typeRef(WString.class));
                }
                /*else if (conversionMode == TypeConversionMode.PrimitiveOrBufferParameter) {
                 if (valueTypeString.matches("char\\*"))
                 return typeRef(StringPointer.ByValue.class);
                 else if (valueTypeString.matches("wchar_t\\*"))
                 return typeRef(WStringPointer.ByValue.class);
                 }*/
            }
        }

        if (valueType instanceof TypeRef.Primitive) {
            JavaPrim prim = getPrimitive(valueType, libraryClassName);
            if (prim != null) {
                return primRef(prim);
            }

//			if (!valueType.getModifiers().contains("long"))
//				return valueType.toString();
        }
        if (valueType instanceof TypeRef.TaggedTypeRef) {
            Identifier name = result.declarationsConverter.getActualTaggedTypeName((TypeRef.TaggedTypeRef) valueType);
            if (name != null) {
                if (valueType instanceof Enum) {
                    TypeRef tr = findEnum(name, libraryClassName);
                    if (tr != null) {
                        TypeRef intRef = primRef(JavaPrim.Int);
                        intRef.setCommentBefore(tr.getCommentBefore());
                        return intRef;
                    }
                } else if (valueType instanceof Struct) {
                    TypeRef.SimpleTypeRef tr = findStructRef(name, libraryClassName);
                    if (tr != null) {
                        switch (conversionMode) {
                            case PointedValue:
                            case NativeParameterWithStructsPtrPtrs:
                            case NativeParameter:
                            case PrimitiveOrBufferParameter:
                            case ReturnType:
                            case PrimitiveReturnType:
                            case FieldType:
                                return tr;
                            case StaticallySizedArrayField:
                            case ExpressionType:
                            default:
                                return subType(tr, ident("ByValue"));
                        }
                    }
                }
            }
        }

        if (valueType instanceof TypeRef.FunctionSignature) {
            TypeRef tr = findCallbackRef((TypeRef.FunctionSignature) valueType, libraryClassName);
            if (tr != null) {
                return tr;
            } else {
                return typeRef(((TypeRef.FunctionSignature) valueType).getFunction().getName().clone());
            }
        }
        if (valueType instanceof TypeRef.TargettedTypeRef) {
            //TypeRef target = resolveTypeDef(((TargettedTypeRef) valueType).getTarget(), callerLibraryClass);
            TypeRef target = ((TypeRef.TargettedTypeRef) valueType).getTarget();

            boolean staticallySized = valueType instanceof TypeRef.ArrayRef && ((TypeRef.ArrayRef) valueType).hasStaticStorageSize();

            TypeRef convArgType = null;
            JavaPrim prim = getPrimitive(target, libraryClassName);
            if (prim != null) {
                if (prim == JavaPrim.Void) {
                    return typeRef(result.config.runtime.pointerClass);
                } else {
                    convArgType = primRef(prim);
                }
            } else {
                Identifier name = null;
                if (target instanceof TypeRef.SimpleTypeRef) {
                    name = ((TypeRef.SimpleTypeRef) target).getName();
                } else if (target instanceof Struct) {
                    Struct struct = (Struct) target;
                    if (struct == null) {
                        valueType = resolveTypeDef(original, libraryClassName, true, false);
                        struct = null;
                    } else {
                        name = result.declarationsConverter.getActualTaggedTypeName(struct);
                    }
                } else if (target instanceof TypeRef.FunctionSignature) {
                    TypeRef tr = findCallbackRef((TypeRef.FunctionSignature) target, libraryClassName);
                    if (tr != null) {
                        if (valueType instanceof TypeRef.ArrayRef) {
                            return new TypeRef.ArrayRef(tr);
                        } else {
                            return tr;
                        }
                    }
                    //else
                    //	return typeRef(((FunctionSignature)valueType).getFunction().getName());
                } else if (target instanceof TypeRef.Pointer) {
                    if (conversionMode == TypeConversionMode.NativeParameter) {
                        return typeRef(PointerByReference.class);
                    }

                    TypeRef.Pointer pt = ((TypeRef.Pointer) target);
                    TypeRef ptarget = pt.getTarget();
                    if (ptarget instanceof TypeRef.SimpleTypeRef) {
                        TypeRef.SimpleTypeRef ptargett = (TypeRef.SimpleTypeRef) ptarget;
                        Identifier tname = ptargett.getName();
                        if (result.structsFullNames.contains(tname)) {
//							if (conversionMode == TypeConversionMode.FieldType)
//                                                            return typeRef(PointerByReference.class);
//							else
                            return new TypeRef.ArrayRef(typeRef(ident(ptargett.getName(), "ByReference")));
                        } else if ((tname = result.findFakePointer(tname)) != null) {
                            return new TypeRef.ArrayRef(typeRef(tname.clone()));
                        }
                    }
                }
                if (name != null) {
                    if (result.isFakePointer(name)) {
                        if (conversionMode == TypeConversionMode.NativeParameter) {
                            return typeRef(result.config.runtime.pointerClass);
                        } else {
                            return typeRef(PointerByReference.class);
                        }
                    }
                    /// Pointer to Objective-C class ?
                    convArgType = findObjCClass(name);
                    boolean isQualStruct = result.structsFullNames.contains(name);
                    if (convArgType == null || isQualStruct) {
                        /// Pointer to C structure
                        TypeRef.SimpleTypeRef structRef = isQualStruct ? typeRef(name) : findStructRef(name, libraryClassName);
                        if (structRef != null) {//result.cStructNames.contains(name)) {
                            switch (conversionMode) {
                                case ExpressionType:
                                case FieldType:
                                    convArgType = valueType instanceof TypeRef.ArrayRef
                                            ? structRef
                                            : subType(structRef, ident("ByReference"));
                                    if (valueType instanceof TypeRef.Pointer) {
                                        return convArgType;
                                    }
                                    break;
                                default:
                                    if (isQualStruct
                                            && (valueType instanceof TypeRef.ArrayRef) && (conversionMode == TypeConversionMode.NativeParameterWithStructsPtrPtrs
                                            || conversionMode == TypeConversionMode.PrimitiveOrBufferParameter)) {
                                        return arrayRef(structRef);
                                    }
                                    convArgType = structRef;
                                    if (valueType instanceof TypeRef.Pointer) {
                                        return convArgType;
                                    }
                                    break;
                            }
                        } else {
                            try {
                                TypeConversionMode targettedConversionMode;
                                switch (conversionMode) {
                                    case NativeParameter:
                                    case NativeParameterWithStructsPtrPtrs:
                                    case PrimitiveOrBufferParameter:
                                    case PrimitiveReturnType:
                                        targettedConversionMode = TypeConversionMode.PointedValue;
                                        break;
                                    default:
                                        targettedConversionMode = conversionMode;
                                        break;
                                }
                                convArgType = convertTypeToJNA(target, targettedConversionMode, libraryClassName);
                                /*if (result.isUndefinedType(convArgType)) {
                                 if (allowFakePointers && original instanceof SimpleTypeRef)
                                 return typeRef(result.getFakePointer(libraryClassName, ((SimpleTypeRef)original).getName().clone()));
                                 else
                                 convArgType = typeRef(result.config.runtime.pointerClass);
                                 }*/

                                if (convArgType != null && result.callbacksFullNames.contains(ident(convArgType.toString())) && !(valueType instanceof TypeRef.ArrayRef)) {
                                    TypeRef tr = typeRef(result.config.runtime.pointerClass);
                                    if (!result.config.noComments) {
                                        tr.setCommentBefore("@see " + convArgType);
                                    }
                                    return tr;
                                }
                                prim = getPrimitive(convArgType, libraryClassName);
                            } catch (UnsupportedConversionException ex) {
                                if (valueType instanceof TypeRef.Pointer
                                        && target instanceof TypeRef.SimpleTypeRef
                                        && result.config.features.contains(JNAeratorConfig.GenFeatures.TypedPointersForForwardDeclarations)
                                        && allowFakePointers) {

                                    if (isResolved((TypeRef.SimpleTypeRef) target)) {
                                        return target;
                                    }
//									int i = name.lastIndexOf('.');
//									if (i >= 0) {
//										name = name.substring(i + 1);
//									}
                                    return typeRef(result.getFakePointer(libraryClassName, name));
                                } else {
                                    return typeRef(result.config.runtime.pointerClass);
                                }
                            }
                        }
                    }
                } else {
                    try {
                        convArgType = convertTypeToJNA(target, conversionMode, libraryClassName);
                        prim = getPrimitive(convArgType, libraryClassName);
                    } catch (UnsupportedConversionException ex) {
                        //convArgType = null;//
                        return typeRef(result.config.runtime.pointerClass);
                    }
                }
            }
            switch (conversionMode) {
                case StaticallySizedArrayField:
                    return new TypeRef.ArrayRef(convArgType);
                case PrimitiveOrBufferParameter:
                    if (!result.config.noPrimitiveArrays && (target.getModifiers().contains(ModifierType.Const)
                            || valueType.getModifiers().contains(ModifierType.Const))) {
                        return new TypeRef.ArrayRef(convArgType);
                    }
                    Class<? extends Buffer> bc = primToBuffer.get(prim);
                    if (bc != null) {
                        return typeRef(bc);
                    }
                case ReturnType:
                case FieldType:
                    if (staticallySized) {
                        return arrayRef(convArgType);
                    }
                default:
                    if (prim != null) {
                        if (prim == JavaPrim.Byte) {
                            return (TypeRef) typeRef(result.config.runtime.pointerClass).importComments(convArgType);
                        }

                        Class<? extends ByReference> byRefClass = primToByReference.get(prim);
                        if (byRefClass != null) {
                            return typeRef(byRefClass).importDetails(convArgType, false);
                        }
                    }
                    if (convArgType != null && !convArgType.toString().equals(result.config.runtime.pointerClass.getName()) && valueType instanceof TypeRef.Pointer && target instanceof TypeRef.SimpleTypeRef) {
                        return convArgType;
                    }

            }
            if (target instanceof TypeRef.Pointer) {
                return typeRef(PointerByReference.class);
            }

            if (allowUnknownPointers) {
                return typeRef(result.config.runtime.pointerClass);
            }
        }
        if (valueType instanceof TypeRef.SimpleTypeRef) {
            Identifier name = ((TypeRef.SimpleTypeRef) valueType).getName();
            if (name == null) {
                throw new UnsupportedConversionException(valueType, null);
            }

            boolean isQualStruct = result.structsFullNames.contains(name);
            //isQualCallback = result.callbacksFullNames.contains(name);
            TypeRef.SimpleTypeRef structRef = null;

            if (!isQualStruct && (valueType instanceof TypeRef.SimpleTypeRef) && isResolved((TypeRef.SimpleTypeRef) valueType)) {
                structRef = (TypeRef.SimpleTypeRef) valueType;
            } else {
                if (name instanceof Identifier.SimpleIdentifier) {
                    TypeRef tr = findObjCClass(name);
                    if (tr == null) {
                        tr = findObjCClass(new Identifier.SimpleIdentifier(((Identifier.SimpleIdentifier) name).getName()));
                    }
                    if (tr != null) {
                        return tr;
                    }
                }
                structRef = isQualStruct ? typeRef(name) : findStructRef(name, libraryClassName);
            }

            if (structRef != null) {
                isQualStruct = result.structsFullNames.contains(structRef.getName());
                switch (conversionMode) {
                    case PointedValue:
                        if (!(isQualStruct && isResolved(structRef))) {
                            return typeRef(result.config.runtime.pointerClass);
                        }
                    case FieldType:
                        return structRef;
                    default:
                        if (isQualStruct) {
                            return subType(structRef, ident("ByValue"));
                        } else {
                            return structRef;
                        }
                }
            }

            TypeRef callbackRef = findCallbackRef(name, libraryClassName);
            if (callbackRef != null) {
                return callbackRef;
            }

            TypeRef.SimpleTypeRef enumTypeRef = findEnum(name, libraryClassName);
            //FieldRef enumQualifiedName = findEnum(name);
            if (enumTypeRef != null) {
                return enumTypeRef;
            }

            TypeRef objCClassRef = findObjCClass(name);
            if (objCClassRef != null) {
                return objCClassRef;
            }
        }

        JavaPrim prim = getPrimitive(valueType, libraryClassName);
        if (prim != null) {
            return primRef(prim);
        }

        if (valueType instanceof TypeRef.SimpleTypeRef && allowFakePointers) {
            //return typeRef(result.getUndefinedType(libraryClassName, ((SimpleTypeRef)valueType).getName().clone()));
//            if (conversionMode == PointedValue)
//                return typeRef(result.config.runtime.pointerClass);
//            else
            return typeRef(result.getFakePointer(libraryClassName, ((TypeRef.SimpleTypeRef) valueType).getName().clone()));
        }
        unknownTypes.add(String.valueOf(valueType));
        throw new UnsupportedConversionException(valueType, null);
    }

    @Override
    public Pair<Expression, TypeRef> convertExpressionToJava(Expression x, Identifier libraryClassName, boolean promoteNativeLongToLong, boolean forceConstant, Map<String, Pair<Expression, TypeRef>> mappings) throws UnsupportedConversionException {
        if (x instanceof Expression.Cast) {
            TypeRef tpe = ((Expression.Cast) x).getType();
            Pair<Expression, TypeRef> casted = convertExpressionToJava(((Expression.Cast) x).getTarget(), libraryClassName, promoteNativeLongToLong, forceConstant, mappings);

            TypeRef tr = convertTypeToJNA(tpe, TypeConversionMode.ExpressionType, libraryClassName);
            Expression val = casted.getFirst();
            return typed(val, tr);
        } else if (x instanceof Expression.TypeRefExpression) {

            Expression.TypeRefExpression tre = (Expression.TypeRefExpression) x;
            TypeRef tr = tre.getType();
            if (tr instanceof TypeRef.SimpleTypeRef) {
                TypeRef.SimpleTypeRef str = (TypeRef.SimpleTypeRef) tr;
                Identifier ident = str.getName();
                if (ident != null) {
                    if (result.enumItemsFullName.contains(ident)) {
                        return typed(tre, typeRef(Integer.TYPE));
                    }
                }
            }
            if (tr.isMarkedAsResolved()) {
                return typed(tre, tr);
            } else {
                TypeRef conv = convertTypeToJNA(tr, TypeConversionMode.ExpressionType, libraryClassName);
                return typed(new Expression.TypeRefExpression(conv), conv);
            }
        }
        return super.convertExpressionToJava(x, libraryClassName, promoteNativeLongToLong, forceConstant, mappings);
    }
}
