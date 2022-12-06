/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (https://github.com/nativelibs4java/JNAerator).
	
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

public abstract class StoredDeclarations extends Declaration {
	final List<Declarator> declarators = new ArrayList<Declarator>();

	public static class TypeDef extends StoredDeclarations {

		public TypeDef() {}
		public TypeDef(TypeRef valueType, Declarator... declarators) {
			this(valueType, Arrays.asList(declarators));
		}
		public TypeDef(TypeRef valueType, List<Declarator> declarators) {
			setValueType(valueType);
			setDeclarators(declarators);
		}
		@Override
		public void accept(Visitor visitor) {
			visitor.visitTypeDef(this);
		}
		
	}
    /*
	@Override
	public String toString(CharSequence indent) {
		return formatComments(indent, false, true, true) +
			getModifiersStringPrefix() + getValueTypeAndStorageSuffix(indent) + ";"
			+ (commentAfter == null ? "" : " " + commentAfter.trim())
			;
	}*/
	
	public List<Declarator> getDeclarators() {
		return unmodifiableList(declarators);
	}
	
	public void setDeclarators(List<Declarator> declarators) {
		changeValue(this, this.declarators, declarators);
	}
	public void addDeclarators(List<Declarator> declarators) {
		for (Declarator d : declarators)
			addDeclarator(d);
	}
		
	public void addDeclarator(Declarator declarator) {
		if (declarator == null)
			return;
		declarators.add(declarator);
		declarator.setParentElement(this);
	}
	@Override
	public Element getNextChild(Element child) {
		Element e = super.getNextChild(child);
		if (e != null)
			return e;
		return getNextSibling(declarators, child);
	}

	@Override
	public Element getPreviousChild(Element child) {
		Element e = super.getPreviousChild(child);
		if (e != null)
			return e;
		return getPreviousSibling(declarators, child);
	}
	
	
	@Override
	public boolean replaceChild(Element child, Element by) {
		if (super.replaceChild(child, by))
			return true;
		
		return replaceChild(declarators, Declarator.class, this, child, by);
	}
}
