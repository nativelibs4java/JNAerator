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

	  http://jalico.googlecode.com/
*/
package com.ochafik.util.listenable;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * ListenableCollection&lt;V&gt; that wraps a Collection&lt;U&gt;, using adapters from U to V (and optionally backwards, for support of the add(V) method).<br/>
 * If the wrapped collection implements ListenableCollection, all its events will be adapted and propagated to listeners of the AdaptedCollection.<br/> 
 * This is useful to convert a collection to another type<br/>
 * The resulting wrapped collection is listenable, and listens to modifications of its wrapped collection if it is an instance of ListenableCollection.
 * @author Olivier Chafik
 * @param <U> type of the elements of the wrapped collection
 * @param <V> type of the elements of the adapted collection
 */
public class AdaptedCollection<U, V> extends AbstractCollection<V> implements ListenableCollection<V> {
	protected final Collection<U> collection;
	protected final Adapter<U,V> forwardAdapter;
	protected final Adapter<V,U> backwardAdapter;
	protected ListenableSupport<V> collectionSupport;
	protected boolean currentlyCausingChange = false;
	
	public AdaptedCollection(Collection<U> collection, Adapter<U, V> forwardAdapter) {
		this(collection, forwardAdapter, null);
	}
	
	public AdaptedCollection(Collection<U> collection, Adapter<U, V> forwardAdapter, Adapter<V, U> backwardAdapter) {
		super();
		if (forwardAdapter == null)
			throw new NullPointerException("AdaptedCollection needs a non-null forward adapter");
		
		this.collection = collection;
		this.forwardAdapter = forwardAdapter;
		this.backwardAdapter = backwardAdapter;
	}
	
	public Adapter<U, V> getForwardAdapter() {
		return forwardAdapter;
	}
	
	public Adapter<V, U> getBackwardAdapter() {
		return backwardAdapter;
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
		if (collectionSupport != null && collectionSupport.hasListeners()) {
			try {
				currentlyCausingChange = true;
				Collection<V> removedElements = new ArrayList<V>(AdaptedCollection.this);
				collection.clear();
				collectionSupport.fireRemoved(AdaptedCollection.this, removedElements, 0, removedElements.size() - 1);
			} finally {
				currentlyCausingChange = false;
			}
		} else {
			collection.clear();
		}
	}
	
	@Override
	public boolean add(V value) {
		if (backwardAdapter == null)
			throw new UnsupportedOperationException("No backward adapter in this AdapterCollection");
		
		try {
			currentlyCausingChange = true;
			if (collection.add(backwardAdapter.adapt(value))) {
				if (collectionSupport != null && collectionSupport.hasListeners())
					collectionSupport.fireAdded(this, Collections.singleton(value));
				
				return true;
			}
			return false;
		} finally {
			currentlyCausingChange = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object value) {
		if (backwardAdapter == null) {
			// Give index of removed element in collection event when it does not cost more 
			// (simplifies subclassing as List, as for the remove method there is only the need to reimplement removeWithoutBackWardAdapter) 
			int i = 0;
			for (Iterator<U> it = collection.iterator(); it.hasNext();) {
				if (forwardAdapter.adapt(it.next()).equals(value)) {
					try {
						currentlyCausingChange = true;
						it.remove();
						if (collectionSupport != null && collectionSupport.hasListeners())
							collectionSupport.fireRemoved(this, (Collection<V>)Collections.singleton(value), i, i);
						
						return true;
					} finally {
						currentlyCausingChange = false;
					}
				}
				i++;
			}
			return false;
		} else {
			return removeWithoutBackWardAdapter(value);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected boolean removeWithoutBackWardAdapter(Object value) {
		try {
			currentlyCausingChange = true;
			if (collection.remove(backwardAdapter.adapt((V)value))) {
				if (collectionSupport != null && collectionSupport.hasListeners())
					collectionSupport.fireRemoved(this, (Collection<V>)Collections.singleton(value));
				
				return true;
			}
			return false;
		} finally {
			currentlyCausingChange = false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object value) {
		if (backwardAdapter != null) {
			return collection.contains(backwardAdapter.adapt((V)value));
		} else {
			for (U element : collection) {
				if (forwardAdapter.adapt(element).equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected class IteratorAdapter implements Iterator<V> {
		protected Iterator<U> iterator;
		protected V lastValue;
		public IteratorAdapter(Iterator<U> iterator) {
			this.iterator=iterator;
		}
		public boolean hasNext() {
			return iterator.hasNext();
		}
		public V next() {
			return lastValue = forwardAdapter.adapt(iterator.next());
		}
		public void remove() {
			try {
				currentlyCausingChange = true;
				iterator.remove();
				
				if (collectionSupport != null && collectionSupport.hasListeners())
					collectionSupport.fireRemoved(AdaptedCollection.this, Collections.singleton(lastValue));
			} finally {
				currentlyCausingChange = false;
			}
			lastValue = null;	
		}
	}
	
	public void addCollectionListener(CollectionListener<V> l) {
		if (collectionSupport == null) {
			collectionSupport = new ListenableSupport<V>();
			if (collection instanceof ListenableCollection<?>) {
				((ListenableCollection<U>)collection).addCollectionListener(new CollectionListener<U>() {
					public void collectionChanged(CollectionEvent<U> e) {
						// Do not propagate the event if we triggered it
						if (currentlyCausingChange)
							return;
						
						// Only propagate if someone is listening (CollectionSupport already tries not to fire anything when there is no listener, but here we are trying to avoid to create the wrapped elements collection)
						if (collectionSupport == null || !collectionSupport.hasListeners())
							return;
						
						// Adapt the collection of changed / added / removed elements in the event
						collectionSupport.fireEvent(
							AdaptedCollection.this, 
							new AdaptedCollection<U, V>(e.getElements(), AdaptedCollection.this.forwardAdapter, AdaptedCollection.this.backwardAdapter), 
							e.getType(), 
							e.getFirstIndex(), 
							e.getLastIndex());
					}
				});
			}
		}
				
		collectionSupport.addCollectionListener(l);
	}
	
	public void removeCollectionListener(CollectionListener<V> l) {
		if (collectionSupport == null)
			return;
		
		collectionSupport.removeCollectionListener(l);
	}
}
