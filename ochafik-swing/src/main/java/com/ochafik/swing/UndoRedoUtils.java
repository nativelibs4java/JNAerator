/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.swing;

import javax.swing.JComponent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public class UndoRedoUtils {

	public static void registerNewUndoManager(JTextComponent jtc) {
		registerNewUndoManager(jtc, jtc.getDocument());
	}
	public static void registerNewUndoManager(JComponent jtc, Document d) {
		UndoManager undoManager = new UndoManager();
		d.addUndoableEditListener(undoManager);
	    FormUtils.registerUndoRedoActions(jtc, FormUtils.createUndoAction(undoManager, "undo"), FormUtils.createRedoAction(undoManager, "redo"));
	}

}
