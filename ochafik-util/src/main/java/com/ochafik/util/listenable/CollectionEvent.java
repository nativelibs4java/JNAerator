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

import java.util.Collection;
import java.util.EventObject;

/**
 * <p>
 * Event that represents a modification of a listenable collection.<br/>
 * An event can be of three types : addition, removal and update.<br/>
 * Multiple elements can be packed in the same event instance.<br/>
 * For instance, if a listenable collection is cleared then it may issue no more than a single REMOVED event with all of its elements as argument.
 * </p>
 * <br/>
 * <p>
 * The UPDATED event type is here to notify the listeners that something changed about the elements referred to by the event.<br/>
 * It is typically triggered when one adds an existing element to a ListenableSet.
 * </p>  
 * @author Olivier Chafik
 * @param <T> Type of the elements of the collection from which the event originated
 */
public class CollectionEvent<T> extends EventObject {
	
	private static final long serialVersionUID = -5266364113759541622L;

	/// Type of the event. 
	public enum EventType {
		ADDED, 
		REMOVED,
		UPDATED
	}
	
	/// Type of the event
	protected final EventType type;
	
	/// Elements affected by the event
	protected final Collection<T> elements;
	
	/// Index of the first affected element in the source, or -1 if not applicable
	protected final int firstIndex;
	
	/// Index of the last affected element in the source, or -1 if not applicable
	protected final int lastIndex;
	
	@SuppressWarnings("unchecked")
	/**
	 * Get the source of the event
	 * @return listenable collection where the event originated from
	 */
	public ListenableCollection<T> getSource() {
		return (ListenableCollection<T>)super.getSource();
	}
	
	/**
	 * Get the event type
	 * @return type of the event
	 */
	public EventType getType() {
		return type;
	}
	
	/**
	 * Get the elements affected by the event.
	 * @return the elements affected by the event.
	 */
	public Collection<T> getElements() {
		return elements;
	}
	
	/**
	 * Get the index of the first affected element in the source, or -1 if not applicable
	 * @return Index of the first affected element in the source, or -1 if not applicable
	 */
	public int getFirstIndex() {
		return firstIndex;
	}
	
	/**
	 * Get the index of the last affected element in the source, or -1 if not applicable
	 * @return Index of the last affected element in the source, or -1 if not applicable
	 */
	public int getLastIndex() {
		return lastIndex;
	}
	
	public CollectionEvent(ListenableCollection<T> source, Collection<T> elements, EventType type) {
		this(source, elements, type, -1, -1);
	}
	
	public CollectionEvent(ListenableCollection<T> source, Collection<T> elements, EventType type, int firstIndex, int lastIndex) {
		super(source);
		this.elements = elements;
		this.type = type;
		this.firstIndex = firstIndex;
		this.lastIndex = lastIndex;
	}
	
}
