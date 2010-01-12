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

import java.util.Map;

class UnmodifiableEntry<K, V> implements Map.Entry<K, V> {
	K key;
	V value;
	
	public UnmodifiableEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}
	public V setValue(V value) {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Map.Entry))
			return false;
			
		Map.Entry e = (Map.Entry)obj;
		if (key == null) {
			if (e.getKey() != null)
				return false;
		} else if (!key.equals(e.getKey()))
			return false;
		
		if (value == null) {
			if (e.getValue() != null)
				return false;
		}
		return value.equals(e.getValue());
	}
	
	@Override
	public String toString() {
		return "<"+getKey()+", "+getValue()+">";
	}	
}