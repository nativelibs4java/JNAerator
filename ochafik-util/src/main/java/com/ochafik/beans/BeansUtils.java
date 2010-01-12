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
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.ochafik.util.string.StringUtils;
public class BeansUtils {
	public static final void markTransientProperty(String name,Class<?> classe) throws IntrospectionException {	
		BeanInfo info = Introspector.getBeanInfo(classe);
		PropertyDescriptor[] propertyDescriptors =
					     info.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; ++i) {
		    PropertyDescriptor pd = propertyDescriptors[i];
		    if (pd.getName().equals(name)) {
			pd.setValue("transient", Boolean.TRUE);
		    }
		}
	}

	public static void addPropertyChangeListener(Object bean, final String propertyName, final PropertyChangeListener listener) {
		if (bean == null)
			throw new NullPointerException("Null bean !");
		
		try {
			bean.getClass().getMethod("addPropertyChangeListener", String.class, PropertyChangeListener.class).invoke(bean, propertyName, listener);
		} catch (Exception ex) {
			try {
				bean.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class).invoke(bean, new PropertyChangeListener() {
					
					public void propertyChange(PropertyChangeEvent evt) {
						if (propertyName.equals(evt.getPropertyName()))
							listener.propertyChange(evt);
					}
				});
			} catch (Exception ex2) {
				throw new RuntimeException(ex);
			}
		}
	}
	public static void removePropertyChangeListener(Object bean, String propertyName, PropertyChangeListener listener) {
		if (bean == null)
			throw new NullPointerException("Null bean !");
		
		try {
			bean.getClass().getMethod("removePropertyChangeListener", PropertyChangeListener.class).invoke(bean, listener);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static Object get(Object bean, String propertyName) {
		return get(bean, propertyName, Object.class);
	}
		
	public static <T> T get(Object bean, String propertyName, Class<T> propertyClass) {
		if (bean == null)
			throw new NullPointerException("Null bean !");
		
		try {
			String cap  = StringUtils.capitalize(propertyName);
			Method m;
			if (propertyClass == Boolean.class) {
				try {
					m = bean.getClass().getMethod("is" + cap);
				} catch (NoSuchMethodException ex) {
					m = bean.getClass().getMethod("get" + cap);
				}
			} else {
				m = bean.getClass().getMethod("get" + cap);
			}
			return propertyClass.cast(m.invoke(bean));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static <T> void set(Object bean, String propertyName, Class<T> propertyClass, T value) {
		if (bean == null)
			throw new NullPointerException("Null bean !");
		
		try {
			bean.getClass().getMethod("set" + StringUtils.capitalize(propertyName), propertyClass).invoke(bean, value);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
