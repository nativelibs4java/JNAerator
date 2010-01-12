/* 
 * Copyright (c) 2009 Olivier Chafik, All Rights Reserved
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
package com.sun.jna;

import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Support for C bit fields
 * @author Olivier Chafik
 */
public class BitFields {
	private static abstract class PrimHandler {
		abstract long longValue(Object value);
		abstract Object objectValue(long value);
		abstract void writeLong(Pointer p, long offset, long value);
		abstract long readLong(Pointer p, long offset);
		abstract int size();
		abstract void writeObject(Pointer pointer, long offset, Object value);
		abstract Object readObject(Pointer p, long offset);
		boolean supportsBitOffset() {
			return true;
		}
		boolean supportsBitLength() {
			return true;
		} 
	}
	private static abstract class NonIntHandler extends PrimHandler {
		public void writeLong(Pointer p, long offset, long value) {
			throw new UnsupportedOperationException();
		}
		public long readLong(Pointer p, long offset) {
			throw new UnsupportedOperationException();
		}
		public Object objectValue(long value) {
			throw new UnsupportedOperationException();
		}
		public long longValue(Object value) {
			throw new UnsupportedOperationException();
		}
		public boolean supportsBitLength() {
			return false;
		}
	}
	private static final class StringHandler extends NonIntHandler {
		final boolean wide;
		public StringHandler(boolean wide) {
			this.wide = wide;
		}
		public Object readObject(Pointer p, long offset) {
			p = p.getPointer(offset);
            return p != null ? wide ? (Object)new WString(p.getString(0, true)) : (Object)p.getString(0) : null;
		}		
		public int size() {
			return Native.POINTER_SIZE;
		}

		
		public void writeObject(Pointer pointer, long offset, Object value) {
			pointer.setPointer(offset, (Pointer)value);
		}
		
	}
	private static final PrimHandler 
	INT_HANDLER = new PrimHandler() {
		public void writeLong(Pointer p, long offset, long value) {
			p.setInt(offset, (int)value);
		}
		public long readLong(Pointer p, long offset) {
			return p.getInt(offset);
		}
		public Object objectValue(long value) {
			return new Integer((int)value);
		}
		public long longValue(Object value) {
			return ((Integer)value).longValue();
		}
		public void writeObject(Pointer p, long offset, Object value) {
			p.setInt(offset, value == null ? 0 : ((Integer)value).intValue());
		}
		public Object readObject(Pointer p, long offset) {
			return new Integer(p.getInt(offset));
		}
		public int size() {
			return 4;
		}
	},
	LONG_HANDLER = new PrimHandler() {
		public void writeLong(Pointer p, long offset, long value) {
			p.setLong(offset, value);
		}
		public long readLong(Pointer p, long offset) {
			return p.getLong(offset);
		}
		public Object objectValue(long value) {
			return new Long(value);
		}
		public long longValue(Object value) {
			return ((Long)value).longValue();
		}
		public void writeObject(Pointer p, long offset, Object value) {
			p.setLong(offset, value == null ? 0 : ((Long)value).longValue());
		}
		public Object readObject(Pointer p, long offset) {
			return new Long(p.getLong(offset));
		}
		public int size() {
			return 8;
		}
	},
	SHORT_HANDLER = new PrimHandler() {
		public void writeLong(Pointer p, long offset, long value) {
			p.setShort(offset, (short)value);
		}
		public long readLong(Pointer p, long offset) {
			return p.getShort(offset);
		}
		public Object objectValue(long value) {
			return new Short((short)value);
		}
		public long longValue(Object value) {
			return ((Short)value).longValue();
		}
		public void writeObject(Pointer p, long offset, Object value) {
			p.setShort(offset, value == null ? 0 : ((Short)value).shortValue());
		}
		public Object readObject(Pointer p, long offset) {
			return new Short(p.getShort(offset));
		}
		public int size() {
			return 2;
		}
	},
	BYTE_HANDLER = new PrimHandler() {
		public void writeLong(Pointer p, long offset, long value) {
			p.setByte(offset, (byte)value);
		}
		public long readLong(Pointer p, long offset) {
			return p.getByte(offset);
		}
		public Object objectValue(long value) {
			return new Byte((byte)value);
		}
		public long longValue(Object value) {
			return ((Byte)value).longValue();
		}
		public void writeObject(Pointer p, long offset, Object value) {
			p.setByte(offset, value == null ? 0 : ((Byte)value).byteValue());
		}
		public Object readObject(Pointer p, long offset) {
			return new Byte(p.getByte(offset));
		}
		public int size() {
			return 1;
		}
	},
	CHAR_HANDLER = new PrimHandler() {
		public void writeLong(Pointer p, long offset, long value) {
			p.setChar(offset, (char)value);
		}
		public long readLong(Pointer p, long offset) {
			return p.getChar(offset);
		}
		public Object objectValue(long value) {
			return new Character((char)value);
		}
		public long longValue(Object value) {
			return ((Character)value).charValue();
		}
		public void writeObject(Pointer p, long offset, Object value) {
			p.setChar(offset, value == null ? (char)0 : ((Character)value).charValue());
		}
		public Object readObject(Pointer p, long offset) {
			return new Character(p.getChar(offset));
		}
		public int size() {
			return 2;
		}
	},
	BOOL_HANDLER = new PrimHandler() {
		public void writeLong(Pointer p, long offset, long value) {
			p.setByte(offset, (byte)value);
		}
		public long readLong(Pointer p, long offset) {
			return p.getByte(offset);
		}
		public Object objectValue(long value) {
			return ((byte)value) == 0 ? Boolean.FALSE : Boolean.TRUE;
		}
		public long longValue(Object value) {
			return ((Boolean)value).booleanValue() ? -1 : 0;
		}
		public void writeObject(Pointer p, long offset, Object value) {
			p.setByte(offset, value == null ? 0 : (byte)(Boolean.TRUE.equals(value) ? -1 : 0));
		}
		public Object readObject(Pointer p, long offset) {
			return p.getByte(offset) == 0 ? Boolean.FALSE : Boolean.TRUE;
		}
		public int size() {
			return 1;
		}
	},
	DOUBLE_HANDLER = new NonIntHandler() {
		public long readLong(Pointer p, long offset) {
			return p.getLong(offset);
		}
		public void writeLong(Pointer p, long offset, long value) {
			p.setLong(offset, value);
		}
		public long longValue(Object value) {
			return Double.doubleToRawLongBits(((Double)value).doubleValue());
		}
		public Object objectValue(long value) {
			return new Double(Double.longBitsToDouble((long)value));
		}
		public void writeObject(Pointer p, long offset, Object value) {
			p.setDouble(offset, value == null ? 0 : ((Double)value).doubleValue());
		}
		public Object readObject(Pointer p, long offset) {
			return new Double(p.getDouble(offset));
		}
		public int size() {
			return 8;
		}
	},
	FLOAT_HANDLER = new NonIntHandler() {
		public long readLong(Pointer p, long offset) {
			return p.getInt(offset);
		}
		public void writeLong(Pointer p, long offset, long value) {
			p.setInt(offset, (int)value);
		}
		public long longValue(Object value) {
			return Float.floatToRawIntBits(((Float)value).floatValue());
		}
		public Object objectValue(long value) {
			return new Float(Float.intBitsToFloat((int)value));
		}
		public void writeObject(Pointer p, long offset, Object value) {
			p.setFloat(offset, value == null ? 0 : ((Float)value).floatValue());
		}
		public Object readObject(Pointer p, long offset) {
			return new Float(p.getFloat(offset));
		}
		public int size() {
			return 4;
		}
	},
//	POINTER_HANDLER = new NonIntHandler() {
//	public void writeObject(Pointer p, long offset, Object value) {
//	p.setPointer(offset, ((Pointer)value));
//}
//public Object readObject(Pointer p, long offset) {
//	return p.getPointer(offset);
//}
//public int size() {
//	return Native.POINTER_SIZE;
//}
//}
	STRING_HANDLER = new StringHandler(false),
	WSTRING_HANDLER = new StringHandler(true)
	;
	
	private static final Map primHandlers = new IdentityHashMap(10);
	static {
		primHandlers.put(Integer.TYPE, INT_HANDLER);
		primHandlers.put(Integer.class, INT_HANDLER);
		
		primHandlers.put(Long.TYPE, LONG_HANDLER);
		primHandlers.put(Long.class, LONG_HANDLER);
		
		primHandlers.put(Short.TYPE, SHORT_HANDLER);
		primHandlers.put(Short.class, SHORT_HANDLER);
		
		primHandlers.put(Byte.TYPE, BYTE_HANDLER);
		primHandlers.put(Byte.class, BYTE_HANDLER);
		
		primHandlers.put(Character.TYPE, CHAR_HANDLER);
		primHandlers.put(Character.class, CHAR_HANDLER);
		
		primHandlers.put(Boolean.TYPE, BOOL_HANDLER);
		primHandlers.put(Boolean.class, BOOL_HANDLER);
		
		primHandlers.put(Float.TYPE, FLOAT_HANDLER);
		primHandlers.put(Float.class, FLOAT_HANDLER);
		
		primHandlers.put(Double.TYPE, DOUBLE_HANDLER);
		primHandlers.put(Double.class, DOUBLE_HANDLER);
		
//		primHandlers.put(Pointer.class, POINTER_HANDLER);
		primHandlers.put(String.class, STRING_HANDLER);
		primHandlers.put(WString.class, WSTRING_HANDLER);
	}
	private static PrimHandler getHandlerWithAtLeastNBytes(final int n) {
		switch (n) {
		case 1:
			return BYTE_HANDLER;
		case 2:
			return SHORT_HANDLER;
		case 3:
		case 4:
			return INT_HANDLER;
		case 5:
		case 6:
		case 7:
		case 8:
			return LONG_HANDLER;
		default:
			return null;
			//throw new UnsupportedOperationException("TODO No handler for " + n + " bytes !");
		}
	}
	
	private static PrimHandler getPrimHandler(Class type, int bitOffset, int bits) {
		PrimHandler handler = (PrimHandler)primHandlers.get(type);
//		if (handler == null && Pointer.class.isAssignableFrom(type))
//			handler = (PrimHandler)primHandlers.get(Pointer.class);
		if (handler == null && (bitOffset | bits) != 0 || 
			handler != null && (
				!handler.supportsBitOffset() && (bitOffset != 0) ||
				!handler.supportsBitLength() && (bits != 0)
			)
		)
			throw new UnsupportedOperationException("Bit fields only support integral fields !!!");
		//if ((bits & ~63) != 0)
		//	throw new UnsupportedOperationException("Bit fields cannot be larger than 64 bits !!!");
		
		return handler;
	}
	
	static void print(BigInteger bi) {
		for (int i = 0, len = bi.bitLength(); i < len; i++)
			System.out.print(bi.testBit(i) ? '1' : '0');
		System.out.println();
	}
	private static BigInteger shiftedMask(int bits, int bitOffset) {
		BigInteger mask = BigInteger.valueOf(bits == 0 ? 0 : 1L);
		if (bits != 0)
			mask = mask.shiftLeft(bits);
		mask = mask.subtract(BigInteger.valueOf(1));
		if (bitOffset != 0) {
			mask = mask.shiftLeft(bitOffset);
			// Fix sign extension :
			for (int i = 0; i < bitOffset; i++)
				mask.clearBit(i);
		}
		return mask;
	}

	private static byte[] getBigEndianByteArray(Pointer pointer, long offset, int bytesToFetch) {
		byte[] bs = pointer.getByteArray(offset, bytesToFetch);
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			revert(bs);
		return bs;
	}
	private static void setBigEndianByteArray(Pointer pointer, long offset, byte[] bs) {
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			revert(bs);
		pointer.write(offset, bs, 0, bs.length);
	}
	private static void revert(byte[] bs) {
		for (int i = 0, len = bs.length, sup = len >>> 1; i < sup; i++) {
			int j = len - i - 1;
			
			byte t = bs[i];
			bs[i] = bs[j];
			bs[j] = t;
		}
	}
	public static final Object UNHANDLED_TYPE = new Object() {};
	
	public static boolean setPrimitiveValue(Pointer pointer, long offset, int bitOffset, int bits, Object value, Class type) {
		PrimHandler handler = getPrimHandler(type, bitOffset, bits);
		if (handler == null)
			return false;
		
		if ((bitOffset | bits) == 0) {
			handler.writeObject(pointer, offset, value);
			return true;
		}
	
		if (bits <= 0)
			bits = handler.size() << 3;
		
		// Read existing alien bits to OR them, 
		// and make sure we read/write bits up to bits + bitOffset 
		// (may need to use larger type than that of the value for read operation)
		int bitLen = bits + bitOffset;
		int bytesToFetch = (bitLen >> 3) + ((bitLen & 7) == 0 ? 0 : 1);
		PrimHandler io = getHandlerWithAtLeastNBytes(bytesToFetch);
		if (io != null) {
			long longValue = handler.longValue(value);
			longValue <<= bitOffset;
			
			long existing = io.readLong(pointer, offset);
			if (bits != 0) {
				long mask = ((1L << bits) - 1) << bitOffset;
				longValue &= mask;
				existing &= ~mask;
			}
			longValue |= existing;
			io.writeLong(pointer, offset, longValue);
		} else {
			BigInteger bigValue = BigInteger.valueOf(handler.longValue(value));
			bigValue = bigValue.shiftLeft(bitOffset);
			
			byte[] bs = getBigEndianByteArray(pointer, offset, bytesToFetch);
			BigInteger existing = new BigInteger(bs);
			BigInteger mask = shiftedMask(bits, bitOffset);
			bigValue = bigValue.and(mask);
			existing = existing.and(mask.not());
			bigValue = bigValue.or(existing);
			
			setBigEndianByteArray(pointer, offset, bigValue.toByteArray());
		}
		return true;
	}
	public static Object getPrimitiveValue(Pointer pointer, long offset, int bitOffset, int bits, Class type) {
		PrimHandler handler = getPrimHandler(type, bitOffset, bits);
		if (handler == null)
			return UNHANDLED_TYPE;
		
		if ((bitOffset | bits) == 0)
			return handler.readObject(pointer, offset);

		if (bits <= 0)
			bits = handler.size() << 3;
		
		// Read bits to up to bits + bitOffset - 1
		int bitLen = bits + bitOffset;
		int bytesToFetch = (bitLen >> 3) + ((bitLen & 7) == 0 ? 0 : 1);
		
		long longValue;
		PrimHandler io = getHandlerWithAtLeastNBytes(bytesToFetch);
		if (io != null) {
			longValue = io.readLong(pointer, offset);
			longValue >>= bitOffset;
			if (bits != 0) {
				long mask = (1L << bits) - 1;
				longValue &= mask;
			}
		} else {
			BigInteger bigValue = new BigInteger(getBigEndianByteArray(pointer, offset, bytesToFetch));
			bigValue = bigValue.shiftRight(bitOffset);
			if (bits != 0) {
				BigInteger mask = shiftedMask(bits, 0);
				bigValue = bigValue.and(mask);
			}
			longValue = bigValue.longValue();
		}
		return handler.objectValue(longValue);
	}
	
	
}

