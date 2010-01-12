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
package com.ochafik.lang.jnaerator.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ochafik.lang.jnaerator.parser.Expression.EmptyArraySize;
import com.ochafik.lang.jnaerator.parser.ModifierKind;
import com.ochafik.util.string.StringUtils;

public abstract class TypeRef extends ModifiableElement implements Declarator.MutableByDeclarator {
	
	protected boolean markedAsResolved;
	public TypeRef setMarkedAsResolved(boolean markedAsResolved) {
		this.markedAsResolved = markedAsResolved;
		return this;
	}
	public boolean isMarkedAsResolved() {
		return markedAsResolved;
	}
	@Override
	public TypeRef importDetails(Element from, boolean move) {
		return (TypeRef) super.importDetails(from, move);
	}
	@Override
	public TypeRef addModifiers(Modifier... mds) {
		return (TypeRef) super.addModifiers(mds);
	}
	public static abstract class TaggedTypeRef extends TypeRef {
		Identifier tag, originalTag;
		public Identifier getTag() {
			return tag;
		}
		public void setTag(Identifier tag) {
//			if (tag != null && tag.equals("lc_category_struct"))
//				tag = tag;
//				new Exception("Recursion too deep " + tag).printStackTrace();
			
			
			this.tag = changeValue(this, this.tag, tag);
			if (originalTag == null && tag != null)
				setOriginalTag(tag.clone());
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getTag()) {
				setTag((Identifier)by);
				return true;
			}
			if (child == getOriginalTag()) {
				setOriginalTag((Identifier)by);
				return true;
			}
			return super.replaceChild(child, by);
		}
		public void setOriginalTag(Identifier originalTag) {
			this.originalTag = changeValue(this, this.originalTag, originalTag);
		}
		public Identifier getOriginalTag() {
			return originalTag;
		}
		public boolean isForwardDeclaration() {
			return forwardDeclaration;
		}
		public void setForwardDeclaration(boolean forwardDeclaration) {
			this.forwardDeclaration = forwardDeclaration;
		}
		
		boolean forwardDeclaration = false;
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitTaggedTypeRef(this);
		}
	}
	//protected final List<String> modifiers = new ArrayList<String>();
	
	@Override
	public TypeRef clone() {
		return (TypeRef) super.clone();
	}
	
	public static class SimpleTypeRef extends TypeRef {
		protected Identifier name;

		public SimpleTypeRef(String name) {
			this();
			setName(new Identifier.SimpleIdentifier(name));
		}
		public SimpleTypeRef(Identifier name) {
			this();
			setName(name);
		}
		public SimpleTypeRef() {
		}
		
		public Identifier getName() {
			return name;
		}
		public void setName(Identifier name) {
			this.name = changeValue(this, this.name, name);
		}
		
		@Override
		public String toString(CharSequence indent) {
			return formatComments(indent, true, false, false) + getModifiersStringPrefix() + name;
		}
		
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getName())
				setName((Identifier)by);
			return super.replaceChild(child, by);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitSimpleTypeRef(this);
		}
		
		
	}

	public String variableDeclarationToString(String varName, boolean isVarArg, CharSequence indent) {
		return toString(indent) + (isVarArg ? "... " : " ") + varName;
	}
	public boolean acceptsModifier(Modifier modifier) {
		return modifier.isAnyOf(ModifierKind.NumericTypeQualifier, ModifierKind.TypeQualifier);
	}
	/*
	public TypeRef addModifier(String modifier) {
		if (modifier != null)
			modifiers.add(modifier);
		return this;
	}
	public void addModifier(String modifier, int i) {
		if (modifier != null)
			modifiers.add(i, modifier);
	}*/
	
	public String getModifiersStringPrefix() {
		return StringUtils.implode(modifiers, " ") + (modifiers.isEmpty() ? "" : " ");
	}

	public static class FunctionSignature extends TypeRef {
		public enum Type {
			CFunction, ObjCBlock
		}
		protected Type type = Type.CFunction;
		protected Function function;

		public void setType(Type type) {
			this.type = type;
		}

		public Type getType() {
			return type;
		}


		public FunctionSignature(Function function) {
			this();
			setFunction(function);
		}

		@Override
		public FunctionSignature clone() {
			return (FunctionSignature) super.clone();
		}
		
		public FunctionSignature() {
		}

		public Function getFunction() {
			return function;
		}
		public void setFunction(Function function) {
			this.function = changeValue(this, this.function, function);
		}
		
		@Override
		public String toString(CharSequence indent) {
			if (function == null)
				return null;
			
			assert function.getBody() == null;
			String s = 
				(function.getValueType() == null ? "" : function.getValueType() + " ") +
				"(" +
					function.getModifiersStringPrefix() +
					(Type.ObjCBlock.equals(getType()) ? "^" : "*") +
					(function.getName() == null ? "" : function.getName()) +
				")(" +
				StringUtils.implode(function.getArgs(), ", ") +
				")" + (getModifiers().isEmpty() ? "" : " ") + StringUtils.implode(getModifiers(), " ");
			
			return getModifiersStringPrefix() + s;
		}

		@Override
		public String variableDeclarationToString(String varName, boolean isVarArg, CharSequence indent) {
			if (isVarArg || getFunction() == null | getFunction().getName() == null)
				return super.variableDeclarationToString(varName, isVarArg, indent);
			else
				return toString(indent);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitFunctionSignature(this);
		}

		@Override
		public boolean replaceChild(Element child, Element by) {
			if (getFunction() == child) {
				setFunction((Function) by);
				return true;
			}
			return super.replaceChild(child, by);
		}
	}
	
	public static class Primitive extends SimpleTypeRef {//TypeRef {
		//protected String name;
		
		static Set<String> cPrimitiveTypes = new HashSet<String>();
		static {
			cPrimitiveTypes.add("long");
			cPrimitiveTypes.add("int");
			cPrimitiveTypes.add("short");
			cPrimitiveTypes.add("bool");
			cPrimitiveTypes.add("char");
			cPrimitiveTypes.add("void");
			cPrimitiveTypes.add("double");
			cPrimitiveTypes.add("float");
			cPrimitiveTypes.add("size_t");
			cPrimitiveTypes.add("__int8");
			cPrimitiveTypes.add("__int16");
			cPrimitiveTypes.add("__int32");
			cPrimitiveTypes.add("__int64");
			cPrimitiveTypes.add("__uint8");
			cPrimitiveTypes.add("__uint16");
			cPrimitiveTypes.add("__uint32");
			cPrimitiveTypes.add("__uint64");
			
		}
		
		public static boolean isACPrimitive(String s) {
			return cPrimitiveTypes.contains(s);
		}
		public Primitive(String name) {
			setName(name == null ? null : new Identifier.SimpleIdentifier(name));
		}

		public Primitive() {}
		
		@Override
		public Primitive addModifiers(Modifier... mds) {
			return (Primitive)super.addModifiers(mds);
		}

		/*public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}*/
		@Override
		public String toString(CharSequence indent) {
			if (name != null)
				return getModifiersStringPrefix() + name;
			else
				return getModifiersStringPrefix().trim();
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitPrimitive(this);
		}
	}
	
	/*public static class StructTypeRef extends TypeRef {
		protected Struct struct;
		
		public StructTypeRef(Struct struct) {
			this();
			setStruct(struct);
		}
		public StructTypeRef() {
		}
		public Struct getStruct() {
			return struct;
		}
		public void setStruct(Struct struct) {
			this.struct = changeValue(this, this.struct, struct);
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getStruct()) {
				setStruct((Struct) by);
				return true;
			}
			return super.replaceChild(child, by);
		}
		@Override
		public String toString(CharSequence indent) {
			return getStruct() == null ? null : getStruct().toCoreString("");
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitStructTypeRef(this);
		}
	}*/
	/*public static class EnumTypeRef extends TypeRef {
		protected Enum enumeration;
		
		
		public EnumTypeRef(Enum enumeration) {
			this();
			setEnumeration(enumeration);
		}
		public EnumTypeRef() {}
		
		public void setEnumeration(Enum enumeration) {
			this.enumeration = changeValue(this, this.enumeration, enumeration);
		}
		public Enum getEnumeration() {
			return enumeration;
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getEnumeration()) {
				setEnumeration((Enum) by);
				return true;
			}
			return super.replaceChild(child, by);
		}
		@Override
		public String toString(CharSequence indent) {
			return getEnumeration() == null ? null : getEnumeration().toCoreString("");
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitEnumTypeRef(this);
		}
	}*/
	/*
	public static class SubTypeRef extends TargettedTypeRef {
		public enum Style { 
			Dot { public String toString() { return "."; } }, 
			Colons { public String toString() { return "::"; } } 
		}
		public SubTypeRef() {}
		public SubTypeRef(TypeRef target, Style style, String subName) {
			setTarget(target);
			setStyle(style);
			setSubName(subName);
		}

		Style style;
		String subName;
		
		public void setStyle(Style style) {
			this.style = style;
		}
		public Style getStyle() {
			return style;
		}
		public String getSubName() {
			return subName;
		}
		public void setSubName(String subName) {
			this.subName = subName;
		}
		
		@Override
		public String toString(CharSequence indent) {
			return getTarget().toString(indent) + getStyle() + getSubName();
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitSubTypeRef(this);
		}
	}*/
	public static class Pointer extends TargettedTypeRef {
		Declarator.PointerStyle pointerStyle;
		
		public Pointer(TypeRef target, Declarator.PointerStyle pointerStyle) {
			this();
			setTarget(target);
			setPointerStyle(pointerStyle);
		}
		public Pointer() {
		}
		public void setPointerStyle(Declarator.PointerStyle pointerStyle) {
			this.pointerStyle = pointerStyle;
		}
		public Declarator.PointerStyle getPointerStyle() {
			return pointerStyle;
		}
		@Override
		public String toString(CharSequence indent) {
			String s = getModifiersStringPrefix();
			//return getTarget() + (s.length() == 0 ? "" : " " + s.trim()) + VariableStorage.toString(getPointerStyle());
			return s + getTarget() + getPointerStyle();
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitPointer(this);
		}
	}
	
	public static abstract class TargettedTypeRef extends TypeRef {
		protected TypeRef target;
		public TypeRef getTarget() {
			return target;
		}
		public void setTarget(TypeRef target) {
			this.target = changeValue(this, this.target, target);
		}
		
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (getTarget() == child) {
				setTarget((TypeRef) by);
				return true;
			}
			return super.replaceChild(child, by);
		}

			
	}
	public static class ArrayRef extends TargettedTypeRef {
		final List<Expression> dimensions = new ArrayList<Expression>();
		
		public ArrayRef(TypeRef target, Expression... dimensions) {
			this(target, Arrays.asList(dimensions));
		}
		public ArrayRef(TypeRef target, List<Expression> dimensions) {
			this();
			setDimensions(dimensions);
			setTarget(target);
		}
		
		public void flattenDimensions(List<Expression> out) {
			out.addAll(dimensions);
		}
			
		public List<Expression> flattenDimensions() {
			List<Expression> dims = new ArrayList<Expression>();
			if (getTarget() instanceof ArrayRef) {
				((ArrayRef)getTarget()).flattenDimensions(dims);
			}
			flattenDimensions(dims);
			return dims;
		}
		
		public boolean hasStaticStorageSize() {
			if (dimensions.isEmpty())
				return false;
			
			Expression x = dimensions.get(dimensions.size() - 1);
			return x != null && !(x instanceof EmptyArraySize);
		}
		
		public ArrayRef() {
		}

		@Override
		public Element getNextChild(Element child) {
			Element e = getNextSibling(dimensions, child);
			if (e != null)
				return e;
			return super.getNextChild(child);
		}
		
		@Override
		public Element getPreviousChild(Element child) {
			Element e = getPreviousSibling(dimensions, child);
			if (e != null)
				return e;
			return super.getPreviousChild(child);
		}
		
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (super.replaceChild(child, by))
				return true;
			
			if (replaceChild(dimensions, Expression.class, this, child, by))
				return true;
			
			return super.replaceChild(child, by);
		}
		
		public List<Expression> getDimensions() {
			return unmodifiableList(dimensions);
		}
		public void setDimensions(List<Expression> dimensions) {
			changeValue(this, this.dimensions, dimensions);
		}
		public String bracketsToString() {
			return "[" + StringUtils.implode(dimensions, "][") + "]";
		}
		@Override
		public String toString(CharSequence indent) {
			return getModifiersStringPrefix() + getTarget() + bracketsToString();
		}
	
		@Override
		public String variableDeclarationToString(String varName, boolean isVarArg, CharSequence indent) {
			return (getTarget() == null ? "" : getTarget().toString(indent)) + (isVarArg ? "... " : " ") + varName + bracketsToString();
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitArray(this);
		}
		public void addDimension(Expression x) {
			if (x == null)
				return;
			
			x.setParentElement(this);
			dimensions.add(x);
		}
	}

	

}
