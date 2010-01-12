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

import java.util.Comparator;
import java.util.SortedSet;

/**
 * Default implementation of the ListenableSet and SortedSet interface.<br/>
 * This class follows both the decorator and proxy patterns : it wraps an existing java.util.Set and adds the listenable feature to it.<br/>
 * @author Olivier Chafik
 * @param <T> Type of the elements of the set
 */
class DefaultListenableSortedSet<T> extends DefaultListenableSet<T> implements ListenableSortedSet<T> {
	public DefaultListenableSortedSet(SortedSet<T> set, ListenableSupport<T> collectionSupport) {
		super(set,collectionSupport);
	}
	public DefaultListenableSortedSet(SortedSet<T> set) {
		super(set);
	}
	public Comparator<? super T> comparator() {
		return ((SortedSet<T>)collection).comparator();
	}
	public T first() {
		return ((SortedSet<T>)collection).first();
	}
	public SortedSet<T> headSet(T toElement) {
		return ((SortedSet<T>)collection).headSet(toElement);
	}
	public T last() {
		return ((SortedSet<T>)collection).last();
	}
	public SortedSet<T> subSet(T fromElement, T toElement) {
		return ((SortedSet<T>)collection).subSet(fromElement, toElement);
	}
	public SortedSet<T> tailSet(T fromElement) {
		return ((SortedSet<T>)collection).tailSet(fromElement);
	}	
}
