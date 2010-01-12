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

import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;

public class FunctionPointerDeclaration extends Declaration {
	Expression defaultValue;
	
	public FunctionPointerDeclaration(FunctionSignature functionSignature) {
		this();
		setValueType(functionSignature);
		//setName(functionSignature.getFunction().getName());
	}

	public FunctionPointerDeclaration() {
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
	
	public void setDefaultValue(Expression defaultValue) {
		this.defaultValue = changeValue(this, this.defaultValue, defaultValue);
	}
	@Override
	public FunctionSignature getValueType() {
		return (FunctionSignature)super.getValueType();
	}
	@Override
	public void setValueType(TypeRef valueType) {
		super.setValueType((FunctionSignature)valueType);
	}
	@Override
	public String toString(CharSequence indent) {
		String asst = getDefaultValue() != null ? " = " + getDefaultValue() : "";
		return getModifiersStringPrefix() + getValueType().toString() + asst + ";";
	}
	public void accept(Visitor visitor) {
		visitor.visitFunctionPointerDeclaration(this);
	}

}
