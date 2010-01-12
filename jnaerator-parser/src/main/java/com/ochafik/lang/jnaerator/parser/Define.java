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


public class Define extends Declaration {

	Expression value;
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Define(String name, Expression value) {
		this();
		setName(name);
		setValue(value);
	}

	public Define() {
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitDefine(this);
	}

	@Override
	public boolean replaceChild(Element child, Element by) {
		if (child == getValue()) {
			setValue((Expression) by);
			return true;
		}
		return super.replaceChild(child, by);
	}
	
	public Expression getValue() {
		return value;
	}
	public void setValue(Expression value) {
		this.value = changeValue(this, this.value, value);
	}
	@Override
	public Element getNextSibling() {
		return super.getNextSibling();
	}
	
	@Override
	public String toString(CharSequence indent) {
		return indent + "#define " + getName() + (getValue() == null ? "" : " " + getValue());
	}
}
