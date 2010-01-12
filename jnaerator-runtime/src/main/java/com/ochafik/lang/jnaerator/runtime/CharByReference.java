/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
* <p/>
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.  
*/
package com.ochafik.lang.jnaerator.runtime;

import com.sun.jna.Native;
import com.sun.jna.ptr.ByReference;

public class CharByReference extends ByReference {
	public CharByReference() {
		this((char)0);
	}

	public CharByReference(char value) {
		super(Native.WCHAR_SIZE);
		setValue(value);
	}
	public void setValue(char value) {
		getPointer().setChar(0, value);
//		switch (Native.WCHAR_SIZE) {
//		case 1:
//		case 2:
//		case 4:
//			getPointer().setInt(0, value);
//			break;
//		default:
//			throw new UnsupportedOperationException("Unhandled CGFloat size : " + CGFloat.SIZE);
//		}
	}
	public char getValue() {
		return getPointer().getChar(0);
	}

}
