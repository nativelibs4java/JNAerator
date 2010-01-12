package com.ochafik.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class SimpleDocumentAdapter implements DocumentListener {
	public abstract void updated(DocumentEvent e);
	public void changedUpdate(DocumentEvent e) {
		updated(e);	
	}

	public void insertUpdate(DocumentEvent e) {
		updated(e);				
	}

	public void removeUpdate(DocumentEvent e) {
		updated(e);	
	}
}
