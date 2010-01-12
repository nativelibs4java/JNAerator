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
import java.util.List;
import java.util.ListIterator;

/**
 * Default implementation of the ListenableList interface.<br/>
 * This class follows both the decorator and proxy patterns : it wraps an existing java.util.Collection and adds the listenable feature to it.<br/>
 * @author Olivier Chafik
 * @param <T> Type of the elements of the list
 */
public class DefaultListenableList<T> extends DefaultListenableCollection<T> implements ListenableList<T>{
	List<T> list;
	public DefaultListenableList(List<T> ilistst) {
		super(ilistst);
		this.list = ilistst;
	}
	public DefaultListenableList(List<T> list, ListenableSupport<T> collectionSupport) {
		super(list,collectionSupport);
		this.list = list;
	}
	public boolean add(T o) {
		add(size(), o);
		return true;
	}
	public void add(int index, T element) {
		list.add(index, element);
		collectionSupport.fireAdded(this,Collections.singleton(element), index, index);
	}
	public boolean addAll(int index, Collection<? extends T> c) {
		int initSize = list.size();
		if (!list.addAll(index, c)) {
			if (list.size() != initSize)
				throw new UnsupportedOperationException("Does not support listeners-enabled proxying of addAll(int, Collection) methods that are not atomical.");
			
			return false;
		}
		collectionSupport.fireAdded(this, new ArrayList<T>(c), initSize, initSize + c.size() - 1);
		return true;
	}
	public T get(int index) {
		return list.get(index);
	}
	public int indexOf(Object o) {
		return list.indexOf(o);
	}
	
	/**
	 * Not supported yet.
	 */
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}
	public T set(int index, T element) {
		T value = list.set(index, element);
		collectionSupport.fireUpdated(this, Collections.singleton(element), index, index);
		return value;
	}
	
	/**
	 * There are no more guarantees made on the behaviour of the sublists returned by this method upon list change than there are on java.util.List.subList.
	 */
	public List<T> subList(int fromIndex, int toIndex) {
		return new DefaultListenableList<T>(list.subList(fromIndex, toIndex),collectionSupport);
	}
	
	/**
	 * Not supported yet.
	 */
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}
	
	public T remove(int index) {
		T removed = list.remove(index);
		if (removed != null) {
			collectionSupport.fireRemoved(this,Collections.singleton(removed), index, index);
		}
		return removed;
	}
	public boolean remove(Object o) {
		int i = indexOf(o);
		return i >= 0 && remove(i) != null;
	}
	public void clear() {
		Collection<T> copy = new ArrayList<T>(this);
		collection.clear();
		collectionSupport.fireRemoved(this, copy, 0, copy.size() - 1);
	}
	
}
