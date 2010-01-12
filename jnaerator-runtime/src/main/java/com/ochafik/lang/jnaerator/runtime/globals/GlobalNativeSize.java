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

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

public class GlobalNativeSize extends GlobalPrimitive<NativeSizeByReference> {
	public GlobalNativeSize(NativeLibrary library, String... symbols) {
		super(library, NativeSizeByReference.class, symbols);
	}
	public NativeSize get() {
		return getValue().getValue();
	}
	public void set(int v) {
		getValue().setValue(new NativeSize(v));
	}
	public void set(long v) {
		getValue().setValue(new NativeSize(v));
	}
	public void set(NativeSize v) {
		getValue().setValue(v);
	}
}
