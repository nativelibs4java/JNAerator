/*
	Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (https://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator.runtime;

import java.util.Arrays;
import java.util.List;

import org.junit.*;
//import com.sun.jna.*;

import static org.junit.Assert.*;

public class StructureTest {
	public static class TestStruct extends Structure<TestStruct, TestStruct.ByValue, TestStruct.ByReference > {
		public int value;
		public TestStruct() {
			super();
		}
		public TestStruct(int value) {
			super();
			this.value = value;
		}
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("value");
		}
		protected ByReference newByReference() { return new ByReference(); }
		protected ByValue newByValue() { return new ByValue(); }
		protected TestStruct newInstance() { return new TestStruct(); }
		public static TestStruct[] newArray(int arrayLength) {
			return Structure.newArray(TestStruct.class, arrayLength);
		}
		public static class ByReference extends TestStruct implements Structure.ByReference {
			
		};
		public static class ByValue extends TestStruct implements Structure.ByValue {
			
		};
	};
	
	
	/// See https://code.google.com/p/jnaerator/issues/detail?id=56
	@Test
	public void testByRefClone() {
		TestStruct s = new TestStruct();
		s.value = 10;
		
		TestStruct.ByReference r = s.byReference();
		r.autoWrite();
		
		assertEquals(s.value, r.value);
	}
}