/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
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
package com.ochafik.lang.jnaerator.parser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SemiUnmodifiableList<T> implements List<T> {
	List<T> list;
	List<T> getList() {
		return list;
	}
	public SemiUnmodifiableList(List<T> list) {
		this.list = list;
	}
	public boolean add(T o) {
		throw new UnsupportedOperationException();
	}
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}
	public void clear() {
		throw new UnsupportedOperationException();
	}
	public boolean contains(Object o) {
		return list.contains(o);
	}
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}
	public T get(int index) {
		return list.get(index);
	}
	public int indexOf(Object o) {
		return list.indexOf(o);
	}
	public boolean isEmpty() {
		return list.isEmpty();
	}
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Iterator<T> it = list.iterator();

			public boolean hasNext() {
				return it.hasNext();
			}

			public T next() {
				return it.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}
	public ListIterator<T> listIterator(final int index) {
		return new ListIterator<T>() {
			ListIterator<T> it = list.listIterator(index);

			public boolean hasNext() {
				return it.hasNext();
			}

			public T next() {
				return it.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public void add(T o) {
				throw new UnsupportedOperationException();
			}

			public boolean hasPrevious() {
				return it.hasPrevious();
			}

			public int nextIndex() {
				return it.nextIndex();
			}

			public T previous() {
				return it.previous();
			}

			public int previousIndex() {
				return it.previousIndex();
			}

			public void set(T o) {
				throw new UnsupportedOperationException();
			}
		};
	}
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}
	public int size() {
		return list.size();
	}
	public List<T> subList(int fromIndex, int toIndex) {
		return new SemiUnmodifiableList<T>(list.subList(fromIndex, toIndex));
	}
	public Object[] toArray() {
		return list.toArray();
	}
	public <U> U[] toArray(U[] a) {
		return list.toArray(a);
	}
	@Override
	public String toString() {
		return list.toString();
	}
}
