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
package com.ochafik.beans;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.ochafik.swing.FormUtils;


@SuppressWarnings("unchecked")
public class BeansController<M> {
	M model;
	Class<M> modelClass;
	
	public BeansController(Class<M> modelClass) {
		this.modelClass=modelClass;
	}
	
	Map<String,java.util.List<JComponent>> viewsByPropertyName=new HashMap<String,java.util.List<JComponent>>();
	Map<String,Class> propertiesTypes=new HashMap<String,Class>();
	Map<String,Object> oldValues=new HashMap<String,Object>();
	Map<String,Method> getterMethods=new HashMap<String,Method>();
	Map<String,Method> setterMethods=new HashMap<String,Method>();
	public PropertyChangeSupport getPropertyChangeSupport() { 
		return propertyChangeSupport;
	}
	PropertyChangeSupport propertyChangeSupport=new PropertyChangeSupport(this);
	static final Class getterArgs[]=new Class[0];
		
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName,PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName,listener);
	}
	public JComponent createScrollableViewComponent(
			final String propertyName,
			String caption, 
			String title, 
			String tooltip, 
			boolean largeComponent
	)  {//, IllegalAccessException {
		JComponent c=createViewComponent(propertyName,caption,largeComponent);
		if (c==null) return c;
		if (title!=null) c.setBorder(BorderFactory.createTitledBorder(title));
		if (c instanceof JTextArea) {
			JTextArea ta=(JTextArea)c;
            ta.setLineWrap(true);
			ta.setWrapStyleWord(true);
			JScrollPane jsp=new JScrollPane(ta);
			c=jsp;
		}
		if (title!=null) c.setBorder(BorderFactory.createTitledBorder(title));
		if (tooltip!=null) c.setToolTipText(tooltip);
		return c;
	}
	public static final boolean booleanTrue=true;//, booleanFalse=false;
	//private static Object booleanTrueObject,booleanFalseObject;
	//public static Class BooleanPrimitiveClass;
	/*static {
		try {
			Class clazz=BeansController.class;
			BooleanPrimitiveClass=clazz.getField("booleanTrue").getType();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/	
	public JComponent createViewComponent(final String propertyName,String caption, boolean largeComponent) {//throws NoSuchMethodException {//, IllegalAccessException {
        try {
    		Class propertyType=getPropertyType(propertyName);
    		JComponent jc;
    		if (String.class.isAssignableFrom(propertyType)) {
    			jc=largeComponent ? new JTextArea() : new JTextField();
                final JTextComponent jtc=(JTextComponent )jc;
                FormUtils.addUndoRedoSupport(jtc);
                jtc.addFocusListener(new FocusAdapter() { 
                    @Override
                    public void focusGained(FocusEvent arg0) {
                        jtc.selectAll();
                    }
                    @Override
                    public void focusLost(FocusEvent arg0) {
                        // TODO Auto-generated method stub
                        jtc.setSelectionStart(0);
                        jtc.setSelectionEnd(0);
                    }
                });
                
    		} else if (isBoolean(propertyType)) {
    			jc=caption==null ? new JCheckBox() : new JCheckBox(caption);
    		} else if (isInteger(propertyType)) {
                jc=new JTextField();
            } else {
    			System.err.println("IMPLEMENTME! Don't know how to create a view component for model class "+propertyType.getName());
    			jc=null;
    		}
    		attachViewComponent(jc,propertyName);
    		return jc;
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("No such field in "+modelClass.getName()+" : "+propertyName);
        }
	}
    public void attachViewComponent(JComponent jc, final String propertyName) throws NoSuchMethodException {//, IllegalAccessException {
		Class propertyType=getPropertyType(propertyName);
		if (String.class.isAssignableFrom(propertyType)) {
			final JTextComponent c=(JTextComponent)jc;
            c.getDocument().addDocumentListener( new DocumentListener() {
                public void changedUpdate(DocumentEvent e) { fireViewChange(c,propertyName,c.getText()); }
				public void insertUpdate(DocumentEvent e) { fireViewChange(c,propertyName,c.getText()); }
				public void removeUpdate(DocumentEvent e) { fireViewChange(c,propertyName,c.getText()); }
			});
		} else if (isBoolean(propertyType)) {
			final AbstractButton c=(AbstractButton)jc;
			c.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent evt) { 
				fireViewChange(c,propertyName,new Boolean(c.isSelected())); 
			}});
		} else if (isInteger(propertyType)){
            final JTextComponent c=(JTextComponent)jc;
            c.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) { fireViewChange(c,propertyName,c.getText()); }
                public void insertUpdate(DocumentEvent e) { fireViewChange(c,propertyName,c.getText()); }
                public void removeUpdate(DocumentEvent e) { fireViewChange(c,propertyName,c.getText()); }
            });
            
        } else {
			System.err.println("IMPLEMENTME! Don't know how to create a view component for model class "+propertyType.getName());
		}
		if (jc!=null) {
			java.util.List<JComponent> views=viewsByPropertyName.get(propertyName);
			if (views==null) {
				 views=new ArrayList<JComponent>();
				 viewsByPropertyName.put(propertyName,views);
			}
			views.add(jc);
		}
		jc.setEnabled(model!=null);
	}
	Method getGetterMethod(String propertyName) throws NoSuchMethodException {//, IllegalAccessException {
		Method getterMethod=getterMethods.get(propertyName);
		if (getterMethod==null) {
			try {
				getterMethod=modelClass.getMethod(getGetterMethodName(propertyName),getterArgs);
			} catch (NoSuchMethodException ex) {
				getterMethod=modelClass.getMethod(getIsGetterMethodName(propertyName),getterArgs);
			}
			getterMethods.put(propertyName,getterMethod);
		}
		return getterMethod;
	}
	Class getPropertyType(String propertyName) throws NoSuchMethodException { //, IllegalAccessException {
		Class propertyType=propertiesTypes.get(propertyName);
		if (propertyType==null) {
			propertyType=getGetterMethod(propertyName).getReturnType();
			propertiesTypes.put(propertyName,propertyType);
		}
		return propertyType;
	}
	Method getSetterMethod(String propertyName) throws NoSuchMethodException { //, IllegalAccessException {
		Method setterMethod=setterMethods.get(propertyName);
		if (setterMethod==null) {
			Class propertyType=getPropertyType(propertyName);
			Class setterArgs[]=new Class[] { propertyType };
			setterMethod=modelClass.getMethod(getSetterMethodName(propertyName),setterArgs);
			setterMethods.put(propertyName,setterMethod);
		}
		return setterMethod;
	}
	boolean updatingModel=false;
    public M getModel() { return model; }
	public void setModel(M model) {
		this.model=model;
		if (model!=null) {
			if (!modelClass.isAssignableFrom(model.getClass())) throw new ClassCastException(model.getClass().getName()+" not a subclass of "+modelClass.getName());
			
		}
		modelUpdated();
	}
    boolean firingPropertyChange=false;
    Set<String> propertiesBeingFired=new TreeSet<String>();
	public void fireViewChange(Component eventSource,String propertyName, Object newValue) {
		//System.out.println("FireViewChange : propertyName="+propertyName+", firingPropertyChange="+firingPropertyChange+", model="+model);
        
        /// Do not fire change events if somebody is just setting the model
        if (updatingModel) return;
        
        if (propertiesBeingFired.contains(propertyName)) {
            return;
        } else {
            propertiesBeingFired.add(propertyName);
        }
		if (model!=null) {
			try {
				Class propertyType=getPropertyType(propertyName);
				Object oldValue=oldValues.get(propertyName);
				boolean validValue=true;
                if (String.class.isAssignableFrom(propertyType)) {
                    getSetterMethod(propertyName).invoke(model,newValue);
                    oldValues.put(propertyName,newValue);
                    
                    for (JComponent view : viewsByPropertyName.get(propertyName)) {
                        if (view!=eventSource) {
                            JTextComponent tc=(JTextComponent)view;
                            //System.out.println("propertyName="+propertyName+" set at "+newValue
                            tc.setText((String)newValue);
                        }
                    }
				} else if (isBoolean(propertyType)) {
                    getSetterMethod(propertyName).invoke(model,newValue);
                    oldValues.put(propertyName,newValue);
                    
                    for (JComponent view : viewsByPropertyName.get(propertyName)) {
                        if (view!=eventSource) {
                            AbstractButton cb=(AbstractButton)view;
                            cb.setSelected(((Boolean)newValue).booleanValue());
                        }
                    }
				} else if (isInteger(propertyType)) {
                    try {
                        int intValue=Integer.parseInt(((String)newValue).trim());
                        getSetterMethod(propertyName).invoke(model,new Integer(intValue));
                        oldValues.put(propertyName,newValue);
                        
                        for (JComponent view : viewsByPropertyName.get(propertyName)) {
                            view.setFont(view.getFont().deriveFont(Font.PLAIN));
                            view.setForeground(Color.black);
                            if (view!=eventSource) {
                                JTextComponent tc=(JTextComponent)view;
                                tc.setText(intValue+"");
                            }
    					}
                    } catch (NumberFormatException ex) {
                        validValue=false;
                        if (eventSource instanceof Component) {
                            Component eventSourceComponent=(Component)eventSource;
                            eventSourceComponent.setFont(eventSourceComponent.getFont().deriveFont(Font.BOLD|Font.ITALIC));
                            eventSourceComponent.setForeground(Color.red);
                        }
                    }
				}
				if (propertyChangeSupport!=null&&validValue) {
                    propertyChangeSupport.firePropertyChange(
        				propertyName,
    					oldValue,
    					newValue
                    );
                }
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} 
		//firingPropertyChange=false;
        propertiesBeingFired.remove(propertyName);
	}
	private static boolean isBoolean(Class propertyType) {
        return Boolean.class.isAssignableFrom(propertyType) || propertyType.getName().equals("boolean");
	}	
	public void modelUpdated() {
        if (updatingModel) return;
        updatingModel=true;
        boolean nullModel=model==null;
		//System.out.println("Model updated "+nullModel);
		for (String propertyName : propertiesTypes.keySet()) {
			try {
				Class propertyType=getPropertyType(propertyName);
				
				Object value=model==null ?
					null :
					getGetterMethod(propertyName).invoke(model);
				//System.out.println("\tValue("+propertyName+") = "+value);
				for (JComponent view : viewsByPropertyName.get(propertyName)) {
					if (String.class.isAssignableFrom(propertyType)) {
						JTextComponent tc=(JTextComponent)view;
						String svalue=(String)value;
						tc.setText(svalue==null ? "" : svalue);
						tc.setEnabled(!nullModel);
					} else if (isBoolean(propertyType)) {
						//TODO
						AbstractButton cb=(AbstractButton)view;
						Boolean bvalue=(Boolean)value;
						cb.setEnabled(!nullModel);
						cb.setSelected(bvalue!=null && bvalue.booleanValue());
					} else if (isInteger(propertyType)) {
                        JTextComponent tc=(JTextComponent)view;
                        Integer ivalue=(Integer)value;
                        tc.setText(ivalue==null ? "" : ivalue.toString());
                        tc.setEnabled(!nullModel);
                    }
				}
			} catch (Exception ex) {
                System.err.println("Error while updating views for property '"+propertyName+"' of model "+modelClass.getName());
				ex.printStackTrace();
			}
		}
        updatingModel=false;
	}
    private static final boolean isInteger(Class c) {
        return Integer.class.isAssignableFrom(c);
    }
	final static String getGetterMethodName(String field) {
		return "get"+capitalizeFirstLetter(field);
	}
	final static String getIsGetterMethodName(String field) {
		return "is"+capitalizeFirstLetter(field);
	}
	final static String getSetterMethodName(String field) {
		return "set"+capitalizeFirstLetter(field);
	}
	final static String capitalizeFirstLetter(String s) {
		int sLength=s.length();
		if (sLength<=1) return s.toUpperCase();
		else {
			return s.substring(0,1).toUpperCase()+s.substring(1);
		}
	}	
}
