package com.ochafik.lang.jnaerator.runtime;

import java.lang.reflect.Field;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.WString;

public class RuntimeTest {
	public static class BitFieldStruct extends com.sun.jna.Structure {
		//@Override
		protected Integer getBitsAnnotation(Field field) {
			Bits bits = field.getAnnotation(Bits.class);
			return bits == null ? null : bits.value();
		}
	}
	public static class TestStruct extends BitFieldStruct {
		@Bits(1) public int i0;
		@Bits(1) public int i1;
		public short s;
		@Bits(3) public int i2;
		@Bits(1) public int i3;
		

		public TestStruct() { super(); }
		public TestStruct(Pointer p) {
			super();
			useMemory(p);
		}
	}
	public static class DummyStruct extends BitFieldStruct {
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

	}
	@Test
	public void dummyStruct() {
		DummyStruct ds = new DummyStruct();
		ds.write();
		ds.read();
		
	}
	@Test
	public void testSimpleBitField() {
		TestStruct s = new TestStruct();
		s.i0 = 3;
		s.s = -1;
		s.i2 = -1;
		s.write();
		s = new TestStruct(s.getPointer());
		s.read();
		
		Assert.assertEquals(1, s.i0); // 3 & 1
		Assert.assertEquals(0, s.i1);
		Assert.assertEquals(-1, s.s);
		Assert.assertEquals(7, s.i2); // -1 & (1 << 3)
		Assert.assertEquals(0, s.i3);
	}
	
	public static class FloatBitOffset extends BitFieldStruct {
		@Bits(5)
		public int i;
		public float f;
		public int j;
		public FloatBitOffset() { super(); }
		public FloatBitOffset(Pointer p) {
			super();
			useMemory(p);
		}
	}
	@Test
	public void testFloatOffsetBitField() {
		FloatBitOffset s = new FloatBitOffset();
		s.i = 27;
		s.f = (float)Math.PI;
		s.j = -535131351;

		s.write();
		s = new FloatBitOffset(s.getPointer());
		s.read();
		
		Assert.assertEquals(27, s.i);
		Assert.assertEquals((float)Math.PI, s.f, 0);
		Assert.assertEquals(-535131351, s.j);
	}
	
	public static class BigBitOffset extends BitFieldStruct {
		@Bits(31)
		public int i;
		@Bits(63)
		public long l;
		public int end;
		public BigBitOffset() { super(); }
		public BigBitOffset(Pointer p) {
			super();
			useMemory(p);
		}
	}
	Random random = new Random(System.currentTimeMillis());
	@Test
	public void testBigBitField() {
		for (int i = 5; i-- != 0;) {
			long randl = random.nextLong();
			BigBitOffset s = new BigBitOffset();
			s.i = -1;
			s.l = randl;
			s.end = 8;
	
			s.write();
			s = new BigBitOffset(s.getPointer());
			s.read();
			
//			long initRandl = randl;
//			long rr = (initRandl << 1) >> 1;
			randl = randl & ~(1L << 63);
			Assert.assertEquals(Integer.MAX_VALUE, s.i);
			Assert.assertEquals(randl, s.l);
			Assert.assertEquals(8, s.end);
		}
	}
	
	
	
	public static class BitOffsetPointer extends BitFieldStruct {
		@Bits(3)
		public int i;
		public Pointer p;
		public int end;
		public BitOffsetPointer() { super(); }
		public BitOffsetPointer(Pointer p) {
			super();
			useMemory(p);
		}
	}

	@Test
	public void testBitOffsetPointer() {
		Pointer pconstant = Pointer.createConstant(10);
		for (int i = 5; i-- != 0;) {
			BitOffsetPointer s = new BitOffsetPointer();
			s.i = -1;
			s.p = pconstant;
			s.end = 8;
	
			s.write();
			s = new BitOffsetPointer(s.getPointer());
			s.read();
			
			Assert.assertEquals(pconstant, s.p);
			Assert.assertEquals(7, s.i);
			Assert.assertEquals(8, s.end);
		}
	}
	public static class PaddingTest extends BitFieldStruct {
		@Bits(1)
		public int i;
		public int afterFields;
		public PaddingTest() {
			super();
		}
		public PaddingTest(Pointer p) {
			super();
			useMemory(p);
		}
	}
	@Test
	public void testPaddingAfterBitFields() {
		PaddingTest t = new PaddingTest();
		t.i = 15;
		t.afterFields = 3;
		t.write();
		
		PaddingTest o = new PaddingTest(t.getPointer());
		o.read();
		Assert.assertEquals(1, o.i);
		Assert.assertEquals(t.afterFields, o.afterFields);
		Assert.assertEquals(t.afterFields, t.getPointer().getInt(4));
		
	}
}
