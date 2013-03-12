package com.ochafik.lang.jnaerator.runtime;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.WString;

public class RuntimeTest {
	public static class DummyStruct extends com.sun.jna.Structure {
		public int i;
		public long l;
		public short s;
		public byte b;
		public char c;
		public boolean z;
		public Pointer p;
		public String str;
		public WString wstr;
		public int[] ii = new int[10];
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("i", "l", "s", "b", "c", "z", "p", "str", "wstr", "ii");
		}
	}
	@Test
	public void dummyStruct() {
		DummyStruct ds = new DummyStruct();
		ds.write();
		ds.read();
	}
}
