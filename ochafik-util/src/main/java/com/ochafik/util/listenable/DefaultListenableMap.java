package com.ochafik.util.listenable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;


/**
 * Default implementation of the ListenableMao interface.<br/>
 * This class follows both the decorator and proxy patterns : it wraps an existing java.util.Map and adds the listenable feature to it.<br/>
 * @author Olivier Chafik
 * @param <K> Type of the keys of the map
 * @param <V> Type of the values of the map
 */
public class DefaultListenableMap<K,V> implements ListenableMap<K,V> {
	
	Map<K,V> map;
	
 	DefaultListenableSet<K> keySet;
 	DefaultListenableSet<Map.Entry<K,V>> entrySet;
 	
 	ListenableSupport<V> valuesSupport;
 	ListenableCollection<V> values;
 	
 	public DefaultListenableMap(Map<K,V> map) {
		this.map = map;
	}
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	public int size() {
		return map.size();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
	
	public void clear() {
		Collection<Map.Entry<K,V>> copy = new ArrayList<Map.Entry<K,V>>(map.entrySet());
		map.clear();
		propagateRemoved(copy);
	}
	@Override
	public boolean equals(Object obj) {
		return map.equals(obj);
	}
	public V get(Object key) {
		return map.get(key);
	}
	@Override
	public int hashCode() {
		return map.hashCode();
	}
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public V put(K key, V value) {
		V v = map.put(key,value);
		if (v !=null) {
			propagateUpdated(key, v, value);
		} else {
			propagateAdded(key, value);
		}
		return v;
	} 
	public void putAll(Map<? extends K, ? extends V> t) {
		for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
			put(e.getKey(),e.getValue());
		}
	}
	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		V v = map.remove(key);
		if (v !=null) {
			propagateRemoved(Collections.singletonMap((K)key, (V)v).entrySet());
		}
		return v;
	}
	
	void propagateAdded(K key, V value) {
		if (keySet != null)
			keySet.collectionSupport.fireAdded(keySet, Collections.singleton(key));
		
		if (entrySet != null) {
			entrySet.collectionSupport.fireAdded(entrySet, Collections.singleton((Map.Entry<K,V>)new UnmodifiableEntry<K,V>(key, value)));
		}
			
		if (values != null) {
			valuesSupport.fireAdded(values, Collections.singleton(value));
		}
	}
	void propagateRemoved(Collection<Map.Entry<K, V>> entries) {
		if (keySet != null)
			keySet.collectionSupport.fireRemoved(keySet, new AdaptedCollection<Map.Entry<K,V>, K>(entries, entryToKeyAdapter));
		
		if (entrySet != null) {
			entrySet.collectionSupport.fireRemoved(entrySet, entries);
		}
			
		if (values != null) {
			valuesSupport.fireRemoved(values, new AdaptedCollection<Map.Entry<K,V>, V>(entries, entryToValueAdapter));
		}
	}
	
	void propagateUpdated(K key, V oldValue, V newValue) {
		if (keySet != null)
			keySet.collectionSupport.fireUpdated(keySet, Collections.singleton(key));
		
		boolean different = !oldValue.equals(newValue);
		
		if (entrySet != null) {
			Collection<Map.Entry<K, V>> newEntries = Collections.singleton((Map.Entry<K, V>)new UnmodifiableEntry<K,V>(key, newValue));
			if (different) {
				Collection<Map.Entry<K, V>> oldEntries = Collections.singleton((Map.Entry<K, V>)new UnmodifiableEntry<K,V>(key, oldValue));
				entrySet.collectionSupport.fireRemoved(entrySet, oldEntries);
				entrySet.collectionSupport.fireAdded(entrySet, newEntries);
			} else {
				entrySet.collectionSupport.fireUpdated(entrySet, newEntries);
			}
		}
			
		if (values != null) {
			if (different) {
				valuesSupport.fireRemoved(values, Collections.singleton(oldValue));
				valuesSupport.fireAdded(values, Collections.singleton(newValue));
			} else {
				valuesSupport.fireUpdated(values, Collections.singleton(newValue));
			}
		}
	}
	
	public ListenableSet<K> keySet() {
		if (keySet == null) {
			keySet = new DefaultListenableSet<K>(map.keySet()) {
				@SuppressWarnings("unchecked")
				@Override
				public boolean remove(Object o) {
					V value = map.remove(o);
					if (value != null) {
						propagateRemoved(Collections.singletonMap((K)o, value).entrySet());
						return true;
					}
					return false;
				}
				@Override
				public void clear() {
					DefaultListenableMap.this.clear();
				}
				@Override
				public boolean removeAll(Collection<?> c) {
					if (!collectionSupport.hasListeners())
						return collection.removeAll(c);
					
					boolean changed = false;
					for (Object o : c)
						changed = remove(o) || changed;
					
					return changed;
				}
				@Override
				public boolean add(K o) {
					throw new UnsupportedOperationException();
				}
				@Override
				public boolean addAll(Collection<? extends K> c) {
					throw new UnsupportedOperationException();
				}
				@Override
				public boolean retainAll(Collection<?> c) {
					if (!collectionSupport.hasListeners())
						return collection.retainAll(c);
					
					Collection<Map.Entry<K,V>> removedElements = new ArrayList<Map.Entry<K,V>>(c.size());
					for (Iterator<Map.Entry<K, V>> it = map.entrySet().iterator(); it.hasNext();) {
						Map.Entry<K, V> e = it.next();
						if (!c.contains(e.getKey())) {
							it.remove();
							removedElements.add(e);
						}
					}
					propagateRemoved(removedElements);
					return !removedElements.isEmpty();
				}
				@Override
				public Iterator<K> iterator() {
					return new ListenableIterator(this,collection.iterator()) {
						@Override
						public void remove() {
							// cannot use map.remove(lastValue), as the iterator would throw a concurrent modification exception :-(
							V assoc = get(lastValue);
							iterator.remove();
							propagateRemoved(Collections.singletonMap(lastValue, assoc).entrySet());
						}
					};
				}
			};
		}
 		
 		return keySet;
	}
	
	protected Adapter<Map.Entry<K, V>, K> entryToKeyAdapter = new Adapter<Map.Entry<K, V>, K>() { public K adapt(Map.Entry<K,V> value) { 
		return value.getKey(); 
	}};
	protected Adapter<Map.Entry<K, V>, V> entryToValueAdapter = new Adapter<Map.Entry<K, V>, V>() { public V adapt(Map.Entry<K,V> value) { 
		return value.getValue(); 
	}};
	
	public ListenableSet<Map.Entry<K,V>> entrySet() {
		if (entrySet == null) {
			entrySet = new DefaultListenableSet<Map.Entry<K, V>>(map.entrySet()) {
				@SuppressWarnings("unchecked")
				@Override
				public boolean remove(Object o) {
					Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
					V value = map.remove(entry.getKey());
					if (value != null) {
						propagateRemoved(Collections.singleton(entry));
						return true;
					}
					return false;
				}
				@Override
				public void clear() {
					DefaultListenableMap.this.clear();
				}
				@Override
				public boolean removeAll(Collection<?> c) {
					if (!collectionSupport.hasListeners())
						return collection.removeAll(c);
					
					boolean changed = false;
					for (Object o : c)
						changed = remove(o) || changed;
					
					return changed;
				}
				@Override
				public boolean add(Map.Entry<K, V> o) {
					throw new UnsupportedOperationException();
				}
				@Override
				public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
					throw new UnsupportedOperationException();
				}
				@Override
				public boolean retainAll(Collection<?> c) {
					if (!collectionSupport.hasListeners())
						return collection.retainAll(c);
					
					Collection<Map.Entry<K,V>> removedElements = new ArrayList<Map.Entry<K,V>>(c.size());
					for (Iterator<Map.Entry<K, V>> it = map.entrySet().iterator(); it.hasNext();) {
						Map.Entry<K, V> e = it.next();
						if (!c.contains(e)) {
							it.remove();
							removedElements.add(e);
						}
					}
					propagateRemoved(removedElements);
					return !removedElements.isEmpty();
				}
				@Override
				public Iterator<Map.Entry<K, V>> iterator() {
					return new ListenableIterator(this,collection.iterator()) {
						@Override
						public void remove() {
							iterator.remove();
							propagateRemoved(Collections.singleton(lastValue));
						}
					};
				}
			};
		}
 		
 		return entrySet;
	}
	
	public ListenableCollection<V> values() {
		if (values == null) {
			valuesSupport = new ListenableSupport<V>();
			values = ListenableCollections.unmodifiableCollection(new DefaultListenableCollection<V>(map.values(), valuesSupport));
 		}
 		
 		return values;
	}
	
	
}
