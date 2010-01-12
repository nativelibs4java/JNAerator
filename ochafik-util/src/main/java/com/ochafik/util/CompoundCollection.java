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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/*
 * This is a Collection that gathers the content of several collections, without the need to create
 *  a big structure and add them all to it. It is space and speed efficient (except for containsAll(Collection)),
 *  however it does not implement add(U) and addAll(Collection<U>) optional methods. 
 *  
 * It should be use in cases where you want to iterate through several collections as through one.
 */
public class CompoundCollection<U> implements Collection<U> {
	Collection<Collection<? extends U> > collections;
	public CompoundCollection() {
		collections=new LinkedList<Collection<? extends U> >();
	}
	public CompoundCollection(Collection<Collection<? extends U>> lists) {
		collections = new ArrayList<Collection<? extends U>>(lists);
	}
	public CompoundCollection(Collection<? extends U>... lists) {
		this(Arrays.asList(lists));
	}
	public void addComponent(Collection<U> col) {
        collections.add(col);
    }
    public boolean removeComponent(Collection<U> col) {
        return collections.remove(col);
    }
    public int size() {
		int s=0;
		for (Collection<? extends U> col : collections) {
			s+=col.size();
		}
		return s;
	}
	public void clearComponents() {
		collections.clear();
	}
	public void clear() {
		for (Collection<? extends U> col : collections) {
			col.clear();
		}
	}
	public boolean isEmpty() {
		for (Collection<? extends U> col : collections) {
			if (!col.isEmpty()) {
				return false; 
			}
		}
		return true;
	}
	public Object[] toArray() {
		int len=size();
		Object[] a=new Object[len];
		int i=0;
		for (U element : this) {
			a[i]=element;
		}
		return a;
	}
    @SuppressWarnings("unchecked")
    //@Deprecated
    public <T> T[] toArray(T[] objects) {
        int size=size();
        Class<T> clazz=(Class<T>)objects.getClass().getComponentType();
        T[] array=(T[])Array.newInstance(clazz,size);
        int i=0;
        for (Object element : this) {
            array[i]=clazz.cast(element);
        }
        return array;
	}
    @Deprecated
	public boolean add(U object) { 
        throw new UnsupportedOperationException(); 
    }    
	public boolean contains(Object object) {
		for (Collection<? extends U> col : collections) {
			if (col.contains(object)) {
				return true; 
			}
		}
		return false;
	}
	public boolean remove(Object object) {
        boolean changed=false;
		for (Collection<? extends U> col : collections) {
			if (col.remove(object)) {
                changed=true;
				//return true; 
			}
		}
        return changed;
		//return false;
	}
    @Deprecated
    public boolean addAll(Collection<? extends U> col) {
        boolean changed=false;
        for (U u : col) {
            changed=add(u) || changed;
        } 
        return changed;
	}
    public boolean containsAll(Collection<?> col) {
        for (Object o : col) {
            if (!contains(o)) {
                return false;
            }
        }
        return true; 
	}
    public boolean removeAll(Collection<?> col) {
        boolean changed=false;
        for (Object o : col) {
            changed=remove(o) || changed;
        } 
        return changed;
	}
    public boolean retainAll(Collection<?> col) {
        boolean changed=false;
        for (Iterator<U> it=iterator();it.hasNext();) {
            if (!col.contains(it.next())) {
                it.remove();
                changed=true;
            }
        }
        return changed; 
	}
	public Iterator<U> iterator() {
		//final Iterator firstIterator=first.iterator(),restIterator=rest.iterator();
		return new Iterator<U>() {
			Iterator<? extends Collection<? extends U>> collectionsIterator=collections.iterator();
			Iterator<? extends U> currentCollectionIterator=
				collectionsIterator.hasNext() ?
					collectionsIterator.next().iterator() :
					new Vector<U>(0).iterator();
			public boolean hasNext() {
				boolean collectionExhausted=false;
				do {
					collectionExhausted=true;
					if (currentCollectionIterator.hasNext()) {
						return true;
					} else if (collectionsIterator.hasNext()) {
						currentCollectionIterator=collectionsIterator.next().iterator();
						collectionExhausted=false;
					} 
				} while (!collectionExhausted);
				return false;
			}
			public U next() {
				boolean collectionExhausted=false;
				do {
					collectionExhausted=true;
					if (currentCollectionIterator.hasNext()) {
						return currentCollectionIterator.next();
					} else {
						currentCollectionIterator=collectionsIterator.next().iterator();
						collectionExhausted=false;
					}
				} while (!collectionExhausted);
				return null;
			}
			public void remove() {
				currentCollectionIterator.remove();
			}    
		};
	}
}
