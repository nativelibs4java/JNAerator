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

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;

import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.rococoa.ObjCClass;
import org.rococoa.ObjCObject;
import org.rococoa.cocoa.foundation.NSObject;

import org.bridj.util.DefaultParameterizedType;
import com.ochafik.lang.SyntaxUtils;
import com.ochafik.lang.jnaerator.JNAeratorConfig.GenFeatures;
import com.ochafik.lang.jnaerator.parser.Enum;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.Declarator.ArrayDeclarator;
import com.ochafik.lang.jnaerator.parser.Enum.EnumItem;
import com.ochafik.lang.jnaerator.parser.Expression.Constant;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRef;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRefStyle;
import com.ochafik.lang.jnaerator.parser.Expression.TypeRefExpression;
import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.TypeDef;
import com.ochafik.lang.jnaerator.parser.TypeRef.ArrayRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;
import com.ochafik.lang.jnaerator.parser.TypeRef.Pointer;
import com.ochafik.lang.jnaerator.parser.TypeRef.Primitive;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TargettedTypeRef;
import com.ochafik.lang.jnaerator.runtime.CGFloatByReference;
import com.ochafik.lang.jnaerator.runtime.CharByReference;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.ochafik.lang.jnaerator.runtime.globals.Global;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalByte;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalCGFloat;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalChar;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalDouble;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalFloat;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalInt;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalLong;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalNativeLong;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalNativeSize;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalShort;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

public abstract class TypeConversion implements ObjCppParser.ObjCParserHelper {

    Result result;
    public boolean allowUnknownPointers = true, allowFakePointers = false;

    public TypeConversion(Result result) {
        super();
        this.result = result;
        initTypes();
    }

    //public Set<Identifier> fakePointersSink;
    public enum TypeConversionMode {

        PrimitiveOrBufferParameter,
        NativeParameter,
        NativeParameterWithStructsPtrPtrs,
        FieldType,
        ReturnType,
        ExpressionType,
        StaticallySizedArrayField,
        PrimitiveReturnType, PointedValue
    }
    public Map<JavaPrim, Class<? extends ByReference>> primToByReference = new HashMap<JavaPrim, Class<? extends ByReference>>();
    public Map<JavaPrim, Class<? extends Global>> primToGlobal = new HashMap<JavaPrim, Class<? extends Global>>();
    public Map<JavaPrim, Class<? extends Buffer>> primToBuffer = new HashMap<JavaPrim, Class<? extends Buffer>>();
    public final Set<String> byReferenceClassesNames = new HashSet<String>();
    Map<String, JavaPrim> javaPrims = new TreeMap<String, JavaPrim>();

    protected void prim(String from, JavaPrim to) {
        javaPrims.put(from, to);
    }

    public boolean isObjCppPrimitive(String s) {
        return javaPrims.containsKey(s);
    }

    public enum JavaPrim {

        Void(null, null, ESize.Zero),
        Char(Character.TYPE, Character.class, ESize.CharSize),
        Long(java.lang.Long.TYPE, java.lang.Long.class, ESize.Eight),
        Int(Integer.TYPE, Integer.class, ESize.Four),
        Short(java.lang.Short.TYPE, java.lang.Short.class, ESize.Two),
        Byte(java.lang.Byte.TYPE, java.lang.Byte.class, ESize.One),
        Boolean(java.lang.Boolean.TYPE, java.lang.Boolean.class, ESize.One),
        Float(java.lang.Float.TYPE, java.lang.Float.class, ESize.Four),
        Double(java.lang.Double.TYPE, java.lang.Double.class, ESize.Eight),
        NativeLong(com.sun.jna.NativeLong.class, com.sun.jna.NativeLong.class, ESize.StaticSizeField),
        NativeSize(NativeSize.class, NativeSize.class, ESize.StaticSizeField),
        NSInteger(org.rococoa.cocoa.foundation.NSInteger.class, org.rococoa.cocoa.foundation.NSInteger.class, ESize.StaticSizeField),
        NSUInteger(org.rococoa.cocoa.foundation.NSUInteger.class, org.rococoa.cocoa.foundation.NSUInteger.class, ESize.StaticSizeField),
        CGFloat(org.rococoa.cocoa.CGFloat.class, org.rococoa.cocoa.CGFloat.class, ESize.StaticSizeField);
        public final Class<?> type, wrapperType;
        public final String simpleName, name;
        public final boolean isPrimitive;

        public enum ESize {

            One(expr(1)),
            Two(expr(2)),
            Four(expr(4)),
            Eight(expr(8)),
            StaticSizeField(null) {

                @Override
                public Expression sizeof(JavaPrim p) {
                    return staticField(p.type, "SIZE");
                }
            },
            CharSize(null) {

                @Override
                public Expression sizeof(JavaPrim p) {
                    return staticField(Native.class, "WCHAR_SIZE");
                }
            },
            Zero(expr(0));
            private final Expression sizeOfExpression;

            ESize(Expression sizeOfExpression) {
                this.sizeOfExpression = sizeOfExpression;
            }

            public Expression sizeof(JavaPrim p) {
                return sizeOfExpression.clone();
            }
        }
        public final ESize size;
        private static Map<String, JavaPrim> nameToPrim;

        public static JavaPrim getJavaPrim(String name) {
            if (nameToPrim == null) {
                nameToPrim = new HashMap<String, JavaPrim>();
                for (JavaPrim p : values()) {
                    nameToPrim.put(p.simpleName, p);
                }
            }
            return nameToPrim.get(name);
        }

        JavaPrim(Class<?> type, Class<?> wrapperType, ESize size) {
            this.type = type;
            this.wrapperType = wrapperType;
            this.size = size;
            this.name = type == null ? "void" : type.getName();
            this.isPrimitive = type == null || type.isPrimitive();
            this.simpleName = type == null ? "void" : type.getSimpleName();
        }
    }

    public Expression typeLiteral(TypeRef c) {
        if (c != null && c.toString().equals("?"))
            return Constant.newNull();
        
        if (c instanceof SimpleTypeRef && result.config.runtime == JNAeratorConfig.Runtime.BridJ) {
            Identifier id = ((SimpleTypeRef) c).getName();
            SimpleIdentifier sid = id.resolveLastSimpleIdentifier();
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
        return memberRef(expr(c), MemberRefStyle.Dot, "class");
    }

    protected abstract JavaPrim getCppBoolMappingType();
    
    public void initTypes() {

        prim("void", JavaPrim.Void);

        prim("UTF32Char", JavaPrim.Char);
        prim("unichar", JavaPrim.Char);

        prim("int64_t", JavaPrim.Long);
        prim("uint64_t", JavaPrim.Long);
        prim("u_int64_t", JavaPrim.Long);
        prim("long long", JavaPrim.Long);
        prim("long long int", JavaPrim.Long);
        prim("long int", JavaPrim.Int);
        prim("LONGLONG", JavaPrim.Long);
        prim("ULONGLONG", JavaPrim.Long);
        prim("INT", JavaPrim.Int);
        prim("UINT", JavaPrim.Int);
        prim("SHORT", JavaPrim.Short);
        prim("USHORT", JavaPrim.Short);
        prim("BYTE", JavaPrim.Byte);
        prim("UBYTE", JavaPrim.Byte);
        prim("DOUBLE", JavaPrim.Double);
        prim("FLOAT", JavaPrim.Float);
        prim("WORD", JavaPrim.Short);
        prim("DWORD", JavaPrim.Int);
        
        if (result.config.runtime == JNAeratorConfig.Runtime.BridJ)
            prim("BOOL", JavaPrim.Int);
        else
            prim("BOOL", JavaPrim.Boolean);


        prim("DWORD64", JavaPrim.Long);
        prim("LONG64", JavaPrim.Long);
        prim("UInt64", JavaPrim.Long);
        prim("SInt64", JavaPrim.Long);
        prim("__int64", JavaPrim.Long);
        prim("__int64_t", JavaPrim.Long);

        prim("int32_t", JavaPrim.Int);
        prim("uint32_t", JavaPrim.Int);
        prim("__int32_t", JavaPrim.Int);
        prim("__uint32_t", JavaPrim.Int);
        prim("u_int32_t", JavaPrim.Int);
        prim("uint32", JavaPrim.Int);
        prim("int32", JavaPrim.Int);
        prim("int", JavaPrim.Int);
        //prim("NSUInteger", JavaPrim.NativeSize);
        //prim("NSInteger", JavaPrim.NativeSize);
        prim("SInt32", JavaPrim.Int);
        prim("UInt32", JavaPrim.Int);
        prim("GLint", JavaPrim.Int);
        prim("GLuint", JavaPrim.Int);
        prim("GLenum", JavaPrim.Int);
        prim("GLsizei", JavaPrim.Int);
        prim("__darwin_size_t", JavaPrim.Int);

        prim("__int32", JavaPrim.Int);

        prim("NSInteger", JavaPrim.NSInteger);
        prim("NSUInteger", JavaPrim.NSUInteger);
        prim("CGFloat", JavaPrim.CGFloat);

        JavaPrim longPrim = result.config.gccLong ? JavaPrim.NativeSize : JavaPrim.NativeLong;
        prim("long", longPrim);
        prim("LONG", longPrim);
        prim("ULONG", longPrim);

        JavaPrim sizePrim = result.config.sizeAsLong ? longPrim : JavaPrim.NativeSize;
        prim("size_t", sizePrim);
        prim("ptrdiff_t", sizePrim);

        prim("int16_t", JavaPrim.Short);
        prim("uint16_t", JavaPrim.Short);
        prim("__int16_t", JavaPrim.Short);
        prim("__uint16_t", JavaPrim.Short);
        prim("u_int16_t", JavaPrim.Short);
        prim("uint16", JavaPrim.Short);
        prim("int16", JavaPrim.Short);
        prim("SInt16", JavaPrim.Short);
        prim("UInt16", JavaPrim.Short);
        prim("short", JavaPrim.Short);
        prim("WCHAR", JavaPrim.Short);
        prim("wchar_t", result.config.wcharAsShort ? JavaPrim.Short : JavaPrim.Char);

        prim("__int16", JavaPrim.Short);


        prim("int8_t", JavaPrim.Byte);
        prim("uint8_t", JavaPrim.Byte);
        prim("u_int8_t", JavaPrim.Byte);
        prim("__uint8_t", JavaPrim.Byte);
        prim("__int8_t", JavaPrim.Byte);
        prim("SInt8", JavaPrim.Byte);
        prim("UInt8", JavaPrim.Byte);
        prim("char", JavaPrim.Byte);
        prim("unsigned char", JavaPrim.Byte);
        prim("__unsigned char", JavaPrim.Byte);
        prim("signed char", JavaPrim.Byte);
        prim("__signed char", JavaPrim.Byte);
        prim("SignedByte", JavaPrim.Byte);

        prim("__int8", JavaPrim.Byte);

        prim("float", JavaPrim.Float);
        prim("NSFloat", JavaPrim.Float);
        prim("CGFloat", JavaPrim.Float);

        prim("double_t", JavaPrim.Double);
        prim("double", JavaPrim.Double);
        prim("NSDouble", JavaPrim.Double);
        prim("CGDouble", JavaPrim.Double);

        JavaPrim cppBoolType = getCppBoolMappingType();
        prim("bool", cppBoolType);
        prim("Boolean", cppBoolType);
        prim("boolean_t", cppBoolType);



        primToByReference.put(JavaPrim.Int, IntByReference.class);
        primToByReference.put(JavaPrim.Char, CharByReference.class);
        primToByReference.put(JavaPrim.Short, ShortByReference.class);
        primToByReference.put(JavaPrim.Byte, ByteByReference.class);
        primToByReference.put(JavaPrim.Long, LongByReference.class);
        primToByReference.put(JavaPrim.Float, FloatByReference.class);
        primToByReference.put(JavaPrim.Double, DoubleByReference.class);
        primToByReference.put(JavaPrim.NativeLong, NativeLongByReference.class);
        primToByReference.put(JavaPrim.NativeSize, NativeSizeByReference.class);
        primToByReference.put(JavaPrim.NSInteger, NativeSizeByReference.class);
        primToByReference.put(JavaPrim.NSUInteger, NativeSizeByReference.class);
        primToByReference.put(JavaPrim.CGFloat, CGFloatByReference.class);

        //primsByReference.put(JavaPrim.Void, PointerByReference.class);
        for (Class<?> c : primToByReference.values()) {
            byReferenceClassesNames.add(c.getName());
        }
//		byReferenceClassesNames.add(PointerByReference.class.getName());

        primToGlobal.put(JavaPrim.Int, GlobalInt.class);
        primToGlobal.put(JavaPrim.Char, GlobalChar.class);
        primToGlobal.put(JavaPrim.Short, GlobalShort.class);
        primToGlobal.put(JavaPrim.Byte, GlobalByte.class);
        primToGlobal.put(JavaPrim.Long, GlobalLong.class);
        primToGlobal.put(JavaPrim.Float, GlobalFloat.class);
        primToGlobal.put(JavaPrim.Double, GlobalDouble.class);
        primToGlobal.put(JavaPrim.NativeLong, GlobalNativeLong.class);
        primToGlobal.put(JavaPrim.NativeSize, GlobalNativeSize.class);
        primToGlobal.put(JavaPrim.NSInteger, GlobalNativeSize.class);
        primToGlobal.put(JavaPrim.NSUInteger, GlobalNativeSize.class);
        primToGlobal.put(JavaPrim.CGFloat, GlobalCGFloat.class);

        primToBuffer.put(JavaPrim.Int, IntBuffer.class);
        primToBuffer.put(JavaPrim.Char, CharBuffer.class);
        primToBuffer.put(JavaPrim.Short, ShortBuffer.class);
        primToBuffer.put(JavaPrim.Byte, ByteBuffer.class);
        primToBuffer.put(JavaPrim.Long, LongBuffer.class);
        primToBuffer.put(JavaPrim.Float, FloatBuffer.class);
        primToBuffer.put(JavaPrim.Double, DoubleBuffer.class);
        //primToBuffer.put(JavaPrim.NativeLong, NativeLongByReference.class);

    }
    Map<String, TypeRef> manualTypeDefs = new HashMap<String, TypeRef>();

    public Pair<TypeDef, Declarator> getTypeDef(Identifier name) {
        if (name == null) {
            return null;
        }

        Pair<TypeDef, Declarator> p = result.typeDefs.get(name);
        if (p == null) {
            return null;
        }

        Declarator value = p.getValue();
        String rname = value == null ? null : value.resolveName();
        if (rname != null) {
            if (name.equals("id")) {
                return null;
            }

            if (name.equals("SEL")) {
                return null;
            }

            if (name.equals("IMP")) {
                return null;
            }

            if (name.equals("Class")) {
                return null;
            }

            if (name.equals("BOOL")) {
                if (rname.equals("byte")) {
                    return null;
                }
            }
        }
        return p;
    }

    public TypeRef resolveTypeDef(TypeRef valueType, final Identifier libraryClassName, final boolean convertToJavaRef, final boolean convertEnumToJavaRef) {
        if (valueType == null) {
            return null;
        }

//		if (valueType.toString().equals("CGFunctionEvaluateCallback"))
//			valueType = valueType;

        if (valueType instanceof TaggedTypeRef && convertToJavaRef) {
            TaggedTypeRef ttr = (TaggedTypeRef) valueType;
            if (ttr.getTag() != null) {

                TypeRef ref = ttr instanceof Struct ? typeRef(findStructRef(ttr.getTag(), libraryClassName))
                        : ttr instanceof Enum && convertEnumToJavaRef ? findEnum(ttr.getTag(), libraryClassName) : null;
                if (ref == null && convertEnumToJavaRef) {
                    return ref;
                }
            }
        }
        final TypeRef valueTypeCl = valueType.clone();
        Arg holder = new Arg();
        holder.setValueType(valueTypeCl);
        valueTypeCl.accept(new Scanner() {

            java.util.Stack<String> names = new java.util.Stack<String>();
            int depth = 0;

            @Override
            public void visitSimpleTypeRef(SimpleTypeRef simpleTypeRef) {
                depth++;

                try {
                    Identifier name = ((SimpleTypeRef) simpleTypeRef).getName();
                    if (name == null) {
                        return;
                    }

                    String nameStr = name.toString();
                    if (nameStr == null) {
                        return;
                    }

                    if (JavaPrim.getJavaPrim(nameStr) != null) {
                        return;
                    }

                    if (names.contains(nameStr)) {
                        return;
                    }
                    names.push(nameStr);

                    try {
                        if (resolvesToPrimitive(nameStr)) {
                            return;
                        }

                        super.visitSimpleTypeRef(simpleTypeRef);
                        if (simpleTypeRef.isMarkedAsResolved()) {
                            return;
                        }

                        //					Identifier oc = findObjCClassIdent(name);
                        //					if (oc != null) {
                        //						name.replaceBy(oc);
                        //					}

                        Pair<TypeDef, Declarator> p = getTypeDef(name);
                        if (p != null) {
                            TypeRef tr = p.getFirst().getValueType();//as(p.getSecond().mutateType(p.getFirst().getValueType()), TypeRef.class);
                            if (tr instanceof Enum && !convertEnumToJavaRef) {
                                simpleTypeRef.replaceBy(typeRef(int.class));
                                return;
                            }
                            if (tr instanceof TaggedTypeRef) {
                                Identifier name2 = result.declarationsConverter.getActualTaggedTypeName((TaggedTypeRef) tr);
                                if (name2 != null) {
                                    name = name2;
                                }
                            }
                            if (convertToJavaRef) {
                                if (tr instanceof TaggedTypeRef) {
                                    TaggedTypeRef s = (TaggedTypeRef) tr;
                                    if (s.isForwardDeclaration()) {
                                        return;
                                    }

//									if (tr instanceof Enum) {
//										tr = typeRef(s.getTag().clone());
//									} else {
                                    Identifier ident = result.getTaggedTypeIdentifierInJava(s);
                                    if (ident != null) {
                                        tr = typeRef(ident);//findRef(name, s, libraryClassName));
                                    }//									}
                                } else if (tr instanceof FunctionSignature) {
                                    tr = findCallbackRef((FunctionSignature) tr, libraryClassName);
                                }
                            }
                            String strs = simpleTypeRef.toString();
                            String trs = tr == null ? null : tr.toString();
                            if (trs != null && !strs.equals(trs)) {
                                TypeRef clo = tr.clone();
                                simpleTypeRef.replaceBy(clo);
                                if (depth < 30) {
                                    clo.accept(this);
                                } else {
                                    System.err.println("Infinite loop in type conversion ? " + tr);
                                }
                            }
                            return;
                        }

                        TypeRef manualTypeRef = manualTypeDefs.get(name);
                        if (manualTypeRef != null) {
                            if (!convertToJavaRef) {
                                return;
                            }
                            simpleTypeRef.replaceBy(manualTypeRef);
                            return;
                        }

                        TypeRef structRef = typeRef(result.typeConverter.findStructRef(name, libraryClassName));
                        if (structRef != null) {
                            if (!convertToJavaRef) {
                                return;
                            }
                            simpleTypeRef.replaceBy(structRef);
                        }

                        TypeRef enumRef = result.typeConverter.findEnum(name, libraryClassName);
                        if (enumRef != null) {
                            if (!convertToJavaRef || !convertEnumToJavaRef) {
                                return;
                            }
                            simpleTypeRef.replaceBy(enumRef);
                        }

                        Define define = result.defines.get(name);
                        Expression expression = define == null ? null : define.getValue();
                        if (expression != null) {
                            if (!convertToJavaRef) {
                                return;
                            }
                            Identifier fieldName = null;
                            if (expression instanceof Expression.VariableRef) {
                                fieldName = ((Expression.VariableRef) expression).getName();
                            } else if (expression instanceof MemberRef) {
                                fieldName = ((MemberRef) expression).getName();
                            }

                            if (fieldName != null && !fieldName.equals(name)) {
                                simpleTypeRef.replaceBy(resolveTypeDef(new TypeRef.SimpleTypeRef(fieldName), libraryClassName, true /*convertToJavaRef*/, convertEnumToJavaRef));
                                return;
                            }
                        }
                    } finally {
                        names.pop();
                    }
                } finally {
                    depth--;
                }
            }
        });
        TypeRef tr = holder.getValueType();
//		tr.setParentElement(valueType.getParentElement());
        return tr;// == null ? null : tr.clone();
    }

    public boolean resolvesToPrimitive(String name) {
        return javaPrims.containsKey(name);
    }
//	TypeRef getPrimitiveRef(TypeRef valueType, String callerLibraryClass) {
//		JavaPrim prim = getPrimitive(valueType, callerLibraryClass);
//		if (prim == null)
//			return null;
//		
//		TypeRef tr = typeRef(prim);
//		if (valueType.isUnsigned())
//			tr.addAnnotation(new Annotation(Unsigned.class));
//		if (valueType.isUnsigned())
//			tr.addAnnotation(new Annotation(Unsigned.class));
//		
//		return tr;
//	}

    public static class JavaPrimitive extends Primitive {

        JavaPrim javaPrim;

        public JavaPrimitive() {
        }

        public JavaPrimitive(JavaPrim javaPrim) {
            super();
            setName(ident(javaPrim == JavaPrim.Void ? Void.TYPE : javaPrim.type));
            this.javaPrim = javaPrim;
        }

        public JavaPrim getJavaPrim() {
            return javaPrim;
        }

        public void setJavaPrim(JavaPrim javaPrim) {
            this.javaPrim = javaPrim;
        }
    }

    public JavaPrim getPrimitive(TypeRef valueType, Identifier libraryClassName) {

        valueType = resolveTypeDef(valueType, libraryClassName, true, true);
        if (valueType == null) {
            return null;
        }
        Identifier name = null;
        List<Modifier> mods = valueType.getModifiers();
        int longCount = ModifierType.Long.countIn(mods);
        if (valueType instanceof JavaPrimitive) {
            return ((JavaPrimitive) valueType).getJavaPrim();
        }
        if (valueType instanceof Primitive) {
            name = ((Primitive) valueType).getName();
            if (name == null) {
                if (longCount == 1) {
                    name = ident("long");
                } else if (longCount > 1) {
                    name = ident("long long");
                } else if (valueType.hasModifier(ModifierType.Short)) {
                    name = ident("short");
                } else {
                    name = ident("int");
                }
            }
        } else if (valueType instanceof SimpleTypeRef) {
            name = ((SimpleTypeRef) valueType).getName();
        }


        if (name == null) {
            return null;
        }

        JavaPrim p = JavaPrim.getJavaPrim(name.toString());
        if (p != null && !p.isPrimitive) {
            return p;
        }

        boolean isLong = false;
        String str;
        if ((isLong = valueType.getModifiers().contains("long")) || valueType.getModifiers().contains("short")) {
            str = (isLong ? "long " : "short ") + name;
        } else {
            str = name.toString();
        }

        JavaPrim type = javaPrims.get(str);
        if (type == JavaPrim.Int && longCount > 1) {
            return JavaPrim.Long;
        }

        return type;
    }

    public Identifier findStructRef(Identifier name, Identifier libraryClassName) {
        return findStructRef(result.structsByName.get(name), name, libraryClassName);
    }

    public Identifier findStructRef(Struct s, Identifier name, Identifier libraryClassName) {
        if (s == null || s.isForwardDeclaration()) {
            Pair<TypeDef, Declarator> pair = getTypeDef(name);
            if (pair == null) {
                return null;
            }
            if (pair.getFirst() == null || pair.getSecond() == null) {
                return null;
            }
            Object td = pair.getSecond().mutateType(pair.getFirst().getValueType());
            if (!(td instanceof Struct)) {
                return null;
            }
            s = (Struct) td;
        }
        if (s == null && result.config.runtime == JNAeratorConfig.Runtime.BridJ) {
        	String ns = name.toString();
        	Class<?> cl = null;
        	if (ns.equals("IUnknown"))
        		cl = org.bridj.cpp.com.IUnknown.class;
        	else if (ns.equals("GUID"))
        		cl = org.bridj.cpp.com.GUID.class;
        	else if (ns.equals("RECT"))
        		cl = org.bridj.cpp.com.RECT.class;
        	
        	if (cl != null)
        		return ident(cl);
        }
        return result.getTaggedTypeIdentifierInJava(s);
        /*
            name = result.declarationsConverter.getActualTaggedTypeName((TaggedTypeRef) pair.getFirst().getValueType());

            return findRef(name, s, libraryClassName, !result.config.putTopStructsInSeparateFiles);
        } else {
            return result.getTaggedTypeIdentifierInJava(s);
            //name = result.declarationsConverter.getActualTaggedTypeName(s);
        }*/
    }

    public Identifier findStructRef(Struct s, Identifier libraryClassName) {
        switch (s.getType()) {
            case ObjCClass:
            case ObjCProtocol:
                return result.objectiveCGenerator.getFullClassName(s);
            default:
                return findStructRef(s, result.declarationsConverter.getActualTaggedTypeName(s), libraryClassName);
        }
    }
//	public String find(String name, Element e, String callerLibraryClass) {
//		if (e == null)
//			return null;
//		String library = result.getLibrary(e);
//		if (library == null)
//			return null;
//		SimpleIdentifier libClass = result.getLibraryClassFullName(library);
//		return SyntaxUtils.equal(libClass, callerLibraryClass) ? name : libClass + "." + name;
//	}

    public Identifier libMember(Identifier libClass, Identifier libraryClassName, Identifier member) {
        //return ident(SyntaxUtils.equal(libClass, libraryClassName) ? null : libClass, member);
        return ident(libClass, member);
        //return member; //TODODODODODODODODOoOOOOO
    }

    public Identifier findRef(Identifier name, Element e, Identifier libraryClassName, boolean inLibClass) {
        if (e == null || !name.isPlain()) {
            return null;
        }
        String library = result.getLibrary(e);
        if (library == null) {
            return null;
        }

//		e = e.getParentElement();
        Struct parentStruct = e instanceof Struct ? (Struct) e : e.findParentOfType(Struct.class);
        if (!inLibClass && parentStruct != null) {
            if (parentStruct == e) {
                return ident(result.getLibraryPackage(library), name);
            }

            return ident(result.getTaggedTypeIdentifierInJava(parentStruct), name);
        }
        return libMember(result.getLibraryClassFullName(library), libraryClassName, name);
    }

    public SimpleTypeRef findEnum(Identifier name, Identifier libraryClassName) {
        Enum s = result.enumsByName.get(name);
        if (s == null) {
            return null;
        }
        return findEnumRef(s, libraryClassName);
    }

    public SimpleTypeRef findEnumRef(Enum s, Identifier libraryClassName) {

        if (result.config.runtime == JNAeratorConfig.Runtime.BridJ)
            return typeRef(result.getTaggedTypeIdentifierInJava(s));
        
        Identifier name = result.declarationsConverter.getActualTaggedTypeName(s);

        String library = result.getLibrary(s);
        if (library == null) {
            return null;
        }
        Identifier libClass = result.getLibraryClassFullName(library);
        //return new SimpleTypeRef(SyntaxUtils.equal(libClass, callerLibraryClass) ? name : libClass + "." + name);

        /*if (result.config.runtime == JNAeratorConfig.Runtime.BridJ) {
            return typeRef(findRef(name, s, libraryClassName, result.config.putTopStructsInSeparateFiles));
        }*/
        SimpleTypeRef tr = new SimpleTypeRef("int");
        if (result.config.features.contains(JNAeratorConfig.GenFeatures.EnumTypeLocationComments)) {
            tr.setCommentBefore("@see " + (SyntaxUtils.equal(libClass, libraryClassName) ? name : libClass + "#" + name));
        }
//		if (s.getTag() != null)
//			tr.setCommentBefore("@see enums in " + s.getTag());
        return tr;
    }

    public static Expression javaStaticFieldRef(Identifier javaClass, Identifier fieldName) {
        return memberRef(
                expr(typeRef(javaClass)),
                MemberRefStyle.Dot,
                fieldName);
    }

    public Expression findDefine(Identifier name) {
        Define s = result.defines.get(name);
        String library = s == null ? null : result.getLibrary(s);
        return library == null ? null : javaStaticFieldRef(result.getLibraryClassFullName(library), name);
    }

    public Identifier inferCallBackName(FunctionSignature functionSignature, boolean prependNamespaces, boolean qualify, Identifier libraryClassName) {
        List<String> nameElements = new ArrayList<String>();
        Identifier name = functionSignature.getFunction().getName();
        if (name != null) {
            name = name.clone();
        }

        Identifier parentIdent = null;

        Element parent = functionSignature.getParentElement();

        boolean firstParent = true;
        while (parent != null) {
            if (parent instanceof Struct) {
                parentIdent = findStructRef((Struct) parent, null);
                break;
            } else if (firstParent) {
                if (name == null && parent instanceof TypeDef) {
                    Declarator simpleSto = null;
                    for (Declarator sto : ((TypeDef) parent).getDeclarators()) {
                        String stoName = sto.resolveName();
                        if (stoName == null) {
                            continue;
                        }

                        if (!(sto instanceof ArrayDeclarator)) {
                            boolean weirdName = stoName.startsWith("_") || stoName.endsWith("_");
                            if (simpleSto == null || (simpleSto.resolveName().startsWith("_") || simpleSto.resolveName().endsWith("_")) && !weirdName) {
                                simpleSto = sto;
                            }

                            if (!weirdName) {
                                break;
                            }
                        }
                        if (stoName != null) {
                            name = new SimpleIdentifier(stoName);
                        }
                    }
                } else if (name == null && parent instanceof Arg) {
                    Arg arg = (Arg) parent;
                    Function f = SyntaxUtils.as(arg.getParentElement(), Function.class);
                    if (f != null) {
                        name = new SimpleIdentifier(f.getName() + "_" + arg.getName());
                        break;
                    }
                } else if (firstParent) {
//					if (//parent instanceof VariablesDeclaration || 
//							parent instanceof FunctionPointerDeclaration
//							//|| parent instanceof TypeDef
//					) {
//						nameElements.add("Callback");
//					}
                }
            }
            parent = parent.getParentElement();
            firstParent = false;
        }

        if (qualify && parentIdent == null) {
            //if (libraryClassName != null)
            //	parentIdent = libraryClassName;
            //else {
            String library = result.getLibrary(functionSignature);
            if (library != null) {
                parentIdent = result.getLibraryClassFullName(library);
            }
            //}
        }

        if (prependNamespaces) {
            if (name == null) {
                name = new SimpleIdentifier("callback");
            }

            nameElements.add(name.toString());
            return ident(qualify ? parentIdent : null, StringUtils.implode(nameElements, "_"));
        } else {
            return ident(qualify ? parentIdent : null, name);
        }
    }

    public TypeRef findCallbackRef(Identifier name, Identifier libraryClassName) {
        FunctionSignature s = result.callbacksByName.get(name);
        if (s == null) {
            return null;
        }

//		Struct parentStruct = s.findParentOfType(Struct.class);
//		if (parentStruct != null && (parentStruct.getType() == Struct.Type.ObjCClass || parentStruct.getType() == Struct.Type.ObjCProtocol)) {
//			//Identifier structName = result.declarationsConverter.getActualTaggedTypeName(parentStruct);
//			return //result.result.getObjCClass(parentStruct.getName()).
//				typeRef(//libMember(structName, libraryClassName, 
//						inferCallBackName(s, true, true)//)
//						);
//		}
        return findCallbackRef(s, libraryClassName);
    }

    public TypeRef findCallbackRef(FunctionSignature s, Identifier callerLibraryClass) {
        String library = result.getLibrary(s);
        if (library == null) {
            return null;
        }

//		Struct parentStruct = s.findParentOfType(Struct.class);
//		if (parentStruct != null && (parentStruct.getType() == Struct.Type.ObjCClass || parentStruct.getType() == Struct.Type.ObjCProtocol)) {
//			Identifier structName = result.declarationsConverter.getActualTaggedTypeName(parentStruct);
//			return
//				typeRef(ident(structName, inferCallBackName(s, true, true)));
//		}
        return typeRef(inferCallBackName(s, true, true, callerLibraryClass));
//		return typeRef(libMember(result.getLibraryClassFullName(library), callerLibraryClass, inferCallBackName(s, true, true)));
        //typeRef(ident(result.getLibraryClassFullName(library), inferCallBackName(s, true)));
    }

    static TypeRef primRef(JavaPrim p) {
        if (p == null) {
            return null;
        }

        return new JavaPrimitive(p);
//		return new SimpleTypeRef(toString(p));
    }

    boolean isResolved(SimpleTypeRef tr) {
        return tr != null && (tr.isMarkedAsResolved() || isResolved(tr.getName()));
    }

    boolean isResolved(Identifier i) {
        if (i == null || i.isPlain()) {
            return false;
        }
        return (i instanceof Identifier.QualifiedIdentifier)
                && Identifier.QualificationSeparator.Dot.equals(((Identifier.QualifiedIdentifier) i).getSeparator());
    }

    public enum ConvType {

        Enum, Pointer, Primitive, Struct, NativeLong, NativeSize, Void, Callback
    }
    
    static Map<String, Pair<Integer, Class<?>>> buffersAndArityByType = new HashMap<String, Pair<Integer, Class<?>>>();
    static Map<String, Pair<Integer, Class<?>>> arraysAndArityByType = new HashMap<String, Pair<Integer, Class<?>>>();
    static Map<String, String> pointerFieldGetterNameRadixByType = new HashMap<String, String>();
    static Set<String> objectMethodNames = new HashSet<String>();
    static {
    		for (Method method : Object.class.getDeclaredMethods())
    			objectMethodNames.add(method.getName());
    		
        Object[] data = new Object[]{
            "char", Byte.TYPE, byte[].class, ByteBuffer.class, "Char",
            "long", Long.TYPE, long[].class, LongBuffer.class, "Long",
            "int", Integer.TYPE, int[].class, IntBuffer.class, "Int",
            "short", Short.TYPE, short[].class, ShortBuffer.class, "Short",
            "wchar_t", Character.TYPE, char[].class, CharBuffer.class, "WChar",
            "double", Double.TYPE, double[].class, DoubleBuffer.class, "Double",
            "float", Float.TYPE, float[].class, FloatBuffer.class, "Float",
            "bool", Boolean.TYPE, boolean[].class, null, "Bool"
        };
        for (int arity : new int[]{1, 2, 4, 8, 16}) {
            String suffix = arity == 1 ? "" : arity + "";
            for (int i = 0; i < data.length; i += 5) {
                String rawType = (String) data[i];
                Class<?> scalClass = (Class<?>) data[i + 1];
                Class<?> arrClass = (Class<?>) data[i + 2];
                Class<?> buffClass = (Class<?>) data[i + 3];
                String radix = (String) data[i + 4];

                Pair<Integer, Class<?>> buffPair = new Pair<Integer, Class<?>>(arity, arity == 1 ? scalClass : buffClass),
                        arrPair = new Pair<Integer, Class<?>>(arity, arity == 1 ? scalClass : arrClass);

                for (String type : new String[]{rawType + suffix, "u" + rawType + suffix}) {
                    buffersAndArityByType.put(type, buffPair);
                    arraysAndArityByType.put(type, arrPair);
                    pointerFieldGetterNameRadixByType.put(type, radix);
                }
            }
        }
    }


    Pattern wstringPat = Pattern.compile("(__)?const wchar_t\\*"),
            stringPat = Pattern.compile("(__)?const char\\*"),
            wstringPtrPtrPat = Pattern.compile("(__)?const wchar_t\\*\\*"),
            stringPtrPtrPat = Pattern.compile("(__)?const char\\*\\*");

    protected boolean isString(String typeRefAsString, boolean wide) {
        if (wide) {
            return wstringPat.matcher(typeRefAsString).matches()
                    || result.config.charPtrAsString && typeRefAsString.equals("wchar_t*");
        } else {
            return stringPat.matcher(typeRefAsString).matches()
                    || result.config.charPtrAsString && typeRefAsString.equals("char*");
        }
    }

    protected boolean isStringPtrPtr(String typeRefAsString, boolean wide) {
        if (wide) {
            return wstringPtrPtrPat.matcher(typeRefAsString).matches()
                    || result.config.charPtrAsString && typeRefAsString.equals("wchar_t**");
        } else {
            return stringPtrPtrPat.matcher(typeRefAsString).matches()
                    || result.config.charPtrAsString && typeRefAsString.equals("char**");
        }
    }

    public TypeRef convertTypeToJNA(TypeRef valueType, TypeConversionMode conversionMode, Identifier libraryClassName) throws UnsupportedConversionException {

//		if (String.valueOf(valueType).contains("MonoImageOpenStatus"))
//			valueType.toString();

        TypeRef original = valueType;
        valueType = resolveTypeDef(valueType, libraryClassName, true, false);


//		if (String.valueOf(valueType).contains("MonoObject"))
//			valueType.toString();
        String valueTypeString = String.valueOf(valueType);

        if (valueTypeString.matches("void\\s*\\*") || valueTypeString.matches("const\\s*void\\s*\\*")) {
            //valueType = (TypeRef)valueType;
            if (original instanceof Pointer && result.config.features.contains(GenFeatures.TypedPointersForForwardDeclarations) && allowFakePointers) {
                Pointer p = (Pointer) original;
                if (p.getTarget() instanceof SimpleTypeRef) {
                    if (isResolved((SimpleTypeRef) p.getTarget())) {
                        return p.getTarget();
                    }

                    Identifier name = ((SimpleTypeRef) p.getTarget()).getName();
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

        if (valueType instanceof Primitive) {
            JavaPrim prim = getPrimitive(valueType, libraryClassName);
            if (prim != null) {
                return primRef(prim);
            }

//			if (!valueType.getModifiers().contains("long"))
//				return valueType.toString();
		} 
		if (valueType instanceof TaggedTypeRef) {
			Identifier name = result.declarationsConverter.getActualTaggedTypeName((TaggedTypeRef) valueType);
			if (name != null) {
				if (valueType instanceof Enum) {
					TypeRef tr = findEnum(name, libraryClassName);
					if (tr != null) {
						TypeRef intRef = primRef(JavaPrim.Int);
						intRef.setCommentBefore(tr.getCommentBefore());
						return intRef;
					}
				} else if (valueType instanceof Struct) {
					Identifier tr = findStructRef(name, libraryClassName);
					if (tr != null) {
						switch (conversionMode) {
						case PointedValue:
						case NativeParameterWithStructsPtrPtrs:
						case NativeParameter:
						case PrimitiveOrBufferParameter:
						case ReturnType:
						case PrimitiveReturnType:
						case FieldType:
							return typeRef(tr);
						case StaticallySizedArrayField:
						case ExpressionType:
						default:
							return typeRef(ident(tr, ident("ByValue")));
						}
					}
				}
			}
		}
		
		if (valueType instanceof FunctionSignature) {
			TypeRef tr = findCallbackRef((FunctionSignature)valueType, libraryClassName);
			if (tr != null)
				return tr;
			else
				return typeRef(((FunctionSignature)valueType).getFunction().getName().clone());
		}
		if (valueType instanceof TargettedTypeRef) {
			//TypeRef target = resolveTypeDef(((TargettedTypeRef) valueType).getTarget(), callerLibraryClass);
			TypeRef target = ((TargettedTypeRef) valueType).getTarget();
			
			boolean staticallySized = valueType instanceof ArrayRef && ((ArrayRef)valueType).hasStaticStorageSize();
			
			TypeRef convArgType = null;
			JavaPrim prim = getPrimitive(target, libraryClassName);
			if (prim != null) {
				if (prim == JavaPrim.Void)
					return typeRef(result.config.runtime.pointerClass);
				else
					convArgType = primRef(prim);
			} else {
				Identifier name = null;
				if (target instanceof SimpleTypeRef)
					name = ((SimpleTypeRef) target).getName();
				else if (target instanceof Struct) {
					Struct struct = (Struct)target;
					if (struct == null) {
						valueType =  resolveTypeDef(original, libraryClassName, true, false);
						struct = null;
					} else {
						name = result.declarationsConverter.getActualTaggedTypeName(struct);
					}
				} else if (target instanceof FunctionSignature) {
					TypeRef tr = findCallbackRef((FunctionSignature)target, libraryClassName);
					if (tr != null) {
						if (valueType instanceof TypeRef.ArrayRef) {
							return new TypeRef.ArrayRef(tr);
						} else {
							return tr;
						}
					}
					//else
					//	return typeRef(((FunctionSignature)valueType).getFunction().getName());
				} else if (target instanceof Pointer) {
					if (conversionMode == TypeConversionMode.NativeParameter)
						return typeRef(PointerByReference.class);
					
					Pointer pt = ((Pointer)target);
					TypeRef ptarget = pt.getTarget();
					if (ptarget instanceof SimpleTypeRef) {
						SimpleTypeRef ptargett = (SimpleTypeRef) ptarget;
						Identifier tname = ptargett.getName();
						if (result.structsFullNames.contains(tname)) {
							if (conversionMode == TypeConversionMode.FieldType)
								return typeRef(PointerByReference.class);
							else
								return new ArrayRef(typeRef(ident(ptargett.getName(), "ByReference")));
						} else if ((tname = result.findFakePointer(tname)) != null)
							return new ArrayRef(typeRef(tname.clone()));
					}
				}
				if (name != null) {
					/// Pointer to Objective-C class ?
					convArgType = findObjCClass(name);
					boolean isQualStruct = result.structsFullNames.contains(name);
					if (convArgType == null || isQualStruct) {
						/// Pointer to C structure
						Identifier structRef = isQualStruct ? name : findStructRef(name, libraryClassName);
						if (structRef != null) {//result.cStructNames.contains(name)) {
			 				switch (conversionMode) {
								case ExpressionType:
								case FieldType:
									convArgType = valueType instanceof TypeRef.ArrayRef ?
											typeRef(structRef) :
											typeRef(ident(structRef, ident("ByReference")));
									if (valueType instanceof Pointer)
										return convArgType;
									break;
								default:
									if (isQualStruct && 
											(valueType instanceof ArrayRef) && (
												conversionMode == TypeConversionMode.NativeParameterWithStructsPtrPtrs ||
												conversionMode == TypeConversionMode.PrimitiveOrBufferParameter
											))
										return arrayRef(typeRef(structRef));
									convArgType = typeRef(structRef);
									if (valueType instanceof Pointer)
										return convArgType;
									break;
							}
						} else {
							try {
								convArgType = convertTypeToJNA(target, conversionMode, libraryClassName);
                                /*if (result.isUndefinedType(convArgType)) {
                                    if (allowFakePointers && original instanceof SimpleTypeRef)
                                        return typeRef(result.getFakePointer(libraryClassName, ((SimpleTypeRef)original).getName().clone()));
                                    else
                                        convArgType = typeRef(result.config.runtime.pointerClass);
                                }*/

								if (convArgType != null && result.callbacksFullNames.contains(ident(convArgType.toString())) && !(valueType instanceof ArrayRef)) {
									TypeRef tr = typeRef(result.config.runtime.pointerClass);
									if (!result.config.noComments)
										tr.setCommentBefore("@see " + convArgType);
									return tr;
								}
								prim = getPrimitive(convArgType, libraryClassName);
							} catch (UnsupportedConversionException ex) {
								//convArgType = null;//return typeRef(result.config.runtime.pointerClass);
								if (valueType instanceof TypeRef.Pointer && 
										target instanceof TypeRef.SimpleTypeRef &&
										result.config.features.contains(JNAeratorConfig.GenFeatures.TypedPointersForForwardDeclarations) &&
										allowFakePointers
										) {

                                    if (isResolved((SimpleTypeRef) target)) {
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
                    return new ArrayRef(convArgType);
                case PrimitiveOrBufferParameter:
                    if (!result.config.noPrimitiveArrays && (target.getModifiers().contains(ModifierType.Const)
                            || valueType.getModifiers().contains(ModifierType.Const))) {
                        return new ArrayRef(convArgType);
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
                            return (TypeRef)typeRef(result.config.runtime.pointerClass).importComments(convArgType);
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
            if (target instanceof Pointer) {
                return typeRef(PointerByReference.class);
            }

            if (allowUnknownPointers) {
                return typeRef(result.config.runtime.pointerClass);
            }
        }
        if (valueType instanceof SimpleTypeRef) {
            Identifier name = ((SimpleTypeRef) valueType).getName();
            if (name == null) {
                throw new UnsupportedConversionException(valueType, null);
            }

            boolean isQualStruct = result.structsFullNames.contains(name);
            //isQualCallback = result.callbacksFullNames.contains(name);
            if (!isQualStruct && isResolved((SimpleTypeRef) valueType)) {
                return valueType;
            }

            if (name instanceof SimpleIdentifier) {
                TypeRef tr = findObjCClass(name);
                if (tr == null) {
                    tr = findObjCClass(new SimpleIdentifier(((SimpleIdentifier) name).getName()));
                }
                if (tr != null) {
                    return tr;
                }
            }
            Identifier structRef = isQualStruct ? name : findStructRef(name, libraryClassName);
            if (structRef != null) {
                switch (conversionMode) {
                    case PointedValue:
                    case FieldType:
                        return typeRef(structRef);
                    default:
                        return typeRef(ident(structRef, "ByValue"));
                }
            }

            TypeRef callbackRef = findCallbackRef(name, libraryClassName);
            if (callbackRef != null) {
                return callbackRef;
            }

            SimpleTypeRef enumTypeRef = findEnum(name, libraryClassName);
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

        if (valueType instanceof SimpleTypeRef && allowFakePointers) {
            //return typeRef(result.getUndefinedType(libraryClassName, ((SimpleTypeRef)valueType).getName().clone()));
            return typeRef(result.getFakePointer(libraryClassName, ((SimpleTypeRef)valueType).getName().clone()));
        }
        unknownTypes.add(String.valueOf(valueType));
        throw new UnsupportedConversionException(valueType, null);
    }
    static Map<String, Class<?>> predefObjCClasses = new HashMap<String, Class<?>>();

    static {
        predefObjCClasses.put("id", ObjCObject.class);//org.rococoa.ID.class);
        predefObjCClasses.put("SEL", org.rococoa.Selector.class);
        predefObjCClasses.put("IMP", com.sun.jna.Pointer.class);
        predefObjCClasses.put("Class", ObjCClass.class);
        predefObjCClasses.put("Protocol", ObjCClass.class);
        predefObjCClasses.put("NSObject", NSObject.class);
        //predefObjCClasses.put("NSClass", NSClass.class);
    }

    public Identifier findObjCClassIdent(Identifier name) {
        if (name instanceof SimpleIdentifier) {
            SimpleIdentifier sname = (SimpleIdentifier) name;
            String n = sname.getName();
            if (n.equals("id")
                    && sname.getTemplateArguments().size() == 1/* &&
                    conversionMode != TypeConversionMode.NativeParameter &&
                    conversionMode != TypeConversionMode.NativeParameterWithStructsPtrPtrs*/) {
                Expression x = sname.getTemplateArguments().get(0);
                TypeRefExpression trx = x instanceof TypeRefExpression ? (TypeRefExpression) x : null;
                SimpleTypeRef str = trx.getType() instanceof SimpleTypeRef ? (SimpleTypeRef) trx.getType() : null;
                if (str != null) {
                    name = str.getName();
                }
            }
        }

        Class<?> class1 = predefObjCClasses.get(name.toString());
        if (class1 != null) {
            return ident(class1);
        }

        Struct s = result.getObjcCClassOrProtocol(name);
        if (s != null) {
            return result.objectiveCGenerator.getFullClassName(s);
        }
        return null;
    }

    public TypeRef findObjCClass(Identifier name) {
        return typeRef(findObjCClassIdent(name));
    }

    protected TypeRef arrayRef(TypeRef tr) {
        ArrayRef arrayRef;
        if (tr instanceof ArrayRef) {
            arrayRef = (ArrayRef) tr;
            arrayRef.addDimension(new Expression.EmptyArraySize());
        } else {
            arrayRef = new ArrayRef(tr);
        }
        return arrayRef;
    }
    Set<String> unknownTypes = new HashSet<String>();

    public static <A, B> Pair<A, B> pair(A a, B b) {
        return new Pair<A, B>(a, b);
    }

    public static Pair<Expression, TypeRef> typed(Expression a, TypeRef b) {
        return new Pair<Expression, TypeRef>(a, b);
    }

    public boolean isString(Expression val) {
    		return val instanceof Constant && ((Constant)val).getType() == Constant.Type.String; // TODO use typer + type annotations !
    }
    
    public Constant.Type getConstantType(Expression expr) {
    		if (!(expr instanceof Constant))
    			return null;
    		return ((Constant)expr).getType();
    }

    public abstract Expression getEnumItemValue(EnumItem enumItem);
    
    public TypeRef convertToJavaType(Constant.Type type) {
        switch (type) {
            case Bool:
                return typeRef(Boolean.TYPE);
            case IntegerString:
            case UInt:
            case Int:
                return typeRef(Integer.TYPE);
            case LongString:
            case ULong:
            case Long:
                return typeRef(Long.TYPE);
            case Short:
                return typeRef(Short.TYPE);
            case Byte:
                return typeRef(Byte.TYPE);
            case Float:
                return typeRef(Float.TYPE);
            case Double:
                return typeRef(Double.TYPE);
            case String:
                return typeRef(String.class);
            default:
                return null;
        }
    }

    protected Expression sizeof(JavaPrim prim) {
        return prim.size.sizeof(prim);
    }

    protected Expression findEnumItem(EnumItem enumItem) {
        String library = result.getLibrary(enumItem);
        if (library == null) {
            return null;
        }

        Element parent = enumItem.getParentElement();
        if (parent == null || !(parent instanceof Enum)) {
            return null;
        }

        Enum e = (Enum) parent;
        Identifier ident = ident(result.getLibraryClassFullName(library), result.declarationsConverter.getActualTaggedTypeName(e), ident(enumItem.getName()));
        return expr(typeRef(ident).setMarkedAsResolved(true));
    }
    /// @see http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
    public static Set<String> JAVA_KEYWORDS = new HashSet<String>(Arrays.asList(
            "null",
            "true",
            "false",
            "abstract",
            "continue",
            "for",
            "new",
            "switch",
            "assert",
            "default",
            "goto",
            "package",
            "synchronized",
            "boolean",
            "do",
            "if",
            "private",
            "this",
            "break",
            "double",
            "implements",
            "protected",
            "throw",
            "byte",
            "else",
            "import",
            "public",
            "throws",
            "case",
            "enum",
            "instanceof",
            "return",
            "transient",
            "catch",
            "extends",
            "int",
            "short",
            "try",
            "char",
            "final",
            "interface",
            "static",
            "void",
            "class",
            "finally",
            "long",
            "strictfp",
            "volatile",
            "const",
            "float",
            "native",
            "super",
            "while",
            "wait" // not allowed for function names
            ));
    //static String keywords = " true false double float wait new null boolean return class public protected private ";

    public Identifier getValidJavaArgumentName(Identifier name) {
        return getValidJavaIdentifier(name);
    }

    public Identifier getValidJavaMethodName(Identifier name) {
        String nameStr = name.toString();
        String newName = null;
        if (nameStr.matches("operator[^\\w]+")) {
            String op = nameStr.substring("operator".length());
            //int nArgs = method.getArgs().size();
            String suffix = null;
            java.lang.Enum<?> e = Expression.getAnyOperator(op);
            if (e == null) {
                if (op.equals("()")) {
                    suffix = "parenthesis";
                } else if (op.equals("[]")) {
                    suffix = "brackets";
                } else if (op.equals("->")) {
                    suffix = "arrow";
                }
            } else {
                suffix = e.name();
            }

            if (suffix != null) {
                newName = "operator" + StringUtils.capitalize(suffix);
            }
        } else if (objectMethodNames.contains(nameStr)) {
        		newName = name + "$";
        	}/* else if (nameStr.startsWith("~")) {
        newName = getValidJavaIdentifierString(ident(nameStr.substring(1))) + "Destructor";
        }*/
        
        if (newName == null) {
            newName = getValidJavaIdentifierString(name);
        } else if (result.config.beautifyNames) {
            newName = beautify(newName);
        }

        return ident(newName);
    }

    String beautify(String name) {
        String newName = StringUtils.uncapitalize(StringUtils.underscoredToCamel(name));
        if (name.endsWith("_")) {
            newName += "$";
        }
        return newName;
    }

    public boolean isJavaKeyword(String name) {
        return JAVA_KEYWORDS.contains(name);
    }

    public Identifier getValidJavaIdentifier(Identifier name) {
        return ident(getValidJavaIdentifierString(name));
    }

    public String getValidJavaIdentifierString(Identifier name) {
        if (name == null) {
            return null;
        }

        if (isJavaKeyword(name.toString())) {
            return name + "$";
        } else {
            String newName = name.toString().replace('-', '_').replaceAll("[^\\w]", "\\$");
            if (result.config.beautifyNames) {
                newName = beautify(newName);
            }
            return newName;
        }
    }

    public static String toPrimString(JavaPrim prim) {
        return prim.name;
    }

    public Expression getJavaClassLitteralExpression(TypeRef tr) {
        JavaPrim prim = result.typeConverter.getPrimitive(tr, null);
        return prim != null ? classLiteral(prim.type) : typeLiteral(tr.clone());
    }

    public Expression getJavaClassLitteralExpression() {
        throw new UnsupportedOperationException(getClass().getName() + "." + toString() + " not handled !");
    }
    
    
    public Pair<Expression, TypeRef> convertExpressionToJava(Expression x, Identifier libraryClassName, boolean promoteNativeLongToLong) throws UnsupportedConversionException {
        Pair<Expression, TypeRef> res = null;
        if (x instanceof Expression.AssignmentOp) {
            Pair<Expression, TypeRef> convTarget = convertExpressionToJava(((Expression.AssignmentOp) x).getTarget(), libraryClassName, promoteNativeLongToLong),
                    convValue = convertExpressionToJava(((Expression.AssignmentOp) x).getValue(), libraryClassName, promoteNativeLongToLong);

            res = typed(expr(convTarget.getFirst(), Expression.AssignmentOperator.Equal, convValue.getFirst()), convTarget.getSecond());
        } else if (x instanceof Expression.BinaryOp) {
            Expression.BinaryOp bop = (Expression.BinaryOp) x;
            Pair<Expression, TypeRef> conv1 = convertExpressionToJava(bop.getFirstOperand(), libraryClassName, promoteNativeLongToLong),
                    conv2 = convertExpressionToJava(bop.getSecondOperand(), libraryClassName, promoteNativeLongToLong);

            if (conv1 != null && conv2 != null) {
                TypeRef t1 = conv1.getSecond(), t2 = conv2.getSecond();
                Expression x1 = conv1.getFirst(), x2 = conv2.getFirst();

                String s1 = String.valueOf(t1), s2 = String.valueOf(t2);
                TypeRef tr = null;
                if (bop.getOperator().givesBool)
                    tr = typeRef(boolean.class);
                else {
                    if (s1.equals(s2)) {
                        tr = t1;
                    } else {
                        JavaPrim p1 = getPrimitive(t1, null), p2 = getPrimitive(t2, null);
                        if (p1 != null && p2 != null) {
                            switch (bop.getOperator()) {
                                case LeftShift:
                                case RightShift:
                                case SignedRightShift:
                                    tr = t1;
                                    break;
                                default:
                                    for (JavaPrim p : new JavaPrim[]{
                                                JavaPrim.Double, JavaPrim.Float,
                                                JavaPrim.Long, JavaPrim.NativeSize, JavaPrim.NativeLong, JavaPrim.Int,
                                                JavaPrim.Short, JavaPrim.Byte
                                            }) {
                                        if (p1 == p || p2 == p) {
                                            if (promoteNativeLongToLong && (p == JavaPrim.NativeLong || p == JavaPrim.NativeSize)) {
                                                p = JavaPrim.Long;
                                            }
                                            tr = primRef(p);
                                            break;
                                        }
                                    }
                            }

                        }
                    }
                }
                res = typed(expr(x1, ((Expression.BinaryOp) x).getOperator(), x2), tr);
            }
        } else if (x instanceof Expression.UnaryOp) {
            Expression.UnaryOperator op = ((Expression.UnaryOp) x).getOperator();
            if (op == Expression.UnaryOperator.Not) {
                throw new UnsupportedConversionException(x, null); // TODO handle this properly ?
            }
            Pair<Expression, TypeRef> conv = convertExpressionToJava(((Expression.UnaryOp) x).getOperand(), libraryClassName, promoteNativeLongToLong);

            res = typed(expr(op, conv.getFirst()), conv.getSecond());
        } else if (x instanceof Expression.Constant) {
            Class<?> c = null;
            Expression.Constant jc = ((Expression.Constant) x).asJava();
            switch (jc.getType()) {
                case Byte:
                    c = Byte.TYPE;
                    break;
                case Char:
                    c = Character.TYPE;
                    break;
                case Double:
                    c = Double.TYPE;
                    break;
                case Float:
                    c = Float.TYPE;
                    break;
                case Int:
                case UInt:
                case IntegerString:
                    c = Integer.TYPE;
                    break;
                case ULong:
                case Long:
                case LongString:
                    c = Long.TYPE;
                    break;
                case Short:
                    c = Short.TYPE;
                    break;
                case String:
                    c = String.class;
                    break;
            }
            if (c != null) {
                res = typed(((Expression.Constant) x).asJava(), typeRef(c));
            }

        } else if (x instanceof Expression.TypeRefExpression) {

            Expression.TypeRefExpression tre = (Expression.TypeRefExpression) x;
            TypeRef tr = tre.getType();
            if (tr instanceof TypeRef.SimpleTypeRef) {
                TypeRef.SimpleTypeRef str = (TypeRef.SimpleTypeRef) tr;
                Identifier ident = str.getName();
                if (ident != null) {
                    if (result.enumItemsFullName.contains(ident)) {
                        res = typed(tre, typeRef(Integer.TYPE));
                    }
                }
            }
            if (res == null) {
                if (tr.isMarkedAsResolved()) {
                    res = typed(tre, tr);
                } else {
                    TypeRef conv = convertTypeToJNA(tr, TypeConversionMode.ExpressionType, libraryClassName);
                    res = typed(new Expression.TypeRefExpression(conv), conv);
                }
            }
        } else if (x instanceof Expression.VariableRef) {
            Expression.VariableRef fr = (Expression.VariableRef) x;
            Identifier name = fr.getName();
            if (name != null) {
                Define define = result.defines.get(name);
                if (define != null && define.getValue() != null) {
                    if (x.toString().equals(define.getValue().toString())) {
                        res = null; // avoid some nasty loops
                    } else {
                        Expression defineValue = define.getValue();
                        if (defineValue instanceof Expression.Constant) {
                            Expression.Constant constant = (Expression.Constant) defineValue;
                            res = typed(findDefine(name), convertToJavaType(constant.getType()));
                        }

                        if (res == null) {
                            res = convertExpressionToJava(defineValue, libraryClassName, promoteNativeLongToLong);
                        }
                    }
                } else {
                    String sname = name.toString();
                    if (sname.equals("True") || sname.equals("true")) {
                        res = typed(expr(Expression.Constant.Type.Bool, true), primRef(JavaPrim.Boolean));
                    } else if (sname.equals("False") || sname.equals("false")) {
                        res = typed(expr(Expression.Constant.Type.Bool, false), primRef(JavaPrim.Boolean));
                    } else {
                        Enum.EnumItem enumItem = result.enumItems.get(name);
                        if (enumItem != null) {
                            res = typed(getEnumItemValue(enumItem), typeRef(Long.TYPE));
                        } else {
                            VariablesDeclaration constant = result.globalVariablesByName.get(name);
                            if (constant != null) {
                                res = typed(varRef(findRef(name, constant, libraryClassName, true)), null);
                            } else {
                                res = typed(new Expression.VariableRef(name), null);
                            }
                        }
                    }
                }
            }
        }
        if (res == null) {
//			return convertExpressionToJava(x);
            throw new UnsupportedConversionException(x, null);
        }
        if (res.getFirst() == null) {
            return null;
        }
        res.getFirst().setParenthesis(x.getParenthesis());
        return (Pair<Expression, TypeRef>) res;
    }
}
