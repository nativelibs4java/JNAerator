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

import javax.swing.ComboBoxModel;

/**
 * Swing combo box model (for use by JList) that dynamically reflects the contents of a listenable list.<br/>
 * This model safely propagates events from the listenable list to any registered ListDataListener within the event dispatch thread, even if the events were received from an other thread.
 * @see javax.swing.event.ListDataListener
 * @see javax.swing.JList
 * @see com.ochafik.util.listenable.SwingCollectionListener 
 * @author Olivier Chafik
 * @param <T> Type of the elements of the list
 */
public class ListenableComboModel<T> extends ListenableListModel<T> implements ComboBoxModel {
	private static final long serialVersionUID = -8945907187555315122L;

	public ListenableComboModel(ListenableList<T> list) {
		super(list);
	}
	public ListenableComboModel() {
		super();
	}
	
	T selectedItem;
	
	@Override
	public Object getSelectedItem() {
		if (selectedItem != null && !getList().contains(selectedItem))
			selectedItem = null;
		return selectedItem;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object anItem) {
		if (getList().contains(anItem))
			selectedItem = (T)anItem;
		else
			selectedItem = null;
	}
}
