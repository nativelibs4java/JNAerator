/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.util;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
public class CollectionAdapter<U,V> extends AbstractCollection<V> {
	protected Collection<U> collection;
	protected Adapter<U,V> adapter;
	public CollectionAdapter(Collection<U> collection,Adapter<U,V> adapter) {
		this.collection=collection;
		this.adapter=adapter;
	}
	@Override
	public Iterator<V> iterator() {
		return new IteratorAdapter(collection.iterator());
	}
	@Override
	public int size() {
		return collection.size();
	}
	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}
	@Override
	public void clear() {
		collection.clear();
	}
	@Override
	public boolean add(V o) {
		return collection.add(adapter.reAdapt(o));
	}
	@Override
	public boolean contains(Object o) {
		for (U element : collection) {
			if (adapter.adapt(element).equals(o)) {
				return true;
			}
		}
		return false;
	}
	protected class IteratorAdapter implements Iterator<V> {
		Iterator<U> iterator;
		public IteratorAdapter(Iterator<U> iterator) {
			this.iterator=iterator;
		}
		public boolean hasNext() {
			return iterator.hasNext();
		}
		public V next() {
			return adapter.adapt(iterator.next());
		}
		public void remove() {
			iterator.remove();
		}
	}
	/*protected abstract V adapt(U value);
	protected U reAdapt(V value) {
		throw new UnsupportedOperationException();
	}*/
}
