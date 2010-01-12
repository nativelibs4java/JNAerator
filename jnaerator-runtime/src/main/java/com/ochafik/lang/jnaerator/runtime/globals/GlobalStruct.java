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

import com.ochafik.lang.jnaerator.runtime.Structure;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public class GlobalStruct<S extends Structure<?, ?, ?>> extends Global {
	S value;
	Class<S> valueClass;
	public GlobalStruct(NativeLibrary library, Class<S> valueClass, String... symbols) {
		super(library, symbols);
		this.valueClass = valueClass;
	}
	public S get() {
		if (value == null) {
			value = newInstance();
			Pointer p = getPointer();
			if (!isByValue())
				p = p.getPointer(0);
			value.use(p);
		}
		value.read();
		return value;
	}
	protected S newInstance() {
		try {
			return valueClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void set(S value) {
		if (isByValue()) {
			Pointer p = getPointer();
			int s = value.size();
			p.write(0, value.getPointer().getByteArray(0, s), 0, s);
			get();
		} else {
			this.value = value;
			getPointer().setPointer(0, value.getPointer());
		}
	}
	protected boolean isByValue() {
		return com.sun.jna.Structure.ByValue.class.isAssignableFrom(valueClass);
	}
}
