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
package com.ochafik.lang.jnaerator.runtime.globals;

import com.ochafik.util.string.StringUtils;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public abstract class GlobalPrimitive<T extends PointerType> extends Global {
	protected final Class<T> type;
	protected boolean indirected = false;
	public GlobalPrimitive(NativeLibrary library, Class<T> type, String... symbols) {
		super(library, symbols);
		this.type = type;
	}
	protected T value;
	protected T getValue() {
		if (value == null) {
			try {
				value = type.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to instantiate pointer to " + StringUtils.implode(symbols, "/"), e);
			}
			Pointer pointer = getPointer();
			if (indirected) {
				pointer = pointer.getPointer(0);
			}
			value.setPointer(pointer);
		}
		return value;
	}
	@Override
	public String toString() {
		try {
			return String.valueOf(getClass().getMethod("get").invoke(this));
		} catch (Throwable t) {
			return super.toString();
		}
	}
}
