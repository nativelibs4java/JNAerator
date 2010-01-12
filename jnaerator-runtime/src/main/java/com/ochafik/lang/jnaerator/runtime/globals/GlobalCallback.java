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

import com.sun.jna.Callback;
import com.sun.jna.CallbackReferenceHack;
import com.sun.jna.NativeLibrary;

public class GlobalCallback<T extends Callback> extends Global {
	protected final Class<T> type;
	public GlobalCallback(NativeLibrary library, Class<T> type, String... symbols) {
		super(library, symbols);
		this.type = type;
	}
	protected T value;
	@SuppressWarnings("unchecked")
	public T get() {
		if (value == null) {
			value = (T)CallbackReferenceHack.getCallback(type, getPointer());
		}
		return value;
	}
	public void set(T value) {
		getPointer().setPointer(0, CallbackReferenceHack.getFunctionPointer(this.value = value));
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
