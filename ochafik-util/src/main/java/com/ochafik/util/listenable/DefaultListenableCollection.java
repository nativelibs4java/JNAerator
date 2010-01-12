/*
   Copyright 2008 Olivier Chafik

   Licensed under the Apache License, Version 2.0 (the License);
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an AS IS BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   This file comes from the Jalico project (Java Listenable Collections)

       http://jalico.googlecode.com/.
*/
package com.ochafik.util.listenable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation of the ListenableCollection interface.<br/>
 * This class follows both the decorator and proxy patterns : it wraps an existing java.util.Collection and adds the listenable feature to it.<br/>
 * @author Olivier Chafik
 * @param <T> Type of the elements of the collection
 */
public class DefaultListenableCollection<T> implements ListenableCollection<T> {
	protected Collection<T> collection;
 	protected ListenableSupport<T> collectionSupport;
	
 	public void addCollectionListener(CollectionListener<T> l) {
 		collectionSupport.addCollectionListener(l);
 	}
 	public void removeCollectionListener(CollectionListener<T> l) {
 		collectionSupport.removeCollectionListener(l);
 	}
 	public DefaultListenableCollection(Collection<T> collection) {
		this(collection,new ListenableSupport<T>());
	}
	public DefaultListenableCollection(Collection<T> collection, ListenableSupport<T> collectionSupport) {
		this.collection = collection;
		this.collectionSupport = collectionSupport;
	}
	public boolean add(T o) {
		boolean added = collection.add(o);
		int index = collection instanceof List<?> ? size() : -1;
		if (added) {
			collectionSupport.fireAdded(this,Collections.singleton(o), index, index);
		} else {
			collectionSupport.fireUpdated(this,Collections.singleton(o), index, index);
		}
		return added;
	}
	public boolean addAll(Collection<? extends T> c) {
		if (!collectionSupport.hasListeners())
			return collection.addAll(c);
		
		int max = c.size();
		Collection<T> addedElements = new ArrayList<T>(max), updatedElements = new ArrayList<T>(max);
		for (T t : c) {
			(collection.add(t) ? addedElements : updatedElements).add(t);
		}
		int firstIndex = collection instanceof List<?> ? size() : -1, 
				lastIndex = firstIndex < 0 ? -1 : firstIndex + addedElements.size() - 1; 
		collectionSupport.fireAdded(this, addedElements, firstIndex, lastIndex);
		collectionSupport.fireUpdated(this, updatedElements, firstIndex, lastIndex);

		return !addedElements.isEmpty();
	}
	public void clear() {
		if (!collectionSupport.hasListeners()) {
			collection.clear();
			return;
		}
		Collection<T> copy = new ArrayList<T>(collection);
		collection.clear();
		collectionSupport.fireRemoved(this, copy);
	}
	public boolean contains(Object o) {
		return collection.contains(o);
	}
	public boolean containsAll(Collection<?> c) {
		return collection.containsAll(c);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		return collection.equals(obj);
	}
	@Override
	public int hashCode() {
		return collection.hashCode();
	}
	public boolean isEmpty() {
		return collection.isEmpty();
	}
	
	protected class ListenableIterator implements Iterator<T> {
		Iterator<T> iterator;
		T lastValue;
		DefaultListenableCollection<T> listenableCollection;
		public ListenableIterator(DefaultListenableCollection<T> listenableCollection,Iterator<T> iterator) {
			this.iterator = iterator;
			this.listenableCollection = listenableCollection;
		}
		public boolean hasNext() {
			return iterator.hasNext();
		}
		public T next() {
			lastValue = iterator.next();
			return lastValue;
		}
		public void remove() {
			iterator.remove();
			collectionSupport.fireRemoved(listenableCollection,Collections.singleton(lastValue));
		}
	};
	
	public Iterator<T> iterator() {
		return new ListenableIterator(this,collection.iterator());
	}
	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		boolean removed = collection.remove(o);
		if (removed) {
			collectionSupport.fireRemoved(this,Collections.singleton((T)o));
		}
		return removed;
	}
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<?> c) {
		if (!collectionSupport.hasListeners())
			return collection.removeAll(c);
		
		Collection<T> removedElements = new ArrayList<T>(c.size());
		for (Object t : c) {
			boolean removed = collection.remove(t);
			if (removed) 
				removedElements.add((T)t);
		}
		collectionSupport.fireRemoved(this, removedElements);
		
		return !removedElements.isEmpty();
	}
	public boolean retainAll(Collection<?> c) {
		if (!collectionSupport.hasListeners())
			return collection.retainAll(c);
		
		Collection<T> removedElements = new ArrayList<T>(c.size());
		for (Iterator<T> it = iterator(); it.hasNext();) {
			T e = it.next();
			if (!c.contains(e)) {
				it.remove();
				removedElements.add(e);
			}
		}
		collectionSupport.fireRemoved(this, removedElements);
		return !removedElements.isEmpty();
	}
	public int size() {
		return collection.size();
	}
	public Object[] toArray() {
		return collection.toArray();
	}
	public <V> V[] toArray(V[] a) {
		return collection.toArray(a);
	}
	@Override
	public String toString() {
		return collection.toString();
	}
}
