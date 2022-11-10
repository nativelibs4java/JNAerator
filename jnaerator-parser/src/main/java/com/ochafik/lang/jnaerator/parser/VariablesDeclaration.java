/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (https://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator.parser;

import java.util.Arrays;
import java.util.List;

public class VariablesDeclaration extends StoredDeclarations {

	public VariablesDeclaration() {}
	public VariablesDeclaration(TypeRef valueType, Declarator...declarators) {
		this(valueType, Arrays.asList(declarators));
	}
	
	public VariablesDeclaration(TypeRef valueType, List<Declarator> declarators) {
		setValueType(valueType);
		setDeclarators(declarators);
	}
	@Override
	public VariablesDeclaration addModifiers(Modifier... mod) {
		return (VariablesDeclaration) super.addModifiers(mod);
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visitVariablesDeclaration(this);
	}
	
}