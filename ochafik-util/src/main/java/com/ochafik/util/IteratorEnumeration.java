package com.ochafik.util;
import java.util.Enumeration;
import java.util.Iterator;
@SuppressWarnings("unchecked")
public class IteratorEnumeration implements Enumeration {
	Iterator it;
	public IteratorEnumeration(Iterator it) {
		this.it=it;
	}
	public boolean hasMoreElements() {
		return it.hasNext();
	}
	public Object nextElement() {
		return it.next();
	}
}
