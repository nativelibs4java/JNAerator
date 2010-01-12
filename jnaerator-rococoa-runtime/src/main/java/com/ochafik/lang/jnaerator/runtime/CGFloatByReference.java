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

import org.rococoa.cocoa.CGFloat;

import com.sun.jna.ptr.ByReference;

public class CGFloatByReference extends ByReference {
	public CGFloatByReference() {
		this(0d);
	}

	public CGFloatByReference(float value) {
		super(CGFloat.SIZE);
		set(value);
	}
	public void set(float value) {
		set(new CGFloat(value));
	}

	public CGFloatByReference(double value) {
		super(CGFloat.SIZE);
		set(value);
	}
	public void set(double value) {

		set(new CGFloat(value));
	}

	public void set(CGFloat value) {
		switch (CGFloat.SIZE) {
		case 4:
			getPointer().setFloat(0, value.floatValue());
			break;
		case 8:
			getPointer().setDouble(0, value.doubleValue());
			break;
		default:
			throw new UnsupportedOperationException("Unhandled CGFloat size : " + CGFloat.SIZE);
		}
	}
	public CGFloat get() {
		switch (CGFloat.SIZE) {
		case 4:
			return new CGFloat(getPointer().getFloat(0));
		case 8:
			return new CGFloat(getPointer().getDouble(0));
		default:
			throw new UnsupportedOperationException("Unhandled CGFloat size : " + CGFloat.SIZE);
		}
	}

	public CGFloatByReference(CGFloat value) {
		super(CGFloat.SIZE);
		set(value);
	}
}
