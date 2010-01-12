/**
 * 
 */
package com.ochafik.util;

import java.util.Iterator;

public interface IntArray extends Iterable<Integer> {
	public int[] getBackingArray();
	public int size();
	public int get(int pos);
	public int[] toArray();
	
	public class IntIterator implements Iterator<Integer> {
		int i = -1;
		IntArray array;
		
		public boolean hasNext() {
			return i < array.size() - 1;
		}

		public Integer next() {
			return array.get(++i);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}