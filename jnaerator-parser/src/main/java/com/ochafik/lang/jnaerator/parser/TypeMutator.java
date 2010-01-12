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

import com.ochafik.lang.jnaerator.parser.Declarator.PointerStyle;

public abstract class TypeMutator {
	public static TypeMutator 
		CONST_STAR = new TypeMutator() { @Override public TypeRef mutateType(TypeRef type) {
			type = new TypeRef.Pointer(type, PointerStyle.Pointer);
			type.addModifiers(Modifier.Const);
			return type;
		}},
		STAR = new TypeMutator() { @Override public TypeRef mutateType(TypeRef type) {
			return new TypeRef.Pointer(type, PointerStyle.Pointer);
		}},
		HAT = new TypeMutator() { @Override public TypeRef mutateType(TypeRef type) {
			return new TypeRef.Pointer(type, PointerStyle.HatPointer);
		}},
		AMPERSTAND = new TypeMutator() { @Override public TypeRef mutateType(TypeRef type) {
			return new TypeRef.Pointer(type, PointerStyle.Reference);
		}},
		CONST = new TypeMutator() { @Override public TypeRef mutateType(TypeRef type) {
			type.addModifiers(Modifier.Const);
			return type;
		}},
		BRACKETS = new TypeMutator() { @Override public TypeRef mutateType(TypeRef type) {
			return new TypeRef.ArrayRef(type, new Expression.EmptyArraySize());
		}}
	;
	public abstract TypeRef mutateType(TypeRef type);
	public static TypeMutator array(final Expression expression) {
		return new TypeMutator() { @Override public TypeRef mutateType(TypeRef type) {
			return new TypeRef.ArrayRef(type, expression);
		}};
	}
}
