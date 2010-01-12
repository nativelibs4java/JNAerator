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
package com.ochafik.lang.jnaerator.runtime;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

@Deprecated
public class StringPointer extends Structure<StringPointer, StringPointer.ByValue, StringPointer.ByReference> {
	public String value;
	public String toString() {
		return value;
	}
	public StringPointer() {}
	public StringPointer(PointerType p) {
		this(p.getPointer(), 0);
	}
	public StringPointer(Pointer p) {
		this(p, 0);
	}
	public StringPointer(Pointer p, int offset) {
		useMemory(p, offset);
		read();
	}
	public StringPointer(String value) {
		super();
		this.value = value;
	}
	public static class ByValue extends StringPointer implements com.sun.jna.Structure.ByValue {}
	public static class ByReference extends StringPointer implements com.sun.jna.Structure.ByReference {}
	@Override
	protected ByReference newByReference() {
		return new ByReference();
	}
	@Override
	protected ByValue newByValue() {
		return new ByValue();
	}
	@Override
	protected StringPointer newInstance() {
		return new StringPointer();
	} 
}
