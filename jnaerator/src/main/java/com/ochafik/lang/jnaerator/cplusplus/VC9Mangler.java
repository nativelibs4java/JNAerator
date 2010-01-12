/*
Copyright (c) 2009 Olivier Chafik, All Rights Reserved

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
package com.ochafik.lang.jnaerator.cplusplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ochafik.lang.jnaerator.Result;
import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.ModifiableElement;
import com.ochafik.lang.jnaerator.parser.Modifier;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.Declarator.PointerStyle;
import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;

public class VC9Mangler implements CPlusPlusMangler {
	
	public void mangleType(TypeRef tr, StringBuilder b, Result result, List<TypeRef> referenceList) {
		if (tr == null)
			tr = new TypeRef.SimpleTypeRef("void");
		
		/*for (int i = 0, len = referenceList.size(); i < len; i++) {
			if (referenceList.get(i).toString().equals(tr.toString())) {
				b.append(i);
				return;
			}
		}*/
		referenceList.add(tr);
		
		if (tr instanceof TypeRef.TargettedTypeRef) {
			if (tr instanceof TypeRef.Pointer && ((TypeRef.Pointer)tr).getPointerStyle() == PointerStyle.Reference)
				b.append("A");
			else if (tr instanceof TypeRef.ArrayRef)
				b.append("Q");
			else if (Modifier.__const.isContainedBy(tr.getModifiers()))
				b.append("Q");
			else
				b.append("P");
				//b.append("K");
			mangleStorageMod(tr, b, result);
			mangleType(((TypeRef.TargettedTypeRef)tr).getTarget(), b, result, referenceList);
		} else if (tr instanceof TypeRef.SimpleTypeRef) {
			SimpleTypeRef str = (SimpleTypeRef) tr;
			TypeRef resolved = result.typeConverter.resolveTypeDef(str, null, false);
			if (resolved != null && !resolved.toString().equals(str.toString())) {
				mangleType(resolved, b, result, referenceList);
				return;
			}
			if (str.getName() == null)
				str.setName(new SimpleIdentifier("int"));
			Primitive p = Primitive.parsePrimitive(str);
			String s = signatures.get(p);
			if (s != null)
				b.append(s);
			else {
				lenghtedName(str.getName().toString(), b);
			}
		} else if (tr instanceof TaggedTypeRef) {
			mangleTaggedTypeRef((TaggedTypeRef)tr, b, result);
		} else if (tr instanceof FunctionSignature) {
			FunctionSignature fs = (FunctionSignature) tr;
			b.append("PF");
			mangleType(fs.getFunction().getValueType(), b, result, referenceList);
			mangleArgs(fs.getFunction().getArgs(), b, result, referenceList);
			b.append("E");
		} else {
				throw new UnsupportedOperationException("Cannot mangle type references of class " + tr.getClass().getName() + " : '" + tr + "'");
		}
	}
	private void mangleTaggedTypeRef(TaggedTypeRef tr, StringBuilder b,
			Result result) {

		if (tr instanceof com.ochafik.lang.jnaerator.parser.Enum)
			b.append("W4"); // assume int storage
		else if (tr instanceof Struct) {
			Struct s = (Struct) tr;
			switch (s.getType()) {
			case CPPClass:
				b.append("V");
				break;
			case CStruct:
				b.append("U");
				break;
			case CUnion:
				b.append("T");
				break;
			}
		}
		qualify(b, tr.getOriginalTag(), tr.getNameSpace());
	}
	private void qualify(StringBuilder b, Identifier identifier, List<String> ns) {
		List<SimpleIdentifier> sis = identifier.resolveSimpleIdentifiers();
		for (int i = sis.size(); i-- != 0;)
			b.append(sis.get(i) + "@");
		
		for (int i = ns.size(); i-- != 0;)
			b.append(ns.get(i) + "@");
		b.append("@");
	}
	//F0@@YAXH@Z
	//F0@@YAXHX
	static void lenghtedName(String name, StringBuilder out) {
		out.append(name.length() + name);
	}
	/// @see http://en.wikipedia.org/wiki/Microsoft_Visual_C%2B%2B_Name_Mangling
	public String mangle(Function function, Result result) {
		Identifier name = function.getName();
		if (name == null)
			return null;
		
		StringBuilder b = new StringBuilder("?");
		b.append(function.getName());
		b.append("@");
		
		Element parent = function.getParentElement();
		List<Object> ns;
		boolean isMethod = parent instanceof Struct;
		if (isMethod) {
			ns = new ArrayList<Object>(parent.getNameSpace());
			ns.addAll(((Struct)parent).getTag().resolveSimpleIdentifiers());
		} else {
			ns = new ArrayList<Object>(function.getNameSpace());
		}
		
//		if (ns.isEmpty()) {
//			b.append("@Y");
//		} else {
			for (int i = ns.size(); i-- != 0;)
				b.append(ns.get(i) + "@");
//			b.append("@Q");
			if (Modifier.Static.isContainedBy(function.getModifiers()))
				b.append("@S");
			else if (function.getParentElement() instanceof Struct)
				b.append("@Q");
			else
				b.append("@Y");
//		}
		
		mangleStorageMod(function, b, result);
		
		
		mangleType(function.getValueType(), b, result, new ArrayList<TypeRef>());
		mangleArgs(function.getArgs(), b, result, new ArrayList<TypeRef>());
		b.append("Z");
		return b.toString();
	}
	private void mangleStorageMod(ModifiableElement e, StringBuilder b,
			Result result) {
		
		List<Modifier> modifiers = e.getModifiers();
		if (e instanceof Function) {
			Function f = (Function) e;
			
			/// @see http://www.kegel.com/mangle.html#calling
			if (Modifier.__fastcall.isContainedBy(modifiers))
				b.append("I");
			else if (Modifier.__stdcall.isContainedBy(modifiers))
				b.append("G");
			else //if (Modifier.__cdecl.isContainedBy(modifiers))
				b.append("A");
			if (f.getParentElement() instanceof Struct && !Modifier.Static.isContainedBy(modifiers))
				b.append("E");
		} else if (e instanceof TypeRef) {
			if (Modifier.__const.isContainedBy(modifiers))
				b.append("B");
			else
				b.append("A");
		}
		
	}
	private void mangleArgs(List<Arg> args, StringBuilder b, Result result,
			List<TypeRef> referenceList) {

		if (args.isEmpty())
			b.append("X");
		else {
			for (Arg arg : args) {
				TypeRef tr = arg.createMutatedType();
				mangleType(tr, b, result, referenceList);
			}
			b.append("@");
		}
	}
	static Map<Primitive, String> signatures = new HashMap<Primitive, String>();
	static {
		signatures.put(Primitive.Void, "X");
		signatures.put(Primitive.SChar, "C"); 
		signatures.put(Primitive.Char, "D"); 
		signatures.put(Primitive.UChar, "E"); 
		signatures.put(Primitive.Short, "F"); 
		signatures.put(Primitive.UShort, "G"); 
		signatures.put(Primitive.Int, "H"); 
		signatures.put(Primitive.UInt, "I"); 
		signatures.put(Primitive.Long, "J"); 
		signatures.put(Primitive.ULong, "K");
		signatures.put(Primitive.Float, "M"); 
		signatures.put(Primitive.Double, "N"); 
		signatures.put(Primitive.LongDouble, "O");
		signatures.put(Primitive.LongLong, "_J"); 
		signatures.put(Primitive.ULongLong, "_K");
		signatures.put(Primitive.Bool, "_N"); 
	}
	static Map<String, String> specialCodes = new HashMap<String, String>();
	static final String
		CONSTRUCTOR = "?0",
		DESTRUCTOR = "?1",
		VFTABLE = "?_7",
		VBTABLE = "?_8",
		VCALL = "?_9",
		TYPEOF = "?_A";
	
	static {

		specialCodes.put("new", "?2");
		specialCodes.put("delete", "?3");
		specialCodes.put("operator=", "?4");
		specialCodes.put("operator>>", "?5");
		
		specialCodes.put("operator/=", "?_0");
		specialCodes.put("operator%=", "?_1");
		specialCodes.put("operator>>=", "?_2");
		specialCodes.put("operator<<=", "?_3");
		specialCodes.put("operator&=", "?_4");
		specialCodes.put("operator|=", "?_5");
		specialCodes.put("operator^=", "?_6");
		
		
	}
	
}
