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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import com.ochafik.beans.BeansController;


public class FormUtils {
	
	@SuppressWarnings("serial")
	public static Action createUndoAction(final UndoManager undoManager, String name) {
		return new AbstractAction(name) {
            public void actionPerformed(ActionEvent arg0) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        };
	}
	@SuppressWarnings("serial")
	public static Action createRedoAction(final UndoManager undoManager, String name) {
		return new AbstractAction(name) {
            public void actionPerformed(ActionEvent arg0) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        };
	}
	public static void registerUndoRedoActions(JComponent jtc, Action undoAction, Action redoAction) {
        InputMap inputMap = jtc.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke("pressed UNDO"), "undo");
        inputMap.put(KeyStroke.getKeyStroke("ctrl pressed Z"), "undo");
        inputMap.put(KeyStroke.getKeyStroke("meta pressed Z"), "undo");
        
        inputMap.put(KeyStroke.getKeyStroke("pressed REDO"), "redo");
        inputMap.put(KeyStroke.getKeyStroke("ctrl pressed Y"), "redo");
        inputMap.put(KeyStroke.getKeyStroke("meta pressed Y"), "redo");
        
        ActionMap actionMap = jtc.getActionMap();
        actionMap.put("undo", undoAction);
        actionMap.put("redo", redoAction);
	}
	
    public final static void addUndoRedoSupport(final JTextComponent jtc) {
    	jtc.addPropertyChangeListener("document", new PropertyChangeListener() {
    		public void propertyChange(PropertyChangeEvent evt) {
    			UndoRedoUtils.registerNewUndoManager(jtc);
    		}
    		
    	});
    	UndoRedoUtils.registerNewUndoManager(jtc);
    }
    public final static JPanel makeEntriesPanel(BeansController<?> beansController,int widthMin,FormElement[] formElements)  {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty=1;
        
        JPanel panel=new JPanel(gbl);//new GridLayout(captionsAndProperties.length,2));
        int space=5,pad=1;
        Insets labelsInsets=new Insets(pad,space,pad,space);
        Insets editorsInsets=new Insets(pad,pad,pad,pad);
        for (int i=0;i<formElements.length;i++) {
            gbc.gridy=i;
            
            FormElement formElement=formElements[i];
            
            JLabel label=new JLabel(formElement.name,JLabel.RIGHT);
            gbc.fill=GridBagConstraints.NONE;
            gbc.anchor=GridBagConstraints.NORTHEAST;
            gbc.gridx=0;
            gbc.weightx=0;
            gbc.insets=labelsInsets;
            gbl.setConstraints(label, gbc);
            panel.add(label);
            
            Component editor=
                formElement.editorComponent==null ?
                    beansController.createScrollableViewComponent(
                        formElement.propertyName, 
                        null, 
                        formElement.title, 
                        formElement.toolTip, 
                        formElement.largeComponent
                    ) :
                        formElement.editorComponent;
            gbc.fill=GridBagConstraints.HORIZONTAL;
            gbc.gridx=1;
            gbc.weightx=6;
            gbc.insets=editorsInsets;
            gbl.setConstraints(editor, gbc);
            panel.add(editor);
        }
        JPanel ret=new JPanel(new BorderLayout());
        ret.add("Center",panel);
        ret.add("South",Box.createHorizontalStrut(widthMin));
        return ret;
    }
}
