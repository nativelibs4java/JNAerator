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

import org.rococoa.cocoa.CGFloat;

import com.ochafik.lang.jnaerator.runtime.CGFloatByReference;
import com.sun.jna.NativeLibrary;

public class GlobalCGFloat extends GlobalPrimitive<CGFloatByReference> {
	public GlobalCGFloat(NativeLibrary library, String... symbols) {
		super(library, CGFloatByReference.class, symbols);
	}
	public CGFloat get() {
		return getValue().get();
	}
	public void set(CGFloat v) {
		getValue().set(v);
	}

	public void set(float v) {
		getValue().set(v);
	}

	public void set(double v) {
		getValue().set(v);
	}
}
