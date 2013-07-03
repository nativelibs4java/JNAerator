/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import static com.ochafik.lang.SyntaxUtils.as;
import com.ochafik.lang.SyntaxUtils;
import org.bridj.FlagSet;
import org.bridj.ValuedEnum;
import org.bridj.SizeT;
import org.bridj.ann.CLong;
import org.bridj.ann.Ptr;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.Enum;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bridj.IntValuedEnum;
import org.bridj.util.DefaultParameterizedType;

/**
 *
 * @author ochafik
 */
public class BridJTypeConversion extends TypeConversion {

    public BridJTypeConversion(Result result) {
        super(result);
    }

    @Override
    public void initTypes() {
        super.initTypes();
        result.prim("BOOL", JavaPrim.Int);
    }

    @Override
    public Expression typeLiteral(TypeRef c) {
        if (c instanceof SimpleTypeRef && result.config.runtime == JNAeratorConfig.Runtime.BridJ) {
            Identifier id = ((SimpleTypeRef) c).getName();
            Identifier.SimpleIdentifier sid = id.resolveLastSimpleIdentifier();
            if (!sid.getTemplateArguments().isEmpty()) {
                Identifier erased = id.eraseTemplateArguments();

                List<Expression> exprs = new ArrayList<Expression>();
                exprs.add(typeLiteral(typeRef(erased.clone())));
                for (Expression t : sid.getTemplateArguments()) {
                    if (t instanceof Expression.TypeRefExpression) {
                        exprs.add(typeLiteral(((Expression.TypeRefExpression) t).getType().clone()));
                    }
                }
                return methodCall(expr(typeRef(DefaultParameterizedType.class)), "paramType", exprs.toArray(new Expression[exprs.size()]));
            }
        }
        return super.typeLiteral(c);
    }

    @Override
    public Expression getEnumItemValue(Enum.EnumItem enumItem, boolean forceConstants) {
        Enum e = (Enum) enumItem.getParentElement();
        if (forceConstants) {
            Map<String, EnumItemResult> values = getEnumValuesAndCommentsByName(e, null);
            EnumItemResult enumResult = values.get(enumItem.getName());
            if (enumResult != null) {
                return enumResult.constantValue;
            }
        }
        Expression enumValue = findEnumItem(enumItem);
        if (e.getTag() != null) {
            enumValue = methodCall(enumValue, "value");
        }

        return cast(typeRef(int.class), enumValue.setParenthesis(true));
    }

    public class NL4JConversion {

        public ConvType type = ConvType.Default;
        public NL4JConversion targetTypeConversion;
        private TypeRef typeRef, indirectType;
        public List<Expression> arrayLengths;
        public Expression bits;
        public Expression getExpr, setExpr;
        public boolean wideString, readOnly, byValue, nativeSize, cLong, isUndefined, isTypedPointer;
        public Charset charset;
        public final List<Annotation> annotations = new ArrayList<Annotation>();
        //public String structIOFieldGetterNameRadix;
        public String pointerFieldGetterNameRadix;

        public TypeRef getTypeRef(boolean useRawTypes) {
            if (useRawTypes) {
                switch (type) {
                    case Pointer:
                        return typeRef(long.class);
                    case Enum:
                        return typeRef(int.class);
                    default:
                        return typeRef;
                }
            } else {
                return typeRef;
            }
        }

        public Expression arrayLength() {
            Expression length = null;
            for (Expression m : arrayLengths) {
                m.setParenthesis(true);
                if (length == null) {
                    length = m.clone();
                } else {
                    length = expr(length, Expression.BinaryOperator.Multiply, m.clone());
                }
            }
            return length.setParenthesis(arrayLengths.size() > 1);
        }

        public TypeRef getIndirectTypeRef() {
            if (type == ConvType.Void) {
                return typeRef(ident("?"));
            }
            if (type == ConvType.NativeSize) {
                return typeRef(SizeT.class);
            }
            if (type == ConvType.NativeLong) {
                return typeRef(org.bridj.CLong.class);
            }
            if (type == ConvType.ComplexDouble) {
                return typeRef(org.bridj.ComplexDouble.class);
            }
            TypeRef t = indirectType == null ? typeRef : indirectType;
            return t == null ? null : t.clone();
        }

        public <M extends ModifiableElement> M annotateRawType(M element) throws UnsupportedConversionException {
            element.addAnnotations(annotations);
            if (type != null) {
                switch (type) {
                    case Enum:
                    case Primitive:
                    case Void:
                        break;
                    case NativeLong:
                        element.addAnnotation(new Annotation(CLong.class));
                        break;
                    case NativeSize:
                        element.addAnnotation(new Annotation(Ptr.class));
                        break;
                    case Pointer:
                        element.addAnnotation(new Annotation(Ptr.class));
                        break;
                    case Struct:
                    case ComplexDouble:
                    case FunctionSignature:
                    case Default:
                        //throw new UnsupportedConversionException(typeRef, "Struct by value not supported yet");
                        break;
                    default:
                        throw new UnsupportedConversionException(typeRef, "Not supported");
                }
            }
            return element;
        }

        public <M extends ModifiableElement> M annotateTypedType(M element, boolean useRawTypes) throws UnsupportedConversionException {
            element.addAnnotations(annotations);
            if (type != ConvType.Pointer || useRawTypes) {
                annotateRawType(element);
            }
            return element;
        }
    }

    public NL4JConversion convertTypeToNL4J(TypeRef valueType, Identifier libraryClassName, Expression structIOExpr, Expression valueExpr, int fieldIndex, int bits) throws UnsupportedConversionException {

        TypeRef original = valueType;

        if (!(original instanceof TypeRef.TargettedTypeRef)) {
            TypeRef resolved = result.resolveType(original, false);
            if (resolved == null) {
                resolved = resolveTypeDef(valueType, libraryClassName, false/*true*/, true);
            }
            if (resolved != null) {
                valueType = resolved;
            }
        }

        //Expression offsetExpr = structIOExpr == null ? null : methodCall(structIOExpr, "getFieldOffset", expr(fieldIndex));
        //Expression bitOffsetExpr = structIOExpr == null || bits <= 0 ? null : methodCall(structIOExpr, "getFieldBitOffset", expr(fieldIndex));
        //Expression bitLengthExpr = structIOExpr == null || bits <= 0  ? null : methodCall(structIOExpr, "getFieldBitLength", expr(fieldIndex));

        NL4JConversion conv = new NL4JConversion();

        if (valueType == null) {
            conv.type = ConvType.Void;
            conv.typeRef = primRef(JavaPrim.Void);
            return conv;
        }
        JavaPrim prim = getPrimitive(valueType, libraryClassName);
        if (prim != null) {
            return convertPrimitiveTypeRefToNL4J(prim, structIOExpr, fieldIndex, valueExpr);
        }
        if (valueType instanceof TypeRef.TargettedTypeRef) {
            TypeRef targetRef = ((TypeRef.TargettedTypeRef) valueType).getTarget();

            if (valueType instanceof TypeRef.Pointer.ArrayRef) {
                TypeRef.Pointer.ArrayRef arrayRef = (TypeRef.Pointer.ArrayRef) valueType;

                List<Expression> sizes = new ArrayList<Expression>();
                for (Expression dim : arrayRef.flattenDimensions()) {
                    if (dim == null || dim instanceof Expression.EmptyArraySize) {
                        continue;
                    }

                    Expression m = convertExpressionToJava(dim, libraryClassName, false, true, null).getFirst();
                    m.setParenthesis(false);
                    sizes.add(m);
                }
                if (!sizes.isEmpty()) {
                    conv.arrayLengths = sizes;
                }
            }

            try {
                conv.targetTypeConversion = convertTypeToNL4J(targetRef, libraryClassName, null, null, -1, -1);
            } catch (UnsupportedConversionException ex) {
            }

            if (allowFakePointers
                    && (conv.targetTypeConversion == null || conv.targetTypeConversion.isUndefined)
                    && original instanceof TypeRef.SimpleTypeRef) {
                conv.type = ConvType.Pointer;
                conv.isTypedPointer = true;
                conv.typeRef = typeRef(result.getFakePointer(libraryClassName, ((TypeRef.SimpleTypeRef) original).getName().clone()));
                if (structIOExpr != null) {
                    if (conv.arrayLengths == null) {
                        conv.setExpr = methodCall(structIOExpr.clone(), "setPointerField", thisRef(), expr(fieldIndex), valueExpr);
                    }
                    conv.getExpr = methodCall(structIOExpr.clone(), "getTypedPointerField", thisRef(), expr(fieldIndex));
                }
                return conv;
            } else if (conv.targetTypeConversion == null) {
                conv.type = ConvType.Pointer;
                conv.typeRef = typeRef(result.config.runtime.pointerClass);
                return conv;
            } else {
                TypeRef pointedTypeRef = conv.targetTypeConversion.getIndirectTypeRef();

                if (pointedTypeRef != null) {
                    conv.type = ConvType.Pointer;
                    conv.isUndefined = conv.targetTypeConversion.isUndefined;
                    conv.typeRef = typeRef(ident(result.config.runtime.pointerClass, expr(pointedTypeRef.clone())));
                    if (structIOExpr != null) {
                        if (conv.arrayLengths == null) {
                            conv.setExpr = methodCall(structIOExpr.clone(), "setPointerField", thisRef(), expr(fieldIndex), valueExpr);
                        }
                        conv.getExpr = methodCall(structIOExpr.clone(), "getPointerField", thisRef(), expr(fieldIndex));
                    }
                    return conv;
                }
            }
        } else if (valueType.getResolvedJavaIdentifier() != null) {
            conv.typeRef = typeRef(valueType.getResolvedJavaIdentifier().clone());
            if (valueType instanceof TypeRef.FunctionSignature) {
                conv.type = ConvType.FunctionSignature;
            } else if (valueType instanceof Enum) {
                conv.type = ConvType.Enum;
                conv.typeRef = typeRef(ident(IntValuedEnum.class, expr(conv.typeRef)));
                if (structIOExpr != null) {
                    conv.setExpr = methodCall(structIOExpr.clone(), "setEnumField", thisRef(), expr(fieldIndex), valueExpr);
                    conv.getExpr = methodCall(structIOExpr.clone(), "getEnumField", thisRef(), expr(fieldIndex));//expr(typeRef(FlagSet.class)), "fromValue", methodCall(structPeerExpr.clone(), "getInt", expr(fieldIndex)), classLiteral(conv.typeRef.clone()));
                }
            } else if (valueType instanceof Struct) {
                conv.type = ConvType.Struct;
                if (structIOExpr != null) {
                    conv.setExpr = methodCall(structIOExpr.clone(), "setNativeObjectField", thisRef(), expr(fieldIndex), valueExpr);
                    conv.getExpr = methodCall(structIOExpr.clone(), "getNativeObjectField", thisRef(), expr(fieldIndex));
                    //conv.getExpr = new Expression.New(conv.typeRef, (Expression)methodCall(structIOExpr.clone(), "offset", offsetExpr.clone()));
                }
            } else {
                throw new RuntimeException("Failed to recognize conversion type: " + valueType);
            }
            return conv;
        }

        if (valueType instanceof TypeRef.SimpleTypeRef && allowFakePointers) {
            Identifier name = ((TypeRef.SimpleTypeRef) valueType).getName();
            if (name != null) {
                conv.type = ConvType.Pointer;
                conv.typeRef = typeRef(result.getUndefinedType(libraryClassName, name.resolveLastSimpleIdentifier().clone()));
                conv.isUndefined = true;
                return conv;
            }
        }
        throw new UnsupportedConversionException(original, "Unsupported type");
    }

    private NL4JConversion convertPrimitiveTypeRefToNL4J(JavaPrim prim, Expression structIOExpr, int fieldIndex, Expression valueExpr) {
        NL4JConversion conv = new NL4JConversion();
        String radix;
        switch (prim) {
            case NativeLong:
                conv.type = ConvType.NativeLong;
                conv.typeRef = typeRef(Long.TYPE);
                conv.indirectType = typeRef(org.bridj.CLong.class);
                radix = "CLong";
                break;
            case NativeSize:
                conv.type = ConvType.NativeSize;
                conv.typeRef = typeRef(Long.TYPE);
                conv.indirectType = typeRef(org.bridj.SizeT.class);
                radix = "SizeT";
                break;
            case Void:
                conv.type = ConvType.Void;
                conv.typeRef = primRef(prim);
                radix = null;
                break;
            case ComplexDouble:
                conv.type = ConvType.ComplexDouble;
                conv.typeRef = typeRef(org.bridj.ComplexDouble.class);
                radix = "NativeObject";
                break;
            default:
                conv.type = ConvType.Primitive;
                conv.typeRef = primRef(prim);
                conv.indirectType = typeRef(prim.wrapperType);
                radix = StringUtils.capitalize(prim.type.getName());
                break;
        }
        if (structIOExpr != null && radix != null) {
            conv.setExpr = methodCall(structIOExpr.clone(), "set" + radix + "Field", thisRef(), expr(fieldIndex), valueExpr);
            conv.getExpr = methodCall(structIOExpr.clone(), "get" + radix + "Field", thisRef(), expr(fieldIndex));
        }
        return conv;
    }

    public Expression getFlatArraySizeExpression(TypeRef.Pointer.ArrayRef arrayRef, Identifier callerLibraryName) throws UnsupportedConversionException {
        Expression mul = null;
        List<Expression> dims = arrayRef.flattenDimensions();
        for (int i = dims.size(); i-- != 0;) {
            Expression x = dims.get(i);

            if (x == null || x instanceof Expression.EmptyArraySize) {
                return null;
                //javaType = jr = new ArrayRef(typeRef(Pointer.class));
                //break;
            } else {
                Pair<Expression, TypeRef> c = convertExpressionToJava(x, callerLibraryName, false, true, null);
                c.getFirst().setParenthesis(dims.size() > 1);
                if (mul == null) {
                    mul = c.getFirst();
                } else {
                    mul = expr(c.getFirst(), Expression.BinaryOperator.Multiply, mul);
                }
            }
        }
        return mul;
    }

    protected Expression sizeofToJava(TypeRef type, Identifier libraryClassName) throws UnsupportedConversionException {
        type = resolveTypeDef(type, libraryClassName, true, false);
//		type = type;

        Expression res = null;
        if (type instanceof TypeRef.Pointer) {
            res = memberRef(expr(typeRef(result.config.runtime.pointerClass)), Expression.MemberRefStyle.Dot, "SIZE");
        } else if (type instanceof TypeRef.ArrayRef) {
            res = sizeofToJava(((TypeRef.ArrayRef) type).getTarget(), libraryClassName);
            if (res == null) {
                return null;
            }

            TypeRef.ArrayRef ar = (TypeRef.ArrayRef) type;
            for (Expression x : ar.getDimensions()) {
                Expression c = convertExpressionToJava(x, libraryClassName, false, true, null).getFirst();
                res = expr(res, Expression.BinaryOperator.Multiply, c);
            }
        } else if (type instanceof TypeRef.SimpleTypeRef || type instanceof TypeRef.Primitive) {
            JavaPrim prim = getPrimitive(type, libraryClassName);
            if (prim != null) {
                res = sizeof(prim);
            } else {
                SimpleTypeRef structRef = findStructRef(((TypeRef.SimpleTypeRef) type).getName(), libraryClassName);
                if (structRef == null) {
                    structRef = findStructRef(((TypeRef.SimpleTypeRef) type).getName().resolveLastSimpleIdentifier(), libraryClassName);
                }
                if (structRef != null) {
                    return methodCall(new Expression.New(structRef), Expression.MemberRefStyle.Dot, "size");
                }
            }
        } else if (type instanceof Struct) {
            Struct s = (Struct) type;
            if (s != null) {
                Identifier structName = result.declarationsConverter.getActualTaggedTypeName(s);
                SimpleTypeRef structRef = findStructRef(structName, libraryClassName);
                if (structRef != null) {
                    return methodCall(new Expression.New(structRef), Expression.MemberRefStyle.Dot, "size");
                } else {
                    for (Declaration d : s.getDeclarations()) {
                        if (d instanceof VariablesDeclaration) {
                            TypeRef varsType = d.getValueType();
                            for (Declarator sto : ((VariablesDeclaration) d).getDeclarators()) {
                                Expression so = sizeofToJava(as(sto.mutateType(varsType), TypeRef.class), libraryClassName);
                                if (so == null) {
                                    return null;
                                }

                                if (res == null) {
                                    res = so;
                                } else {
                                    res = expr(res, Expression.BinaryOperator.Plus, so);
                                }
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    @Override
    public Pair<Expression, TypeRef> convertExpressionToJava(Expression x, Identifier libraryClassName, boolean promoteNativeLongToLong, boolean forceConstants, Map<String, Pair<Expression, TypeRef>> mappings) throws UnsupportedConversionException {
        Pair<Expression, TypeRef> res = null;
        if (x instanceof Expression.Cast) {
            TypeRef tpe = ((Expression.Cast) x).getType();
            Pair<Expression, TypeRef> casted = convertExpressionToJava(((Expression.Cast) x).getTarget(), libraryClassName, promoteNativeLongToLong, forceConstants, mappings);

            NL4JConversion conv = convertTypeToNL4J(tpe, libraryClassName, null, null, -1, -1);
            TypeRef tr = conv.typeRef;
            Expression val = casted.getFirst();
            if (ConvType.Pointer.equals(conv.type)) {
                if (isString(val)) {
                    val = methodCall(expr(typeRef(result.config.runtime.pointerClass)), "pointerToCString", val);
                } else {
                    val = methodCall(expr(typeRef(result.config.runtime.pointerClass)), "pointerToAddress", val);
                }
            }
            res = typed(val, tr);
        } else if (x instanceof Expression.FunctionCall) {
            Expression.FunctionCall fc = (Expression.FunctionCall) x;
            if ("sizeof".equals(String.valueOf(fc.getFunction())) && fc.getArguments().size() == 1) {
                Expression.TypeRefExpression typeEx = SyntaxUtils.as(fc.getArguments().get(0).getValue(), Expression.TypeRefExpression.class);
                if (typeEx != null) {
                    res = typed(sizeofToJava(typeEx.getType(), libraryClassName), typeRef(Integer.TYPE));
                }
            }
        }
        if (res == null) {
            return super.convertExpressionToJava(x, libraryClassName, promoteNativeLongToLong, forceConstants, mappings);
        }
        if (res.getFirst() == null) {
            return null;
        }
        res.getFirst().setParenthesis(x.getParenthesis());
        return (Pair<Expression, TypeRef>) res;
    }

    @Override
    protected JavaPrim getCppBoolMappingType() {
        return JavaPrim.Boolean;
    }
}
