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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ochafik.lang.jnaerator.parser.Function.Type;
import com.ochafik.util.string.StringUtils;

/**
 * @see http://msdn.microsoft.com/en-us/library/4x7sfztk(VS.80).aspx
 * @author ochafik
 *
 */
public abstract class Declarator extends ModifiableElement {
	protected Expression defaultValue;
	boolean parenthesized = false;
	int bits = -1;
	public int getBits() {
		return bits;
	}
	public void setBits(int bits) {
		this.bits = bits;
	}
	
	public static interface MutableByDeclarator {
		MutableByDeclarator clone();
	}
	
	public enum PointerStyle
	{
		Pointer { @Override public String toString() { return "*"; } }, 
		Reference { @Override public String toString() { return "&"; } }, 
		HatPointer { @Override public String toString() { return "^"; } };
		
		static Map<String, PointerStyle> styles = new TreeMap<String, PointerStyle>();
		static {
			for (PointerStyle style : values())
				styles.put(style.toString(), style);
		}
		public static PointerStyle parsePointerStyle(String s) {
			return styles.get(s);
		}
	}
	
	@Override
	public Declarator addModifiers(Modifier... mds) {
		return (Declarator)super.addModifiers(mds);
	}
	public abstract MutableByDeclarator mutateType(MutableByDeclarator t);
	
	public static class DirectDeclarator extends Declarator {
		public DirectDeclarator(String name, int bits) {
			setName(name);
			setBits(bits);
		}
		public DirectDeclarator(String name) {
			setName(name);
		}
		
		public DirectDeclarator(String name, Expression defaultValue) {
			setName(name);
			setDefaultValue(defaultValue);
		}

		public DirectDeclarator() {}

		String name;
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
		public String resolveName() {
			return getName();
		}
		public MutableByDeclarator mutateType(MutableByDeclarator type) {
			if (type == null)
				return null;
			type = type.clone();
			if (type instanceof Function) {
				((Function)type).setName(new Identifier.SimpleIdentifier(getName()));
			}
			((Element)type).importDetails(this, false);
			return type;
		}
		@Override
		public String toCoreString(CharSequence arg0) {
			return getName();
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitDirectDeclarator(this);
		}
		
		@Override
		public boolean replaceChild(Element child, Element by) {
			return super.replaceChild(child, by);
		}

		@Override
		public void propagateName(String name) {
			setName(name);
		}
	}
	
	public static abstract class TargettedDeclarator extends Declarator {
		public TargettedDeclarator() {}
		public TargettedDeclarator(Declarator target) {
			setTarget(target);
		}

		Declarator target;
		public Declarator getTarget() {
			return target;
		}
		public void setTarget(Declarator target) {
			this.target = changeValue(this, this.target, target);
		}
		@Override
		public String resolveName() {
			return getTarget() != null ? getTarget().resolveName() : null;
		}
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (child == getTarget()) {
				setTarget((Declarator)by);
				return true;
			}
			return super.replaceChild(child, by);
		}

		@Override
		public void propagateName(String name) {
			if (getTarget() != null)
				getTarget().propagateName(name);
			else
				setTarget(new DirectDeclarator(name));
		}
	}

	public static class PointerDeclarator extends TargettedDeclarator {
		Declarator.PointerStyle pointerStyle;
		public PointerDeclarator() {}
		public PointerDeclarator(Declarator target, Declarator.PointerStyle pointerStyle) {
			super(target);
			setPointerStyle(pointerStyle);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitPointerDeclarator(this);
		}
		@Override
		public MutableByDeclarator mutateType(MutableByDeclarator type) {
			if (type == null)
				return null;
			
			type = type.clone();
			if (type instanceof Function) {
				type = (MutableByDeclarator)new TypeRef.FunctionSignature((Function)type).importDetails((Element)type, true);
			} else if (type instanceof TypeRef) {
				type = (MutableByDeclarator) new TypeRef.Pointer((TypeRef)type, getPointerStyle()).importDetails((Element)type, true);
			} else
				throw new IllegalArgumentException(type.getClass().getName() + " cannot be mutated by pointer");
			((Element)type).importDetails(this, false);
			return getTarget() == null ? type : getTarget().mutateType(type);
		}
		public Declarator.PointerStyle getPointerStyle() {
			return pointerStyle;
		}
		public void setPointerStyle(Declarator.PointerStyle pointerStyle) {
			this.pointerStyle = pointerStyle;
		}
		
		@Override
		public String toCoreString(CharSequence indent) {
			StringBuilder b = new StringBuilder();
			b.append(getPointerStyle());
			if (getTarget() != null)
				b.append(getTarget().toString(indent));
//			if (getBits() >= 0)
//				b.append(":" + getBits());
			//if (getDefaultValue() != null)
			//	b.append(" = " + getDefaultValue());
			return b.toString();
		}
	}
	public static class FunctionDeclarator extends TargettedDeclarator {
		final List<Arg> args = new ArrayList<Arg>();

		public FunctionDeclarator() {}
		public FunctionDeclarator(Declarator target, List<Arg> args) {
			setTarget(target);
			setArgs(args);
		}
		
		@Override
		public MutableByDeclarator mutateType(MutableByDeclarator type) {
			/*if (type == null) {
				System.out.println("mutating null type : " + this);
				return null;
			}*/
			type = type == null ? null : type.clone();
			
			if (type == null || type instanceof TypeRef) {
				Function f = new Function();
				f.importDetails(this, false);
				f.setValueType((TypeRef)type);
				f.setType(Type.CFunction);
				f.setArgs(getArgs());
			
				return getTarget().mutateType(f);
			} else if (type instanceof Function) {
				Function ff = (Function)type;
				
				Function f = new Function();
				f.importDetails(this, false);
				f.setValueType(new TypeRef.FunctionSignature(ff));
				f.setType(Type.CFunction);
				f.setArgs(getArgs());
			
				return getTarget().mutateType(f);
			} else {
				throw new IllegalArgumentException("Function declarator can only mutate type references ! (mutating \"" + type + "\" by \"" + this + "\")");
				
			}
		}
		public List<Arg> getArgs() {
			return unmodifiableList(args);
		}
		public void setArgs(List<Arg> args) {
			changeValue(this, this.args, args);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitFunctionDeclarator(this);
		}
		
		@Override
		public boolean replaceChild(Element child, Element by) {
			if (replaceChild(args, Arg.class, this, child, by))
				return true;
			return super.replaceChild(child, by);
		}
		
		@Override
		public Element getNextChild(Element child) {
			Element e = getNextSibling(args, child);
			if (e != null)
				return e;
				
			return super.getNextChild(child);
		}
		
		@Override
		public Element getPreviousChild(Element child) {
			Element e = getPreviousSibling(args, child);
			if (e != null)
				return e;
				
			return super.getNextChild(child);
		}
		
		@Override
		public String toCoreString(CharSequence indent) {
			StringBuilder b = new StringBuilder();
			if (getTarget() != null)
				b.append(getTarget().toString(indent));
			b.append('(');
			b.append(implode(getArgs(), ", ", indent));
			b.append(')');
			return b.toString();
		}
	}
	public static class ArrayDeclarator extends TargettedDeclarator {
		protected final List<Expression> dimensions = new ArrayList<Expression>();
		public ArrayDeclarator() {}
		public ArrayDeclarator(Declarator target, List<Expression> dimensions) {
			super(target);
			setDimensions(dimensions);
		}
		public ArrayDeclarator(Declarator target, Expression... dimensions) {
			this(target, Arrays.asList(dimensions));
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitArrayDeclarator(this);
		}
		public void setDimensions(List<Expression> dimensions) {
			changeValue(this, this.dimensions, dimensions);
		}
		public List<Expression> getDimensions() {
			return unmodifiableList(dimensions);
		}
		public void addDimension(Expression ex) {
			if (ex == null)
				return;
			dimensions.add(ex);
			ex.setParentElement(this);
		}
		
		public MutableByDeclarator mutateType(MutableByDeclarator type) {
			if (type == null)
				return null;
			
			type = type.clone();
			if (type instanceof TypeRef)
				type = new TypeRef.ArrayRef((TypeRef)type, deepClone(getDimensions()));
			else if (type instanceof Function)
			{
				Function f = (Function)type;
				f.setValueType(new TypeRef.ArrayRef(f.getValueType(), deepClone(getDimensions())));
				type = f;
			}
			((Element)type).importDetails(this, false);
			return target.mutateType(type);
		}
		
		@Override
		public Element getNextChild(Element child) {
			Element e = getNextSibling(dimensions, child);
			if (e != null)
				return null;
			return super.getNextChild(child);
		}
		
		@Override
		public Element getPreviousChild(Element child) {
			Element e = getPreviousSibling(dimensions, child);
			if (e != null)
				return null;
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
		@Override
		public String toCoreString(CharSequence indent) {
			StringBuilder b = new StringBuilder();
			if (getTarget() != null)
				b.append(getTarget().toString(indent));
			//if (!getDimensions().isEmpty())
				b.append("[" + StringUtils.implode(getDimensions(), "][") + "]");
//				if (getBits() >= 0)
//					b.append(":" + getBits());
//				if (getDefaultValue() != null)
//				b.append(" = " + getDefaultValue());
			return b.toString();
		}
	}
	
	public void setDefaultValue(Expression defaultValue) {
		this.defaultValue = changeValue(this, this.defaultValue, defaultValue);
	}
	public Expression getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public boolean replaceChild(Element child, Element by) {
		if (child == getDefaultValue()) {
			setDefaultValue((Expression) by);
			return true;
		}
			
		return super.replaceChild(child, by);
	}
	
	public boolean isParenthesized() {
		return parenthesized;
	}
	public void setParenthesized(boolean parenthesized) {
		this.parenthesized = parenthesized;
	}

	public abstract String resolveName();
	public abstract void propagateName(String name);
	
	public String toString(CharSequence indent) {
		StringBuilder b = new StringBuilder();
		if (isParenthesized())
			b.append('(');
		
		if (!getModifiers().isEmpty()) {
			b.append(StringUtils.implode(getModifiers(), " "));
			b.append(" ");
		}
		b.append(toCoreString(indent));
		if (isParenthesized())
			b.append(')');
		if (getBits() >= 0)
			b.append(":" + getBits());
		if (getDefaultValue() != null) {
			b.append(" = ");
			b.append(getDefaultValue().toString(indent));
		}
		return b.toString();
	}

	protected abstract String toCoreString(CharSequence indent);
}
