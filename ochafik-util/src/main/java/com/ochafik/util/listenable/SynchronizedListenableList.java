package com.ochafik.util.listenable;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

class SynchronizedListenableList<T> extends SynchronizedListenableCollection<T> implements ListenableList<T> {
	List<T> list;
	
	public SynchronizedListenableList(List<T> list) {
		super(list);
		this.list = list;
	}
	
	public void add(int index, T element) {
		synchronized (mutex) {
			list.add(index, element);
		}
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		synchronized (mutex) {
			return list.addAll(index, c);
		}
	}

	public T get(int index) {
		synchronized (mutex) {
			return list.get(index);
		}
	}

	public int indexOf(Object o) {
		synchronized (mutex) {
			return list.indexOf(o);
		}
	}

	public int lastIndexOf(Object o) {
		synchronized (mutex) {
			return list.lastIndexOf(o);
		}
	}

	public ListIterator<T> listIterator() {
		if (true)
			throw new UnsupportedOperationException();
		
		synchronized (mutex) {
			return list.listIterator();
		}
	}

	public ListIterator<T> listIterator(int index) {
		if (true)
			throw new UnsupportedOperationException();
		
		synchronized (mutex) {
			return list.listIterator(index);
		}
	}

	public T remove(int index) {
		synchronized (mutex) {
			return list.remove(index);
		}
	}

	public T set(int index, T element) {
		synchronized (mutex) {
			return list.set(index, element);
		}
	}

	public List<T> subList(int fromIndex, int toIndex) {
		if (true)
			throw new UnsupportedOperationException();
		
		synchronized (mutex) {
			return list.subList(fromIndex, toIndex);
		}
	}

}
