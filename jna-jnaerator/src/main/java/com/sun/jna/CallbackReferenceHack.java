package com.sun.jna;

public class CallbackReferenceHack {
	public static Callback getCallback(Class<?> type, Pointer p) {
		return CallbackReference.getCallback(type, p);
	}
	public static Pointer getFunctionPointer(Callback cb) {
		return CallbackReference.getFunctionPointer(cb);
	}
}
