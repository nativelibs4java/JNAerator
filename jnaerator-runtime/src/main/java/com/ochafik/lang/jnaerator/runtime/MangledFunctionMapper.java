/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator.runtime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.NativeLibrary;
import com.sun.jna.win32.StdCallFunctionMapper;
import com.sun.jna.win32.W32APIFunctionMapper;

public class MangledFunctionMapper implements FunctionMapper {
	public static final Map<Object, Object> DEFAULT_OPTIONS;
	static {
		Map<Object, Object> m = new HashMap<Object, Object>();
		m.put(Library.OPTION_FUNCTION_MAPPER, new MangledFunctionMapper(
			new StdCallFunctionMapper()
		));
		
		DEFAULT_OPTIONS = Collections.unmodifiableMap(m);
	}
	public List<FunctionMapper> linked = new ArrayList<FunctionMapper>();
	public MangledFunctionMapper(FunctionMapper... linked ) {
		this.linked.addAll(Arrays.asList(linked));
	}
	public String getFunctionName(NativeLibrary library, Method method) {
		Mangling name = method.getAnnotation(Mangling.class);
		if (name != null) {
			for (String n : name.value()) {
				try {
					if (library.getGlobalVariableAddress(n) != null)
						return n;
					if (n.startsWith("_")) {
						n = n.substring(1);
						if (library.getGlobalVariableAddress(n) != null)
							return n;
					}
				} catch (Throwable ex) {
					ex = null;
				}
			}
		}
		if (linked != null)
			for (FunctionMapper fm : linked) {
				String n = fm.getFunctionName(library, method);
				if (n != null && library.getGlobalVariableAddress(n) != null)
					return n;
			}
		return method.getName();
	}
	
}
