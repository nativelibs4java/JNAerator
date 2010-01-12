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

import java.util.Set;

/**
 * Interface for sets that support modification listeners.<br/>
 * Listenable sets are required to trigger UPDATED events whenever one attempts to add an element that is already present in the set. 
 * @author Olivier Chafik
 * @param <T> Type of the elements of the set
 */
public interface ListenableSet<T> extends ListenableCollection<T>, Set<T> {
}
