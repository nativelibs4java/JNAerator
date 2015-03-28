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

import com.ochafik.lang.SyntaxUtils;
import com.ochafik.lang.jnaerator.JNAeratorConfig.GenFeatures;
import static com.ochafik.lang.jnaerator.TypeConversion.ConvType;
import static com.ochafik.lang.jnaerator.TypeConversion.JavaPrim;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.Declarator.ArrayDeclarator;
import com.ochafik.lang.jnaerator.parser.Declarator.MutableByDeclarator;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.parser.Enum;
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
import com.nativelibs4java.jalico.Pair;
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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.bridj.util.DefaultParameterizedType;
import org.rococoa.ObjCClass;
import org.rococoa.ObjCObject;
import org.rococoa.cocoa.foundation.NSObject;

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
        PrimitiveReturnType,
        PointedValue
    }
    public Map<JavaPrim, Class<? extends ByReference>> primToByReference = new LinkedHashMap<JavaPrim, Class<? extends ByReference>>();
    public Map<JavaPrim, Class<? extends Global>> primToGlobal = new LinkedHashMap<JavaPrim, Class<? extends Global>>();
    public Map<JavaPrim, Class<? extends Buffer>> primToBuffer = new LinkedHashMap<JavaPrim, Class<? extends Buffer>>();
    public final Set<String> byReferenceClassesNames = new HashSet<String>();

    public boolean isObjCppPrimitive(String s) {
        return result.isObjCppPrimitive(s);
    }

    public enum JavaPrim {

        Void(java.lang.Void.TYPE, null, ESize.Zero),
        Char(Character.TYPE, Character.class, ESize.CharSize),
        Long(java.lang.Long.TYPE, java.lang.Long.class, ESize.Eight),
        Int(Integer.TYPE, Integer.class, ESize.Four),
        Short(java.lang.Short.TYPE, java.lang.Short.class, ESize.Two),
        Byte(java.lang.Byte.TYPE, java.lang.Byte.class, ESize.One),
        Boolean(java.lang.Boolean.TYPE, java.lang.Boolean.class, ESize.One),
        Float(java.lang.Float.TYPE, java.lang.Float.class, ESize.Four),
        Double(java.lang.Double.TYPE, java.lang.Double.class, ESize.Eight),
        ComplexDouble(null, null, ESize.Sixteen),
        NativeLong(com.sun.jna.NativeLong.class, com.sun.jna.NativeLong.class, ESize.StaticSizeField),
        NativeSize(NativeSize.class, NativeSize.class, ESize.StaticSizeField),
        NativeTime(null, null, ESize.StaticSizeField),
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
            Sixteen(expr(16)),
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
                nameToPrim = new LinkedHashMap<String, JavaPrim>();
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
            this.name = type == null ? null : type.getName();
            this.isPrimitive = type == null || type.isPrimitive();
            this.simpleName = type == null ? null : type.getSimpleName();
        }
    }

    protected TypeRef functionPointerTypeRef(TypeRef.FunctionSignature fs) {
        return fs;
    }
    
    protected TypeRef pointerTypeRef(TypeRef targetTypeRef) {
        return typeRef(result.config.runtime.pointerClass);
    }
    
    public Expression typeLiteral(TypeRef c) {
        if (c == null) {
            return null;
        }
        if (c.toString().equals("?")) {
            return new Expression.Cast(typeRef(Type.class), Constant.newNull());
        }
        return memberRef(expr(c), MemberRefStyle.Dot, "class");
    }

    protected abstract JavaPrim getCppBoolMappingType();

    public void initTypes() {

        result.prim("void", JavaPrim.Void);
        result.prim("VOID", JavaPrim.Void);

        result.prim("UTF32Char", JavaPrim.Int);
        result.prim("unichar", JavaPrim.Char);

        result.prim("int64_t", JavaPrim.Long);
        result.prim("uint64_t", JavaPrim.Long);
        result.prim("u_int64_t", JavaPrim.Long);
        result.prim("long long", JavaPrim.Long);
        result.prim("long long int", JavaPrim.Long);
        result.prim("long int", JavaPrim.Int);
        result.prim("LONGLONG", JavaPrim.Long);
        result.prim("ULONGLONG", JavaPrim.Long);
        result.prim("INT", JavaPrim.Int);
        result.prim("UINT", JavaPrim.Int);
        result.prim("SHORT", JavaPrim.Short);
        result.prim("USHORT", JavaPrim.Short);
        result.prim("CHAR", JavaPrim.Byte);
        result.prim("byte", JavaPrim.Byte);
        result.prim("BYTE", JavaPrim.Byte);
        result.prim("UBYTE", JavaPrim.Byte);
        result.prim("DOUBLE", JavaPrim.Double);
        result.prim("FLOAT", JavaPrim.Float);
        result.prim("WORD", JavaPrim.Short);
        result.prim("DWORD", JavaPrim.Int);
        result.prim("DWORD64", JavaPrim.Long);
        result.prim("LONG64", JavaPrim.Long);
        result.prim("UInt64", JavaPrim.Long);
        result.prim("SInt64", JavaPrim.Long);
        result.prim("__int64", JavaPrim.Long);
        result.prim("__int64_t", JavaPrim.Long);

        result.prim("int32_t", JavaPrim.Int);
        result.prim("uint32_t", JavaPrim.Int);
        result.prim("__int32_t", JavaPrim.Int);
        result.prim("__uint32_t", JavaPrim.Int);
        result.prim("u_int32_t", JavaPrim.Int);
        result.prim("uint32", JavaPrim.Int);
        result.prim("int32", JavaPrim.Int);
        result.prim("int", JavaPrim.Int);
        //prim("NSUInteger", JavaPrim.NativeSize);
        //prim("NSInteger", JavaPrim.NativeSize);
        result.prim("SInt32", JavaPrim.Int);
        result.prim("UInt32", JavaPrim.Int);
        result.prim("GLint", JavaPrim.Int);
        result.prim("GLuint", JavaPrim.Int);
        result.prim("GLenum", JavaPrim.Int);
        result.prim("GLsizei", JavaPrim.Int);

        result.prim("__int32", JavaPrim.Int);

        result.prim("NSInteger", JavaPrim.NSInteger);
        result.prim("NSUInteger", JavaPrim.NSUInteger);
        result.prim("CGFloat", JavaPrim.CGFloat);

        JavaPrim longPrim = result.config.gccLong ? JavaPrim.NativeSize : JavaPrim.NativeLong;
        result.prim("long", longPrim);
        result.prim("LONG", longPrim);
        result.prim("ULONG", longPrim);

        result.prim("time_t", JavaPrim.NativeTime);

        JavaPrim sizePrim = result.config.sizeAsLong ? longPrim : JavaPrim.NativeSize;
        result.prim("size_t", sizePrim);
        result.prim("ptrdiff_t", sizePrim);
        result.prim("__darwin_size_t", JavaPrim.NativeSize);

        result.prim("complex double", JavaPrim.ComplexDouble);

        result.prim("int16_t", JavaPrim.Short);
        result.prim("uint16_t", JavaPrim.Short);
        result.prim("__int16_t", JavaPrim.Short);
        result.prim("__uint16_t", JavaPrim.Short);
        result.prim("u_int16_t", JavaPrim.Short);
        result.prim("uint16", JavaPrim.Short);
        result.prim("int16", JavaPrim.Short);
        result.prim("SInt16", JavaPrim.Short);
        result.prim("UInt16", JavaPrim.Short);
        result.prim("short", JavaPrim.Short);
        result.prim("WCHAR", JavaPrim.Short);
        result.prim("wchar_t", result.config.wcharAsShort ? JavaPrim.Short : JavaPrim.Char);

        result.prim("__int16", JavaPrim.Short);


        result.prim("int8_t", JavaPrim.Byte);
        result.prim("uint8_t", JavaPrim.Byte);
        result.prim("u_int8_t", JavaPrim.Byte);
        result.prim("__uint8_t", JavaPrim.Byte);
        result.prim("__int8_t", JavaPrim.Byte);
        result.prim("SInt8", JavaPrim.Byte);
        result.prim("UInt8", JavaPrim.Byte);
        result.prim("char", JavaPrim.Byte);
        result.prim("unsigned char", JavaPrim.Byte);
        result.prim("__unsigned char", JavaPrim.Byte);
        result.prim("signed char", JavaPrim.Byte);
        result.prim("__signed char", JavaPrim.Byte);
        result.prim("SignedByte", JavaPrim.Byte);

        result.prim("__int8", JavaPrim.Byte);

        result.prim("float", JavaPrim.Float);
        result.prim("NSFloat", JavaPrim.Float);
        result.prim("CGFloat", JavaPrim.Float);

        result.prim("double_t", JavaPrim.Double);
        result.prim("double", JavaPrim.Double);
        result.prim("NSDouble", JavaPrim.Double);
        result.prim("CGDouble", JavaPrim.Double);

        JavaPrim cppBoolType = getCppBoolMappingType();
        result.prim("bool", cppBoolType);
        result.prim("Boolean", cppBoolType);
        result.prim("boolean_t", cppBoolType);



        primToByReference.put(JavaPrim.Int, IntByReference.class);
        primToByReference.put(JavaPrim.Char, (Class) CharByReference.class);
        primToByReference.put(JavaPrim.Short, ShortByReference.class);
        primToByReference.put(JavaPrim.Byte, ByteByReference.class);
        primToByReference.put(JavaPrim.Long, LongByReference.class);
        primToByReference.put(JavaPrim.Float, FloatByReference.class);
        primToByReference.put(JavaPrim.Double, DoubleByReference.class);
        primToByReference.put(JavaPrim.NativeLong, NativeLongByReference.class);
        primToByReference.put(JavaPrim.NativeSize, (Class) NativeSizeByReference.class);
        primToByReference.put(JavaPrim.NSInteger, (Class) NativeSizeByReference.class);
        primToByReference.put(JavaPrim.NSUInteger, (Class) NativeSizeByReference.class);
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

        TypeRef pInt = new TypeRef.Pointer(new Primitive("int"), Declarator.PointerStyle.Pointer);
        result.addManualTypeDef("intptr_t", pInt);
        result.addManualTypeDef("uintptr_t", pInt);

        // TODO: Add a windows failsafe mode that defines all the typedefs needed:
        // http://msdn.microsoft.com/en-us/library/windows/desktop/aa383751(v=vs.85).aspx
//        TypeRef pVoid = new TypeRef.Pointer(new Primitive("void"), Declarator.PointerStyle.Pointer);
//        result.addManualTypeDef("PVOID", pVoid);
//        result.addManualTypeDef("LPVOID", pVoid);
//        result.addManualTypeDef("LPCVOID", pVoid);
    }

    protected TypeRef findTypeRef(Identifier name, Identifier libraryClassName) {
        TypeRef tr;
        tr = findStructRef(name, libraryClassName);
        if (tr != null) {
            return tr;
        }

        tr = findEnum(name, libraryClassName);
        if (tr != null) {
            return tr;
        }

        tr = findCallbackRef(name, libraryClassName);
        if (tr != null) {
            return tr;
        }

        tr = findObjCClass(name);
        if (tr != null) {
            return tr;
        }

        tr = result.manualTypeDefs.get(name);
        if (tr != null) {
            return tr;
        }

        return null;
    }

    

    public TypeRef normalizeTypeRef(TypeRef tr) {
        return normalizeTypeRef(tr, new HashSet<Identifier>());
    }
    public TypeRef normalizeTypeRef(TypeRef tr, HashSet<Identifier> resolvedNames) {
        if (tr instanceof TypeRef.SimpleTypeRef) {
            Identifier name = ((TypeRef.SimpleTypeRef)tr).getName();
//            if (isObjCppPrimitive(name.toString())) {
//                return tr;
//            }
            TypeRef td = resolvedNames.add(name) ? result.getTypeDef(name) : null;
            if (td != null)
                return normalizeTypeRef(td, resolvedNames);
            
            Struct s = result.resolveStruct(name);
            if (s != null)
                return s;
            Enum e = result.resolveEnum(name);
            if (e != null)
                return e;
            TypeRef.FunctionSignature c = result.resolveCallback(name);
            if (c != null)
                return functionPointerTypeRef(c);
        }
        if (tr instanceof TypeRef.TargettedTypeRef) {
            TypeRef.TargettedTypeRef ttr = (TypeRef.TargettedTypeRef) tr;
            TypeRef target = ttr.getTarget();
            TypeRef ntarget = normalizeTypeRef(target, resolvedNames);
            if (ntarget != target) {
                ttr = (TypeRef.TargettedTypeRef) ttr.clone();
                ttr.setTarget(target);
            }
            return ttr;
        }
        if (tr instanceof TaggedTypeRef) {
            TaggedTypeRef ttr = (TaggedTypeRef) tr;
            if (ttr.isForwardDeclaration()) {
                Identifier name = ttr.getTag();
                if (ttr instanceof Struct) {
                    ttr = result.resolveStruct(name);
                } else if (ttr instanceof Enum) {
                    ttr = result.resolveEnum(name);
                }
                if (ttr != null)
                    return ttr;
            }
        }
        return tr;
    }
    

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

    public JavaPrim getPrimitive(TypeRef valueType) {
        if (!(valueType instanceof Primitive) && !(valueType instanceof JavaPrimitive)) {
            valueType = normalizeTypeRef(valueType);
        }
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
//        if ((isLong = valueType.hasModifier(ModifierType.Long)) || valueType.hasModifier(ModifierType.Short)) {
//        //if ((isLong = valueType.hasModifier(ModifierType.Long)) || valueType.hasModifier(ModifierType.Short)) {
//            str = (isLong ? "long " : "short ") + name;
//        } else {
        str = name.toString();
//        }

        JavaPrim type = result.resolvePrimitive(str);
        if ((type == JavaPrim.Int || type == JavaPrim.NativeLong) && longCount >= 1) {
            return JavaPrim.Long;
        }

        if (type == JavaPrim.Double && valueType.hasModifier(ModifierType._Complex)) {
            return JavaPrim.ComplexDouble;
        }
        return type;
    }

    public SimpleTypeRef findStructRef(Identifier name, Identifier libraryClassName) {
        return findStructRef(result.resolveStruct(name), libraryClassName);
    }

    public SimpleTypeRef findStructRef(Struct s, Identifier name, Identifier libraryClassName) {
        if (s == null || s.isForwardDeclaration()) {
            TypeRef td = result.getTypeDef(name);
            if (!(td instanceof Struct)) {
                return null;
            }
            s = (Struct) td;
        }
        if (s == null && result.config.runtime == JNAeratorConfig.Runtime.BridJ) {
            String ns = name.toString();
            Class<?> cl = null;
            if (ns.equals("IUnknown")) {
                cl = org.bridj.cpp.com.IUnknown.class;
            } else if (ns.equals("GUID")) {
                cl = org.bridj.cpp.com.GUID.class;
            } else if (ns.equals("RECT")) {
                cl = org.bridj.cpp.com.RECT.class;
            }

            if (cl != null) {
                return typeRef(ident(cl));
            }
        }
        return typeRef(getTaggedTypeIdentifierInJava(s));
        /*
         name = result.declarationsConverter.getActualTaggedTypeName((TaggedTypeRef) pair.getFirst().getValueType());

         return findRef(name, s, libraryClassName, !result.config.putTopStructsInSeparateFiles);
         } else {
         return result.getTaggedTypeIdentifierInJava(s);
         //name = result.declarationsConverter.getActualTaggedTypeName(s);
         }*/
    }

    public SimpleTypeRef findStructRef(Struct s, Identifier libraryClassName) {
        if (s == null) {
            return null;
        }
        switch (s.getType()) {
            case ObjCClass:
            case ObjCProtocol:
                return typeRef(result.objectiveCGenerator.getFullClassName(s));
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
  
    protected Identifier packageMember(Identifier libraryPackage, Identifier name) {
        return ident(libraryPackage, name);
    }
    protected Identifier libMember(Identifier libClass, Identifier libraryClassName, Identifier member) {
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

            return ident(getTaggedTypeIdentifierInJava(parentStruct), name);
        }
        return libMember(result.getLibraryClassFullName(library), libraryClassName, name);
    }

    public SimpleTypeRef findEnum(Identifier name, Identifier libraryClassName) {
        return findEnum(result.resolveEnum(name), libraryClassName);
    }

    public SimpleTypeRef findEnum(Enum s, Identifier libraryClassName) {
        if (s == null) {
            return null;
        }

        if (result.config.runtime == JNAeratorConfig.Runtime.BridJ) {
            return typeRef(getTaggedTypeIdentifierInJava(s));
        }

        Identifier name = result.declarationsConverter.getActualTaggedTypeName(s);

        String library = result.getLibrary(s);
        if (library == null) {
            return null;
        }
        Identifier libClass = result.getLibraryClassFullName(library);
        SimpleTypeRef tr = new SimpleTypeRef("int");
        if (result.config.features.contains(JNAeratorConfig.GenFeatures.EnumTypeLocationComments)) {
            tr.setCommentBefore("@see " + (SyntaxUtils.equal(libClass, libraryClassName) ? name : libClass + "#" + name));
        }
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
        if (parent instanceof TypeRef.Pointer)
            parent = parent.getParentElement();

        boolean firstParent = true;
        while (parent != null) {
            if (parent instanceof Struct) {
                SimpleTypeRef parentRef = findStructRef((Struct) parent, null);
                if (parentRef != null) {
                    parentIdent = parentRef.getName();
                    break;
                }
            }
            if (firstParent) {
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
        FunctionSignature s = result.resolveCallback(name);
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
        String library = s == null ? null : result.getLibrary(s);
        if (library == null) {
            return null;
        }

//		Struct parentStruct = s.findParentOfType(Struct.class);
//		if (parentStruct != null && (parentStruct.getType() == Struct.Type.ObjCClass || parentStruct.getType() == Struct.Type.ObjCProtocol)) {
//			Identifier structName = result.declarationsConverter.getActualTaggedTypeName(parentStruct);
//			return
//				typeRef(ident(structName, inferCallBackName(s, true, true)));
//		}
        Identifier identifier = s.getResolvedJavaIdentifier();
        if (identifier == null) {
            throw new UnsupportedConversionException(s, null);
        }
        return typeRef(identifier);
//		return typeRef(libMember(result.getLibraryClassFullName(library), callerLibraryClass, inferCallBackName(s, true, true)));
        //typeRef(ident(result.getLibraryClassFullName(library), inferCallBackName(s, true)));
    }

    static TypeRef primRef(Element element, JavaPrim p) {
        if (p == null) {
            return null;
        }
        if (p.type == null) {
            throw new UnsupportedConversionException(element, "Primitive without known type for this runtime: " + p);
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

        Enum,
        Pointer,
        FunctionSignature,
        Primitive,
        Struct,
        NativeLong,
        NativeSize,
        NativeTime,
        ComplexDouble,
        Void,
        Callback,
        Default
    }
    static Map<String, Pair<Integer, Class<?>>> buffersAndArityByType = new LinkedHashMap<String, Pair<Integer, Class<?>>>();
    static Map<String, Pair<Integer, Class<?>>> arraysAndArityByType = new LinkedHashMap<String, Pair<Integer, Class<?>>>();
    static Set<String> objectMethodNames = new HashSet<String>();

    static {
        for (Method method : Object.class.getDeclaredMethods()) {
            objectMethodNames.add(method.getName());
        }

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
                }
            }
        }
    }
    Pattern wstringPat = Pattern.compile("((__)?const ){1,2}wchar_t\\*"),
            stringPat = Pattern.compile("((__)?const ){1,2}char\\*"),
            wstringPtrPtrPat = Pattern.compile("((__)?const ){1,2}wchar_t\\*\\*"),
            stringPtrPtrPat = Pattern.compile("((__)?const ){1,2}char\\*\\*");

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
    static Map<String, Class<?>> predefObjCClasses = new LinkedHashMap<String, Class<?>>();

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

        Struct s = result.resolveObjCClass(name);
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
        return val instanceof Constant && ((Constant) val).getType() == Constant.Type.String; // TODO use typer + type annotations !
    }

    public Constant.Type getConstantType(Expression expr) {
        if (!(expr instanceof Constant)) {
            return null;
        }
        return ((Constant) expr).getType();
    }

    public abstract Expression getEnumItemValue(EnumItem enumItem, boolean forceConstant);

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
        Identifier enumItemName = ident(enumItem.getName());
        enumItemName.resolveLastSimpleIdentifier().setJavaStaticImportable(true);
        Identifier ident = ident(result.getLibraryClassFullName(library), result.declarationsConverter.getActualTaggedTypeName(e), enumItemName);
        return expr(typeRef(ident).setMarkedAsResolved(true));
    }
    /// @see http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
    public static Set<String> JAVA_OBJECT_METHODS = new HashSet<String>(Arrays.asList(
            "notify", "notifyAll", "equals", "finalize", "getClass", "hashCode", "clone", "toString", "wait" // not allowed for function names
            ));
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
            "while"));
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
        }
//        else if (result.config.beautifyNames) {
//            newName = beautify(newName);
//        }

        return ident(newName);
    }

    String beautify(String name, boolean isType) {
        String newName = StringUtils.underscoredToCamel(name);
        if (!isType) {
            newName = StringUtils.uncapitalize(newName);
        }

        if (name.endsWith("_")) {
            newName += "$";
        }
        return newName;
    }

    public boolean isJavaKeyword(String name) {
        return JAVA_KEYWORDS.contains(name)
                || JAVA_OBJECT_METHODS.contains(name); // not really keywords, but roughly same restrictions apply.
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
//            if (result.config.beautifyNames) {
//                newName = beautify(newName);
//            }
            return newName;
        }
    }

    public static String toPrimString(JavaPrim prim) {
        return prim.name;
    }

    public Expression getJavaClassLitteralExpression(TypeRef tr) {
        JavaPrim prim = result.typeConverter.getPrimitive(tr);
        return prim != null ? classLiteral(prim.type) : typeLiteral(tr.clone());
    }

    public Expression getJavaClassLitteralExpression() {
        throw new UnsupportedOperationException(getClass().getName() + "." + toString() + " not handled !");
    }

    public Pair<Expression, TypeRef> convertExpressionToJava(Expression x, Identifier libraryClassName, boolean promoteNativeLongToLong, boolean forceConstants, Map<String, Pair<Expression, TypeRef>> mappings) throws UnsupportedConversionException {
        Pair<Expression, TypeRef> res = null;
        if (x instanceof Expression.AssignmentOp) {
            Pair<Expression, TypeRef> convTarget = convertExpressionToJava(((Expression.AssignmentOp) x).getTarget(), libraryClassName, promoteNativeLongToLong, forceConstants, mappings),
                    convValue = convertExpressionToJava(((Expression.AssignmentOp) x).getValue(), libraryClassName, promoteNativeLongToLong, forceConstants, mappings);

            res = typed(expr(convTarget.getFirst(), Expression.AssignmentOperator.Equal, convValue.getFirst()), convTarget.getSecond());
        } else if (x instanceof Expression.BinaryOp) {
            Expression.BinaryOp bop = (Expression.BinaryOp) x;
            Pair<Expression, TypeRef> conv1 = convertExpressionToJava(bop.getFirstOperand(), libraryClassName, promoteNativeLongToLong, forceConstants, mappings),
                    conv2 = convertExpressionToJava(bop.getSecondOperand(), libraryClassName, promoteNativeLongToLong, forceConstants, mappings);

            if (conv1 != null && conv2 != null) {
                TypeRef t1 = conv1.getSecond(), t2 = conv2.getSecond();
                Expression x1 = conv1.getFirst(), x2 = conv2.getFirst();

                String s1 = String.valueOf(t1), s2 = String.valueOf(t2);
                TypeRef tr = null;
                if (bop.getOperator().givesBool) {
                    tr = typeRef(boolean.class);
                } else {
                    if (s1.equals(s2)) {
                        tr = t1;
                    } else {
                        // t1 & t2 are already java primitives...
                        JavaPrim p1 = "long".equals(t1.toString()) ? JavaPrim.Long : getPrimitive(t1),
                                p2 = "long".equals(t2.toString()) ? JavaPrim.Long : getPrimitive(t2);
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
                                            tr = primRef(x, p);
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
            Pair<Expression, TypeRef> conv = convertExpressionToJava(((Expression.UnaryOp) x).getOperand(), libraryClassName, promoteNativeLongToLong, forceConstants, mappings);

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
            // The parser might recognize enum items incorrectly as a TypeRefExpression...
            Expression.TypeRefExpression tre = (Expression.TypeRefExpression) x;
            TypeRef tr = tre.getType();
            if (tr instanceof TypeRef.SimpleTypeRef) {
                TypeRef.SimpleTypeRef str = (TypeRef.SimpleTypeRef) tr;
                Identifier name = str.getName();
                if (name != null) {
                    if (result.enumItemsFullName.contains(name)) {
                        res = typed(tre, typeRef(Integer.TYPE));
                    }
//                    res = convertVariableRefToJava(name, libraryClassName, promoteNativeLongToLong);
                }
            }
            if (res == null) {
                if (tr.isMarkedAsResolved()) {
                    res = typed(tre, tr);
                }
//                } else {
//                    TypeRef conv = convertTypeToJNA(tr, TypeConversionMode.ExpressionType, libraryClassName);
//                    res = typed(new Expression.TypeRefExpression(conv), conv);
//                }
            }
        } else if (x instanceof Expression.VariableRef) {
            Identifier name = ((Expression.VariableRef) x).getName();
            Pair<Expression, TypeRef> mapping = mappings == null ? null : mappings.get(name.toString());
            if (mapping != null) {
                res = mapping;
            } else {
                res = convertVariableRefToJava(name, libraryClassName, promoteNativeLongToLong, forceConstants);
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

    static class EnumItemResult {

        public Enum.EnumItem originalItem;
        public Expression convertedValue, unconvertedValue, constantValue;
        public String comments;
        public String exceptionMessage;
        public Declaration errorElement;
    }

    protected Map<String, EnumItemResult> getEnumValuesAndCommentsByName(Enum e, Identifier libraryClassName) {
        Map<String, EnumItemResult> ret = new LinkedHashMap<String, EnumItemResult>();
        Integer lastAdditiveValue = null;
        Expression lastRefValue = null;
        boolean failedOnceForThisEnum = false;
        Map<String, Pair<Expression, TypeRef>> mappings = new LinkedHashMap<String, Pair<Expression, TypeRef>>();
        for (com.ochafik.lang.jnaerator.parser.Enum.EnumItem item : e.getItems()) {
            EnumItemResult res = new EnumItemResult();
            res.originalItem = item;
            try {
                if (item.getArguments().isEmpty()) {
                    // no explicit value
                    if (lastRefValue == null) {
                        if (lastAdditiveValue != null) {
                            lastAdditiveValue++;
                            res.unconvertedValue = expr(lastAdditiveValue);
                        } else {
                            if (item == e.getItems().get(0)) {
                                lastAdditiveValue = 0;
                                res.unconvertedValue = expr(lastAdditiveValue);
                            } else {
                                res.unconvertedValue = null;
                            }
                        }
                    } else {
                        // has a last reference value
                        if (lastAdditiveValue != null) {
                            lastAdditiveValue++;
                        } else {
                            lastAdditiveValue = 1;
                        }

                        res.unconvertedValue = expr(
                                lastRefValue.clone(),
                                Expression.BinaryOperator.Plus,
                                expr(lastAdditiveValue));
                    }
                } else {
                    // has an explicit value
                    failedOnceForThisEnum = false;// reset skipping
                    lastAdditiveValue = null;
                    lastRefValue = item.getArguments().get(0);
                    res.unconvertedValue = lastRefValue;
                    if (lastRefValue instanceof Expression.Constant) {
                        try {
                            lastAdditiveValue = ((Expression.Constant) lastRefValue).asInteger();
                            lastRefValue = null;
                        } catch (Exception ex) {
                        }
                    }
                }
                res.convertedValue = result.typeConverter.convertExpressionToJava(res.unconvertedValue, libraryClassName, true, false, mappings).getFirst();
                res.constantValue = result.typeConverter.convertExpressionToJava(res.unconvertedValue, libraryClassName, true, true, mappings).getFirst();
                mappings.put(item.getName(), typed(res.constantValue, typeRef(int.class)));
            } catch (Exception ex) {
                failedOnceForThisEnum = true;
                res.exceptionMessage = ex.toString();
            }
            failedOnceForThisEnum = failedOnceForThisEnum || res.errorElement != null;
            if (failedOnceForThisEnum) {
                res.errorElement = result.declarationsConverter.skipDeclaration(item);
            }

            ret.put(item.getName(), res);
        }
        return ret;
    }

    private Pair<Expression, TypeRef> convertVariableRefToJava(Identifier name, Identifier libraryClassName, boolean promoteNativeLongToLong, boolean forceConstants) {

        if (name != null) {
            Define define = result.defines.get(name);
            if (define != null && define.getValue() != null) {
                if (name.toString().equals(define.getValue().toString())) {
                    return null; // avoid some nasty loops
                } else {
                    Expression defineValue = define.getValue();
                    if (defineValue instanceof Expression.Constant) {
                        Expression.Constant constant = (Expression.Constant) defineValue;
                        return typed(findDefine(name), convertToJavaType(constant.getType()));
                    }

                    return convertExpressionToJava(defineValue, libraryClassName, promoteNativeLongToLong, forceConstants, null);
                }
            } else {
                String sname = name.toString();
                if (sname.equals("True") || sname.equals("true")) {
                    return typed(expr(Expression.Constant.Type.Bool, true), primRef(define, JavaPrim.Boolean));
                } else if (sname.equals("False") || sname.equals("false")) {
                    return typed(expr(Expression.Constant.Type.Bool, false), primRef(define, JavaPrim.Boolean));
                } else {
                    Enum.EnumItem enumItem = result.enumItems.get(name);
                    if (enumItem != null) {
                        return typed(getEnumItemValue(enumItem, forceConstants), typeRef(Integer.TYPE));
                    } else {
                        VariablesDeclaration constant = result.globalVariablesByName.get(name);
                        if (constant != null) {
                            return typed(varRef(findRef(name, constant, libraryClassName, true)), null);
                        } else {
                            return typed(new Expression.VariableRef(name), null);
                        }
                    }
                }
            }
        }
        return null;
    }

    public Identifier getTaggedTypeIdentifierInJava(TaggedTypeRef s) {
        return result.resolveFullTaggedTypeRef(s).getResolvedJavaIdentifier();
    }

    public Identifier computeTaggedTypeIdentifierInJava(TaggedTypeRef s) {
        Identifier name = result.declarationsConverter.getActualTaggedTypeName(s);
        if (name == null) {
            return null;
        }

        String library = result.getLibrary(s);
        if (library == null) {
            return null;
        }

        name = name.clone();
        Struct parentStruct = s.findParentOfType(Struct.class);
        //Struct parentStruct = s.findParentOfType(Struct.class);
        if (parentStruct != null && parentStruct != s) {
            return ident(getTaggedTypeIdentifierInJava(parentStruct), name);
        } else if ((s instanceof Struct) && (result.config.putTopStructsInSeparateFiles)) {// || result.config.runtime == JNAeratorConfig.Runtime.BridJ)) {
            return packageMember(result.getLibraryPackage(library), name);
            //return ident(result.getLibraryPackage(library), name);
        } else {
            return libMember(result.getLibraryClassFullName(library), null, name);
        }
    }

    public Identifier computeCallbackIdentifierInJava(FunctionSignature fs) {
        Identifier name = inferCallBackName(fs, false, false, null);
        if (name == null) {
            return null;
        }

        String library = result.getLibrary(fs);
        if (library == null) {
            return null;
        }

        name = name.clone();
        Struct parentStruct = fs.findParentOfType(Struct.class);
        //Struct parentStruct = s.findParentOfType(Struct.class);
        if (parentStruct != null) {
            return ident(getTaggedTypeIdentifierInJava(parentStruct), name);
        } else {
            return libMember(result.getLibraryClassFullName(library), null, name);
        }
    }
}
