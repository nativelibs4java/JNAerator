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

		@Override
		public void accept(Visitor visitor) {
			visitor.visitPrimitive(this);
		}
	}
	
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
