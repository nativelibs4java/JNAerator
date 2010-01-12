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
package com.ochafik.lang.reflect;

//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.ochafik.util.string.RegexUtils;
import com.ochafik.util.string.StringUtils;

public class GettersAndSettersHelper {
	public static class GetterAndSetterInfo {
		public Method getter;
		public Method setter;
		//public Field field;
		public String fieldName;
		public boolean isFull() {
			return getter != null && setter != null;
		}
		public Class<?> elementType;
		public GetterAndSetterInfo(String fieldName, Class<?> elementType, Method getter, Method setter, Field field) {
			this.elementType = elementType;
			this.fieldName = fieldName;
			this.getter = getter;
			this.setter = setter;
			//this.field = field;
		}
		public GetterAndSetterInfo() {}
		public boolean isConsistent() {
			if (getter == null || setter == null)
				return true;
			
			Class<?>[] pts = setter.getParameterTypes();
			return pts.length == 1 && pts[0].isAssignableFrom(getter.getReturnType());
		}
	}
	
	public final Map<String, GetterAndSetterInfo> gettersAndSetters = new HashMap<String, GetterAndSetterInfo>();
	
	static Pattern getterSetterPattern = Pattern.compile("(is|get|set)([A-Z]\\w+)");
	
	public GettersAndSettersHelper(Class<?> type, FieldGetter fieldGetter) {
		this.fieldGetter = fieldGetter;
		for (Method method : type.getMethods()) {
			int nParams = method.getParameterTypes().length;
			if (nParams > 1)
				continue;
			
			String name = method.getName();
			String match[] = RegexUtils.match(name, getterSetterPattern);
			if (match != null) {
				Class<?> returnType = method.getReturnType();
				String fieldName = StringUtils.uncapitalize(match[2]);
				boolean isGetter = !match[1].equals("set");
				if (isGetter) {
					if (nParams == 0) {
						GetterAndSetterInfo getterAndSetter = getOrCreatePair(fieldName);
						if (getterAndSetter.getter != null) {
							if (getterAndSetter.getter.getReturnType().isAssignableFrom(returnType)) {
								// refinement
								getterAndSetter.getter = method;
							}
						} else {
							getterAndSetter.getter = method;
						}

						if (!getterAndSetter.isConsistent())
							getterAndSetter.setter = null;
						
					}
					//assertNull("Already found getter " + getterAndSetter.getter, getterAndSetter.getter);
					//getterAndSetter.setFirst(method);
				} else if (nParams == 1) {
					GetterAndSetterInfo getterAndSetter = getOrCreatePair(fieldName);
					//assertNull("Already found setter " + getterAndSetter.setter, getterAndSetter.setter);
					//assert getterAndSetter.setter == null;
					getterAndSetter.setter = method;
					if (!getterAndSetter.isConsistent())
						getterAndSetter.setter = null;
				}
			}
		}
		/*
		for (Map.Entry<String, GetterAndSetterInfo> e : gettersAndSetters.entrySet()) {
			if (e.getValue().setter == null)
				continue;
			try {
				e.getValue().field = fieldGetter == null ? type.getField(e.getKey()) : fieldGetter.getField(type, e.getKey());
			} catch (Exception ex) {
				assertTrue("Failed to find field '" + e.getKey() + "' in " + type.getName(), false);
			}
		}*/
		/*for (Field field : type.getFields()) {
			GetterAndSetterInfo info = gettersAndSetters.get(field.getName());
			if (info == null)
				continue;
			info.field = field;
		}*/
	}
	final FieldGetter fieldGetter;
	public interface FieldGetter {
		public Field getField(Class<?> c, String name) throws SecurityException, NoSuchFieldException;
	}
	public Set<String> getFieldNames() {
		return gettersAndSetters.keySet();
	}
	public Method getGetter(String fieldName) {
		GetterAndSetterInfo pair = gettersAndSetters.get(fieldName);
		return pair == null ? null : pair.getter;
	}
	public Method getSetter(String fieldName) {
		GetterAndSetterInfo pair = gettersAndSetters.get(fieldName);
		return pair == null ? null : pair.setter;
	}
	public Type getFieldType(String fieldName) {
		GetterAndSetterInfo pair = gettersAndSetters.get(fieldName);
		if (pair == null)
			return null;
		if (pair.getter != null)
			return pair.getter.getGenericReturnType();
		if (pair.setter != null)
			return pair.setter.getGenericParameterTypes()[0];
		return null;
	}
	public void assertConsistentPair(GetterAndSetterInfo p) {
		if (p.getter != null && p.setter != null) {
			Class<?> getType = p.getter.getReturnType(),
				setType = p.setter.getParameterTypes()[0];
			
			//assertTrue("Setter argument cannot be given getter result", setType.isAssignableFrom(getType));
			assert setType.isAssignableFrom(getType);
		}
	}
	protected GetterAndSetterInfo getOrCreatePair(String fieldName) { 
		GetterAndSetterInfo getterAndSetter = gettersAndSetters.get(fieldName);
		if (getterAndSetter == null)
			gettersAndSetters.put(fieldName, getterAndSetter = new GetterAndSetterInfo());
		
		return getterAndSetter;
	}
}