package com.ochafik.util.listenable;

public interface Filter<V> {
	public boolean accept(V value);
}
