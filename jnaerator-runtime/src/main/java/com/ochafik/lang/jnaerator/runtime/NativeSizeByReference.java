package com.ochafik.lang.jnaerator.runtime;

import com.sun.jna.ptr.ByReference;

public class NativeSizeByReference extends ByReference {
    public NativeSizeByReference() {
        this(new NativeSize(0));
    }

    public NativeSizeByReference(NativeSize value) {
        super(NativeSize.SIZE);
        setValue(value);
    }

    public void setValue(NativeSize value) {
        if (NativeSize.SIZE == 4)
			getPointer().setInt(0, value.intValue());
		else if (NativeSize.SIZE == 8)
			getPointer().setLong(0, value.longValue());
		else
			throw new RuntimeException("GCCLong has to be either 4 or 8 bytes.");
    }

    public NativeSize getValue() {
		if (NativeSize.SIZE == 4)
			return new NativeSize(getPointer().getInt(0));
		else if (NativeSize.SIZE == 8)
			return new NativeSize(getPointer().getLong(0));
		else
			throw new RuntimeException("GCCLong has to be either 4 or 8 bytes.");
    }
}
