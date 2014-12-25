/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator.runtime.globals;

import com.ochafik.lang.jnaerator.runtime.Union;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

/**
 *
 * @author ochafik
 */
public class GlobalUnion<U extends Union<?, ?, ?>> extends Global {
	U value;
	Class<U> valueClass;
	public GlobalUnion(NativeLibrary library, Class<U> valueClass, String... symbols) {
		super(library, symbols);
		this.valueClass = valueClass;
	}
	public U get() {
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
	protected U newInstance() {
		try {
			return valueClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void set(U value) {
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
		return com.sun.jna.Union.ByValue.class.isAssignableFrom(valueClass);
	}
    
}
