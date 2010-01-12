package com.ochafik.lang.jnaerator.nativesupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Union;
public class NativeExportUtils {
	public static byte[] GetFileBytes(RandomAccessFile raf, long offset, int size) throws IOException {
	   raf.seek(offset);
	   if (size < 0) {
		   ByteArrayOutputStream out = new ByteArrayOutputStream();
		   byte[] b = new byte[1];
		   while (raf.read(b) != 0 && b[0] != 0)
			   out.write(b);
		   return out.toByteArray();
	   }
	   byte[] bytes = new byte[size];
	   raf.readFully(bytes);
	   return bytes;
	}
	public static int fromBigEndian(int i) {
		return ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN ? invertBytes(i) : i;
	}
	public static int fromLittleEndian(int i) {
		return ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN ? invertBytes(i) : i;
	}
	
	private static long fromBigEndian(Long i) {
		return ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN ? invertBytes(i) : i;
	}
	public static int invertBytes(long i) {
		return invertBytes((int)(i & 0xffffffff)) << 32 | invertBytes((int)(i >> 32)); 
	}
	public static int invertBytes(int i) {
//		String h = Integer.toHexString(i);
		int j = (invertBytes((short)(i & 0xffff)) & 0xffff) << 16 | (invertBytes((short)(i >> 16)) & 0xffff);
//		String h2 = Integer.toHexString(j);
		return j;
	}
	public static short invertBytes(short i) {
//		String h = Integer.toHexString(i).substring(4);
//		int k = i & 0xff;
//		h = Integer.toHexString(k);
//		k <<= 8;
//		h = Integer.toHexString(k);
//		int l = i >> 8;
//		h = Integer.toHexString(l);
//		int m = k | l;
//		h = Integer.toHexString(m);
//		
//		
		short j = (short)(((i & 0xff) << 8) | (i >> 8) & 0xff);
//		String h2 = Integer.toHexString(j).substring(4);
		return j;
	}
	public static <S extends Structure> S deserializeBigEndianStruct(S struct, RandomAccessFile raf, long offset) throws IOException {
		return fromBigEndianFields(deserializeStruct(struct, raf, offset));
	}
	static <T> T fromBigEndianFields(T o) {

		try {
			for (java.lang.reflect.Field f : o.getClass().getFields())
			{
				if ((f.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) != 0)
					continue;
				if ((f.getModifiers() & Modifier.PUBLIC) == 0)
					continue;
				
				if (f.getType() == Integer.TYPE)
					f.set(o, fromBigEndian((Integer)f.get(o)));
				else if (f.getType() == Short.TYPE)
					f.set(o, (short)fromBigEndian((Short)f.get(o)));
				else if (f.getType() == Long.TYPE)
					f.set(o, (long)fromBigEndian((Long)f.get(o)));
				else if (Union.class.isAssignableFrom(f.getType())) {
					Union u = (Union)f.get(o);
					fromBigEndianFields(u);
				} else if (Structure.class.isAssignableFrom(f.getType())) {
					Structure u = (Structure)f.get(o);
					fromBigEndianFields(u);
				} 
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return o;
	}
	
	public static <S extends Structure> S deserializeStruct(S struct, RandomAccessFile raf, long offset) throws IOException {
	   byte[] bytes = GetFileBytes(raf, offset, struct.size());
	   struct.getPointer().write(0, bytes, 0, bytes.length);
//	   int i = struct.getPointer().getInt(0);
//	   byte[] rb = struct.getPointer().getByteArray(0, 4);
	   struct.read();
	   return struct;
	}
	public static String createSourceFile(File sourceFile, List<ParsedExport> dllExports) {
		if (dllExports == null)
			return null;
		
		StringBuilder b = new StringBuilder();
		b.append("#line \"" + sourceFile + "\"\n");
		for (ParsedExport ex : dllExports) {
			b.append("// @mangling " + ex.mangling + "\n");
			b.append(ex.demangled + ";\n");
			b.append("\n");
		}
		return b.toString();
	}
	public static void main(String[] args) {
		try {
//			File f = new File("C:\\Prog\\C++\\DllExportTest\\Release\\DllTest.dll");
//			System.out.println(DllExport.createSourceFile(f, DllExport.parseDllExports(f)));
			
			File f = new File("/System/Library/Frameworks/CoreFoundation.framework/CoreFoundation");
			System.out.println(FatMachOExport.parseDllExports(f));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	public static class ParsedExport {
		public String mangling, demangled, library;
	}
	public static int readLittleEndianInt(RandomAccessFile raf, long l) throws IOException {
        raf.seek(l);
        byte[] bytes = new byte[4];
        raf.read(bytes);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(0);
	}
	public static int readBigEndianInt(RandomAccessFile raf, long l) throws IOException {
        raf.seek(l);
        byte[] bytes = new byte[4];
        raf.read(bytes);
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer().get(0);
	}
}
