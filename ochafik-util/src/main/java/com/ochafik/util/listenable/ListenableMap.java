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

/**
 * Interface for maps which key set, entry set and values collection support modification listeners.<br/>
 * @author Olivier Chafik
 * @param <K> Type of the keys of the map
 * @param <V> Type of the values of the map
 */
public interface ListenableMap<K,V> extends Map<K,V> {
	public ListenableSet<K> keySet();
	public ListenableSet<Map.Entry<K,V>> entrySet();
	public ListenableCollection<V> values();
}
