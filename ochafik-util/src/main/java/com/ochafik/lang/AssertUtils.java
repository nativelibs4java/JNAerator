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
package com.ochafik.lang;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;

import com.ochafik.lang.reflect.DebugUtils;

public class AssertUtils {
	public static final <T> boolean unorderedEqual(Collection<T> c1, Collection<T> c2) {
		if (c1.size() != c2.size()) return false;
		
		return c1.containsAll(c2) && c2.containsAll(c1);
	}
	
	public static final <T> boolean orderedEqual(Collection<T> c1, Collection<T> c2) {
		if (c1.size() != c2.size()) return false;
		Iterator<T> i1 = c1.iterator(), i2 = c2.iterator();
		while (i1.hasNext() && i2.hasNext()) {
			T t1 = i1.next(), t2 = i2.next();
			if ((t1 == null) != (t2 == null)) return false;
			if (t1 != null) {
				if (!(t1.equals(t2) && t2.equals(t1))) return false;
			}
		}
		return (i1.hasNext() == i2.hasNext());
	}
	
	public static class Test {
		int value;
		String strr = "This is\nA test...";
		
		public String getStrr() {
			return strr;
		}
	}
	

	public static void main(String[] args) {
		//print(new int[] { 1, 2, 3, 4});
		DebugUtils.println(new Object[] { new Test(), }, new DebugUtils.FieldAccessor() {
			public Object access(Field f, Object target) throws IllegalArgumentException, IllegalAccessException {
				return f.get(target);
			}
		});
	}
}
