/*
	Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (https://github.com/nativelibs4java/JNAerator).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator.runtime;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.Buffer;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public abstract class Structure<S extends Structure<S, V, R>, V extends S, R extends S> 
	extends com.sun.jna.Structure
	implements 
		Comparable<Structure<S, V, R>>,
		StructureType,
		StructureTypeDependent
{	
    protected Structure(Pointer p) {
        super(p);
    }
    protected Structure() {
        super();
    }
	public interface ByReference extends com.sun.jna.Structure.ByReference, StructureTypeDependent {}
	public interface ByValue extends com.sun.jna.Structure.ByValue, StructureTypeDependent {}
	
	transient WeakReference<StructureType> dependency;
	//@Override
	public void setDependency(StructureType type) {
		this.dependency = type == null ? null : new WeakReference<StructureType>(type);
	}
	protected void readDependency() {
		StructureType dep;
		if (dependency == null || (dep = dependency.get()) == null)
			return;
		dep.read();
	}
	
	@Override
	public void read() {
		super.read();
		readDependency();
	}
	@Override
	public void write() {
		super.write();
		readDependency();
	}
	
	protected <T extends Structure<S, V, R>> T setupClone(T clone, StructureType dependency) {
		write();
		clone.use(getPointer());
		clone.setDependency(this);
		return clone;
	}
	
	//getFieldOffset(String fieldName);
	
	protected abstract S newInstance();
	protected abstract V newByValue();
	protected abstract R newByReference();
	
	public R byReference() { return setupClone(newByReference(), this); }
	public V byValue() { return setupClone(newByValue(), this); }
	public S clone() { return setupClone(newInstance(), this); }

	@SuppressWarnings("unchecked")
	public static <S extends Structure>
			S[] newArray(Class<S> structClass, int arrayLength) {
		try {
			S first = structClass.newInstance();
			int sz = first.size();
			Memory mem = new Memory(arrayLength * sz);
			first.use(mem);
			S[] array = (S[])first.castToArray(arrayLength);
			for (int i = 1; i < arrayLength; i++) {
				S s = structClass.newInstance();
				s.use(mem, i * sz);
				array[i] = s;
			}
			return array;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	/**
	 * @deprecated use castToArray instead
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	@Override
	public S[] toArray(int size) {
		return (S[])super.toArray(size);
	}
	/**
	 * @deprecated use castToArray instead
	 * @return the structure as an array of S
	 */
	@Deprecated
	public S[] toArray() {
		return toArray(1);
	}
	/**
	 * @deprecated use castToArray instead
	 * @param size the size of the array
	 * @return the structure as an array of R
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public R[] toReferenceArray(int size) {
		return (R[])byReference().toArray(size);
	}
	/**
	 * @deprecated use castToArray instead
	 *
	 * @param size the size of the array
	 * @return the structure as an array of V
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public V[] toValueArray(int size) {
		return (V[])byValue().toArray(size);
	}
	/**
	 * @deprecated use castToArray instead
	 * @return the structure as an array of R
	 */
	@Deprecated
	public R[] toReferenceArray() {
		return toReferenceArray(1);
	}
	/**
	 * @deprecated use castToArray instead
	 * @return the structure as an array of R
	 */
	@Deprecated
	public V[] toValueArray() {
		return toValueArray(1);
	}

	/**
	 * @deprecated use castToArray instead
	 * @param array the array to convert
	 * @return the structure as an array of S
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	@Override
	public S[] toArray(com.sun.jna.Structure[] array) {
		return (S[])super.toArray(array);
	}
	
	@SuppressWarnings("unchecked")
	public S[] castToArray(int size) {
		return (S[])super.toArray(size);
	}
	public S[] castToArray() {
		return castToArray(1);
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	public R[] castToReferenceArray(int size) {
		return (R[])byReference().toArray(size);
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	public V[] castToValueArray(int size) {
		return (V[])byValue().toArray(size);
	}
	public R[] castToReferenceArray() {
		return castToReferenceArray(1);
	}
	public V[] castToValueArray() {
		return castToValueArray(1);
	}
	@SuppressWarnings("unchecked")
	public S[] castToArray(com.sun.jna.Structure[] array) {
		return (S[])super.toArray(array);
	}

	/**
	 * Simply does a memcmp between the two memory blocks of the two structures
     */
	//@Override
	public int compareTo(Structure<S, V, R> o) {
        if (o == this)
            return 0;
        if (!(o instanceof Structure<?, ?, ?>))
        	return 1;
        
        int size = size();
        int d = size - ((Structure<?, ?, ?>)o).size();
        if (d != 0)
        	return d;
        
        Structure<?, ?, ?> s = (Structure<?, ?, ?>)o;
        if (getPointer().equals(s.getPointer()))
        	return 0;
        
        write();
        s.write();
        
        byte[] bytes1 = getPointer().getByteArray(0, size);
        byte[] bytes2 = s.getPointer().getByteArray(0, size);
        
        for (int i = 0; i < size; i++) {
        	byte b1 = bytes1[i], b2 = bytes2[i];
        	if (b1 != b2)
        		return b1 < b2 ? -1 : 1;
        }
        return 0;
    }
	
	protected <T extends Union<?, ?, ?>> T setupClone(T clone) {
		write();
		clone.use(getPointer());
		return clone;
	}
	
	public S use(Pointer m) {
		return use(m, 0);
	}
	@SuppressWarnings("unchecked")
	public S use(Pointer m, long byteOffset) {
		useMemory(m, (int)byteOffset);
		read();
		return (S)this;
	}
	public S use(Buffer m) {
		return use(m, 0);
	}
	@SuppressWarnings("unchecked")
	public S use(Buffer b, long byteOffset) {
		useMemory(Native.getDirectBufferPointer(b), (int)byteOffset);
		return (S)this;
	}
	
}
