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
import com.sun.jna.WString;

@Deprecated
public class WStringPointer extends Structure<WStringPointer, WStringPointer.ByValue, WStringPointer.ByReference> {
	public WString value;
	public String toString() {
		return value.toString();
	}

	public WStringPointer() {}
	public WStringPointer(PointerType p) {
		this(p.getPointer(), 0);
	}
	public WStringPointer(Pointer p) {
		this(p, 0);
	}
	public WStringPointer(Pointer p, int offset) {
		useMemory(p, offset);
		read();
	}
	public WStringPointer(WString value) {
		super();
		this.value = value;
	}
	public WStringPointer(String value) {
		super();
		this.value = new WString(value);
	}
	public static class ByValue extends WStringPointer implements com.sun.jna.Structure.ByValue {}
	public static class ByReference extends WStringPointer implements com.sun.jna.Structure.ByReference {}
	@Override
	protected ByReference newByReference() {
		return new ByReference();
	}
	@Override
	protected ByValue newByValue() {
		return new ByValue();
	}
	@Override
	protected WStringPointer newInstance() {
		return new WStringPointer();
	}
}