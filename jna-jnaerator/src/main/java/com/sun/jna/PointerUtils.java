package com.sun.jna;

public class PointerUtils {
	public static long getAddress(Pointer p) {
		return p.peer;
	}
	public static long getAddress(PointerType p) {
		return p.getPointer().peer;
	}
	public static Pointer fromAddress(long peer) {
		return new Pointer(peer);
	}
	
}
