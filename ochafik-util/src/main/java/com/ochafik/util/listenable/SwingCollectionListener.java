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

import javax.swing.SwingUtilities;

/**
 * Collection listener proxy that forwards in the event dispatch thread all the events it receives.  
 * @author Olivier Chafik
 * @param <T> type of the elements of the collections that are listened to by this listener
 */
public class SwingCollectionListener<T> implements CollectionListener<T> {
	final CollectionListener<T> listener;
	
	public SwingCollectionListener(CollectionListener<T> listener) {
		this.listener = listener;
	}

	public void collectionChanged(final CollectionEvent<T> e) {
		if (SwingUtilities.isEventDispatchThread())
			listener.collectionChanged(e);
		else
			SwingUtilities.invokeLater(new Runnable() { public void run() {
				listener.collectionChanged(e);
			}});
		
	}	
}
