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
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.bridj.IntValuedEnum;
/**
 *
 * @author ochafik
 */
public class BridJTypeConversion extends TypeConversion {

    public BridJTypeConversion(Result result) {
        super(result);
    }
 
    public Expression getEnumItemValue(Enum.EnumItem enumItem) { 
        Expression enumValue = findEnumItem(enumItem);
        if (((Enum)enumItem.getParentElement()).getTag() != null)
                enumValue = methodCall(enumValue, "value");
		
        return cast(typeRef(int.class), enumValue);
    }
    
    
    public class NL4JConversion {

        public ConvType type;
        public TypeRef typeRef, indirectType;
        public List<Expression> arrayLengths;
        public Expression bits;
        public Expression getExpr, setExpr;
        public boolean wideString, readOnly, isPtr, byValue, nativeSize, cLong, isUndefined;
        public Charset charset;
        public final List<Annotation> annotations = new ArrayList<Annotation>();
        //public String structIOFieldGetterNameRadix;
        public String pointerFieldGetterNameRadix;

        public Expression arrayLength() {
            Expression length = null;
            for (Expression m : arrayLengths) {
                m.setParenthesis(true);
                if (length == null)
                    length = m.clone();
                else
                    length = expr(length, Expression.BinaryOperator.Multiply, m.clone());
            }
            return length.setParenthesis(arrayLengths.size() > 1);
        }
        public TypeRef getIndirectTypeRef() {
            if (type == ConvType.Void) {
                return typeRef(ident("?"));
            }
            if (result.config.runtime == JNAeratorConfig.Runtime.BridJ) {
                if (type == ConvType.NativeSize) {
                    return typeRef(SizeT.class);
                }
                if (type == ConvType.NativeLong) {
                    return typeRef(org.bridj.CLong.class);
                }
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
                        //throw new UnsupportedConversionException(typeRef, "Struct by value not supported yet");
                        break;
                    default:
                        throw new UnsupportedConversionException(typeRef, "Not supported");
                }
            }
            return element;
        }

        public <M extends ModifiableElement> M annotateTypedType(M element) throws UnsupportedConversionException {
            element.addAnnotations(annotations);
            if (type != ConvType.Pointer) {
                annotateRawType(element);
            }
            return element;
        }
    }
    
    public NL4JConversion convertTypeToNL4J(TypeRef valueType, Identifier libraryClassName, Expression structIOExpr, Expression valueExpr, int fieldIndex, int bits) throws UnsupportedConversionException {
        TypeRef original = valueType;
        //if (valueType != null && valueType.toString().contains("MonoDomain"))
        //    valueType = (TypeRef)valueType;
        valueType = resolveTypeDef(valueType, libraryClassName, true, true);

        //Expression offsetExpr = structIOExpr == null ? null : methodCall(structIOExpr, "getFieldOffset", expr(fieldIndex));
        //Expression bitOffsetExpr = structIOExpr == null || bits <= 0 ? null : methodCall(structIOExpr, "getFieldBitOffset", expr(fieldIndex));
        //Expression bitLengthExpr = structIOExpr == null || bits <= 0  ? null : methodCall(structIOExpr, "getFieldBitLength", expr(fieldIndex));

        NL4JConversion conv = new NL4JConversion();

        if (valueType == null) {
            conv.type = ConvType.Void;
            conv.typeRef = primRef(JavaPrim.Void);
            return conv;
        }
        //if (valueType instanceof Struct)
        //	valueType = typeRef(findStructRef((Struct)valueType, libraryClassName));

        if (valueType instanceof TypeRef.TargettedTypeRef) {
            TypeRef targetRef = ((TypeRef.TargettedTypeRef) valueType).getTarget();

            if (valueType instanceof TypeRef.Pointer.ArrayRef) {
                TypeRef.Pointer.ArrayRef arrayRef = (TypeRef.Pointer.ArrayRef) valueType;

                List<Expression> sizes = new ArrayList<Expression>();
                for (Expression dim : arrayRef.flattenDimensions()) {
                    if (dim == null || dim instanceof Expression.EmptyArraySize)
                        continue;
                
                    Expression m = convertExpressionToJava(dim, libraryClassName, false).getFirst();
                    m.setParenthesis(false);
                    sizes.add(m);
                }
                if (!sizes.isEmpty())
                    conv.arrayLengths = sizes;
            }

            try {
                NL4JConversion targetConv = convertTypeToNL4J(targetRef, libraryClassName, null, null, -1, -1);
                //if (result.isFakePointer(libraryClassName))
                if (targetConv.isUndefined && allowFakePointers && original instanceof TypeRef.SimpleTypeRef) {
                    conv.isPtr = true;
                    conv.type = ConvType.Pointer;
                    conv.typeRef = typeRef(result.getFakePointer(libraryClassName, ((TypeRef.SimpleTypeRef)original).getName().clone()));
					if (structIOExpr != null) {
						if (conv.arrayLengths == null)
							conv.setExpr = methodCall(structIOExpr.clone(), "setPointerField", thisRef(), expr(fieldIndex), valueExpr);
						conv.getExpr = methodCall(structIOExpr.clone(), "getTypedPointerField", thisRef(), expr(fieldIndex));
					}
					return conv;
                }
				TypeRef pointedTypeRef = targetConv.getIndirectTypeRef();
				if (targetConv.type != ConvType.Void) {
					if (targetConv.type == ConvType.NativeSize)
						pointedTypeRef = typeRef(SizeT.class);
					else if (targetConv.type == ConvType.NativeLong)
						pointedTypeRef = typeRef(CLong.class);
				}
				if (pointedTypeRef != null) {
					conv.isPtr = true;
                    conv.type = ConvType.Pointer;
                    conv.typeRef = typeRef(ident(result.config.runtime.pointerClass, expr(pointedTypeRef.clone())));
					if (structIOExpr != null) {
						if (conv.arrayLengths == null)
							conv.setExpr = methodCall(structIOExpr.clone(), "setPointerField", thisRef(), expr(fieldIndex), valueExpr);
						conv.getExpr = methodCall(structIOExpr.clone(), "getPointerField", thisRef(), expr(fieldIndex));
					}
					return conv;
				}
	        } catch (UnsupportedConversionException ex) {
                conv.isPtr = true;
                conv.type = ConvType.Pointer;
                conv.typeRef = typeRef(result.config.runtime.pointerClass);
                return conv;

				/*if (valueType instanceof TypeRef.Pointer && targetRef instanceof SimpleTypeRef && allowFakePointers) {
					conv.typeRef = typeRef(result.getFakePointer(libraryClassName, ((SimpleTypeRef)targetRef).getName().clone()));
					if (structIOExpr != null) {
						if (conv.arrayLengths == null)
							conv.setExpr = methodCall(structIOExpr.clone(), "setPointerField", thisRef(), expr(fieldIndex), valueExpr);
						conv.getExpr = methodCall(structIOExpr.clone(), "getTypedPointerField", thisRef(), expr(fieldIndex));
					}
					return conv;
				}//*/
                /*if (valueType instanceof TypeRef.Pointer && targetRef instanceof SimpleTypeRef && allowFakePointers) {
					conv.typeRef = typeRef(ident(result.config.runtime.pointerClass, expr(typeRef(result.getUndefinedType(libraryClassName, ((SimpleTypeRef)targetRef).getName().clone())))));
					if (structIOExpr != null) {
						if (conv.arrayLengths == null)
							conv.setExpr = methodCall(structIOExpr.clone(), "setPointerField", thisRef(), expr(fieldIndex), valueExpr);
						conv.getExpr = methodCall(structIOExpr.clone(), "getTypedPointerField", thisRef(), expr(fieldIndex));
					}
					return conv;
				}*/
	        }
        } else {//if (valueType instanceof SimpleTypeRef || valueType instanceof TaggedTypeRef || valueType) {
            JavaPrim prim = getPrimitive(valueType, libraryClassName);
            if (prim != null) {
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
            } else {
            	
            	Identifier valueName = valueType instanceof TypeRef.SimpleTypeRef ? ((TypeRef.SimpleTypeRef)valueType).getName() : null;
                
                // Structs
                if (valueType instanceof Struct)
                    conv.typeRef = typeRef(findStructRef((Struct)valueType, libraryClassName));
                else if (result.structsFullNames.contains(valueName))
                    conv.typeRef = valueType;
                else
                    conv.typeRef = typeRef(findStructRef(valueName, libraryClassName));
                if (conv.typeRef != null) 
                {
            		//conv.setExpr = methodCall(structPeerExpr.clone(), "set" + radix, offsetExpr.clone(), valueExpr);
                	if (structIOExpr != null) {
                    	conv.setExpr = methodCall(structIOExpr.clone(), "setNativeObjectField", thisRef(), expr(fieldIndex), valueExpr);
                		conv.getExpr = methodCall(structIOExpr.clone(), "getNativeObjectField", thisRef(), expr(fieldIndex));
                		//conv.getExpr = new Expression.New(conv.typeRef, (Expression)methodCall(structIOExpr.clone(), "offset", offsetExpr.clone()));
                	}
                	conv.type = ConvType.Struct;
                	return conv;
                }
                
                // TODO proper namespaces
                if (valueName != null)
                    valueName = valueName.resolveLastSimpleIdentifier();
                
                // Enums
                if (valueType instanceof Enum)
                    conv.typeRef = findEnumRef((Enum)valueType, libraryClassName);
                else if (result.enumsFullNames.contains(valueName))
                    conv.typeRef = valueType;
                else 
                    conv.typeRef = findEnum(valueName, libraryClassName);
                if (conv.typeRef != null) 
                {
                	if (structIOExpr != null) {
                		conv.setExpr = methodCall(structIOExpr.clone(), "setEnumField", thisRef(), expr(fieldIndex), valueExpr);
	                	conv.getExpr = methodCall(structIOExpr.clone(), "getEnumField", thisRef(), expr(fieldIndex));//expr(typeRef(FlagSet.class)), "fromValue", methodCall(structPeerExpr.clone(), "getInt", expr(fieldIndex)), classLiteral(conv.typeRef.clone()));
                	}
                	conv.type = ConvType.Enum;
                	conv.typeRef = typeRef(ident(IntValuedEnum.class, expr(conv.typeRef)));
                	return conv;
                }
                
                // Callbacks
                conv.typeRef = conv.typeRef = 
            		result.callbacksFullNames.contains(valueName) ? valueType : 
        			valueType instanceof TypeRef.FunctionSignature ? 
    					findCallbackRef((TypeRef.FunctionSignature)valueType, libraryClassName) : 
        				findCallbackRef(valueName, libraryClassName);
                if (conv.typeRef != null) 
                {
                	if (structIOExpr != null) {
	                	conv.setExpr = methodCall(structIOExpr.clone(), "setPointerField", thisRef(), expr(fieldIndex), valueExpr);
	                	conv.getExpr = methodCall(structIOExpr.clone(), "getPointerField", thisRef(), expr(fieldIndex));
	            	}
	        		conv.type = ConvType.Pointer;
                	conv.typeRef = typeRef(ident(result.config.runtime.pointerClass, expr(conv.typeRef)));
	        		return conv;
                    
                }
            }
        }

        if (valueType instanceof TypeRef.SimpleTypeRef && allowFakePointers) {
            conv.typeRef = typeRef(result.getUndefinedType(libraryClassName, ((TypeRef.SimpleTypeRef)valueType).getName().resolveLastSimpleIdentifier().clone()));
            conv.isUndefined = true;
            return conv;
        }
        throw new UnsupportedConversionException(original, "Unsupported type");
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
                Pair<Expression, TypeRef> c = convertExpressionToJava(x, callerLibraryName, false);
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
                Expression c = convertExpressionToJava(x, libraryClassName, false).getFirst();
                res = expr(res, Expression.BinaryOperator.Multiply, c);
            }
        } else if (type instanceof TypeRef.SimpleTypeRef || type instanceof TypeRef.Primitive) {
            JavaPrim prim = getPrimitive(type, libraryClassName);
            if (prim != null) {
                res = sizeof(prim);
            } else {
                Identifier structRef = findStructRef(((TypeRef.SimpleTypeRef) type).getName(), libraryClassName);
                if (structRef == null) {
                    structRef = findStructRef(((TypeRef.SimpleTypeRef) type).getName().resolveLastSimpleIdentifier(), libraryClassName);
                }
                if (structRef != null) {
                    return methodCall(new Expression.New(typeRef(structRef)), Expression.MemberRefStyle.Dot, "size");
                }
            }
        } else if (type instanceof Struct) {
            Struct s = (Struct) type;
            if (s != null) {
                Identifier structName = result.declarationsConverter.getActualTaggedTypeName(s);
                Identifier structRef = findStructRef(structName, libraryClassName);
                if (structRef != null) {
                    return methodCall(new Expression.New(typeRef(structRef)), Expression.MemberRefStyle.Dot, "size");
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
    public Pair<Expression, TypeRef> convertExpressionToJava(Expression x, Identifier libraryClassName, boolean promoteNativeLongToLong) throws UnsupportedConversionException {
        Pair<Expression, TypeRef> res = null;
        if (x instanceof Expression.Cast) {
            TypeRef tpe = ((Expression.Cast) x).getType();
            Pair<Expression, TypeRef> casted = convertExpressionToJava(((Expression.Cast) x).getTarget(), libraryClassName, promoteNativeLongToLong);
            
            NL4JConversion conv = convertTypeToNL4J(tpe, libraryClassName, null, null, -1, -1);
            TypeRef tr = conv.typeRef;
            Expression val = casted.getFirst();
            if (conv.isPtr) {
                    if (isString(val))
                        val = methodCall(expr(typeRef(result.config.runtime.pointerClass)), "pointerToCString", val);
                    else
                        val = methodCall(expr(typeRef(result.config.runtime.pointerClass)), "pointerToAddress", val);
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
            return super.convertExpressionToJava(x, libraryClassName, promoteNativeLongToLong);
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
