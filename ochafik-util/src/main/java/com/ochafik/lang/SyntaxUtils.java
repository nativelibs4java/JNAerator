/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (https://github.com/nativelibs4java/JNAerator).
	
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
package com.ochafik.lang;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SyntaxUtils {
	public static <T> T[] array(T... elements) {
		return elements;
	}
	
	public static <T> boolean equal(T a, T b) {
		if (a == null)
			return b == null;
		if (b == null)
			return false;
        return a == b || a.equals(b);
	}

	public static <T> T as(Object value, Class<T> c) {
		if (value == null)
			return null;
		if (c.isAssignableFrom(value.getClass()))
			return c.cast(value);
		return null;
	}
	
	interface Converter<T> {
		public T convert(Object value) throws Exception;
	}
	
	public static <T> void registerConverter(Class<T> c, Converter<T> converter) {
		converters.put(c, converter);
	}
	
	static Map<Class<?>, Converter<?>> converters = new LinkedHashMap<Class<?>, Converter<?>>();
	static {
		registerConverter(Date.class, new Converter<Date>() {
			public Date convert(Object value) throws ParseException {
				String s = value.toString();
				Date date = DateFormat.getDateTimeInstance().parse(s);
				if (date == null)
					date = DateFormat.getDateInstance().parse(s);
				return date;
			}
		});
		
		registerConverter(Double.class, new Converter<Double>() {
			public Double convert(Object value) throws ParseException {
				if (value instanceof Number)
					return ((Number)value).doubleValue();
				String s = value.toString();
				if (s.length() == 0)
					return 0.0;
				
				return Double.parseDouble(s);
			}
		});
		registerConverter(Float.class, new Converter<Float>() {
			public Float convert(Object value) throws ParseException {
				if (value instanceof Number)
					return ((Number)value).floatValue();
				String s = value.toString();
				if (s.length() == 0)
					return 0f;
				
				return Float.parseFloat(s);
			}
		});
		registerConverter(Long.class, new Converter<Long>() {
			public Long convert(Object value) throws ParseException {
				if (value instanceof Number)
					return ((Number)value).longValue();
				String s = value.toString();
				if (s.length() == 0)
					return 0l;
				
				return Long.parseLong(s);
			}
		});
		registerConverter(Integer.class, new Converter<Integer>() {
			public Integer convert(Object value) throws ParseException {
				if (value instanceof Number)
					return ((Number)value).intValue();
				String s = value.toString();
				if (s.length() == 0)
					return 0;
				
				return Integer.parseInt(s);
			}
		});
		registerConverter(Short.class, new Converter<Short>() {
			public Short convert(Object value) throws ParseException {
				if (value instanceof Number)
					return ((Number)value).shortValue();
				String s = value.toString();
				if (s.length() == 0)
					return 0;
				
				return Short.parseShort(s);
			}
		});
		registerConverter(String.class, new Converter<String>() {
			public String convert(Object value) throws ParseException {
				if (value instanceof Date)
					return DateFormat.getDateInstance().format((Date)value);
				return value.toString();
			}
		});
		registerConverter(File.class, new Converter<File>() {
			public File convert(Object value) throws ParseException {
				if (value instanceof URI)
					try {
						value = ((URI)value).toURL();
					} catch (MalformedURLException e1) {}
				
				if (value instanceof String) {
					try {
						value = new URL((String)value);
					} catch (MalformedURLException e) {}
				}
				if (value instanceof URL)
					return new File(((URL)value).getFile());
				
				String s = value.toString();
				if (s.length() == 0)
					return null;
				
				return new File(s);
			}
		});
		registerConverter(URL.class, new Converter<URL>() {
			public URL convert(Object value) {
				if (value instanceof URI)
					try {
						return ((URI)value).toURL();
					} catch (MalformedURLException e1) {}
				
				if (value instanceof File)
					try {
						return ((File)value).toURI().toURL();
					} catch (MalformedURLException e1) {}
				
				String s = value.toString();
				if (s.length() == 0)
					return null;
				
				try {
					return new URL(s);
				} catch (MalformedURLException e) {}
				
				try {
					return new File(s).toURI().toURL();
				} catch (MalformedURLException e) {}
				
				return null;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, Class<T> c) {
		if (value == null)
			return null;
		
		Class<?> type = value.getClass();
		if (c.isAssignableFrom(type))
			return c.cast(value);
		
		Converter<T> converter = (Converter<T>)converters.get(c);
		if (converter != null)
			try {
				return converter.convert(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		for (Constructor<?> cons : c.getConstructors()) {
			Class<?>[] parameterTypes = cons.getParameterTypes();
			if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(type)) {
				try {
					return (T)cons.newInstance(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		for (Method meth : c.getMethods()) {
			String name = meth.getName();
			if ((meth.getModifiers() & Modifier.STATIC) == 0)
				continue;
			
			if (!" parse createInstance newInstance ".contains(" " + name + " "))
				continue;
			
			Class<?>[] parameterTypes = meth.getParameterTypes();
			if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(type)) {
				try {
					return (T)meth.invoke(null, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static class EasyMap<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = -3087972422440202407L;

		public EasyMap<K, V> add(K key, V value) {
			put(key, value);
			return this;
		}
	}
	public static <K, V> EasyMap<K, V> map(K key, V value) {
		return new EasyMap<K, V>().add(key, value);
	}

	@SuppressWarnings("unchecked")
	public static Iterable<Node> iterable(final NodeList list) {
		if (list == null)
			return Collections.EMPTY_LIST;
		
		return new Iterable<Node>() {
			int nextPos = 0;
			public Iterator<Node> iterator() {
				return new Iterator<Node>() {
					public Node next() {
						if (nextPos >= list.getLength())
							throw new NoSuchElementException();
						return list.item(nextPos++);
					}
					public boolean hasNext() {
						return nextPos < list.getLength();
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}
