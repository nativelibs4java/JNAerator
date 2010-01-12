/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator.parser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;


import static com.ochafik.lang.SyntaxUtils.*;

import com.ochafik.beans.BeansUtils;
import com.ochafik.junit.ParameterizedWithDescription;
//import com.ochafik.lang.jnaerator.parser.Expression.FunctionCall;
import com.ochafik.lang.reflect.GettersAndSettersHelper;
import com.ochafik.lang.reflect.GettersAndSettersHelper.GetterAndSetterInfo;
import com.ochafik.util.listenable.Pair;

@SuppressWarnings("unused")
//@RunWith(Parameterized.class)
@RunWith(Parameterized.class)
public class ObjCppElementsTest {
	String description;
	Class<? extends Element> type;
	
	/// Gives the set of subclasses that implement a given abstract class or interface
	Map<Class<?>, Set<Class<?>>> implementations;
	
	public ObjCppElementsTest(String description, Class<? extends Element> type, Map<Class<?>, Set<Class<?>>> implementations) {
		super();
		this.description = description;
		this.type = type;
		this.implementations = implementations;
	}
	
	final static Set<String> fieldsExcludedFromGetterSetterChecks = new HashSet<String>();
	static {
		fieldsExcludedFromGetterSetterChecks.add("previousSibling"); 
		fieldsExcludedFromGetterSetterChecks.add("nextSibling"); 
		fieldsExcludedFromGetterSetterChecks.add("plain"); 
		fieldsExcludedFromGetterSetterChecks.add("class");
		fieldsExcludedFromGetterSetterChecks.add("const");
		fieldsExcludedFromGetterSetterChecks.add("unsigned");
		fieldsExcludedFromGetterSetterChecks.add("gettersAndSetters");
		fieldsExcludedFromGetterSetterChecks.add("modifiersStringPrefix");
		fieldsExcludedFromGetterSetterChecks.add("id");
		fieldsExcludedFromGetterSetterChecks.add("varArgs");
		fieldsExcludedFromGetterSetterChecks.add("parents");
		fieldsExcludedFromGetterSetterChecks.add("pathInFramework");
		fieldsExcludedFromGetterSetterChecks.add("plainStorage");
	}
	
//	@Test
//	public void checkDerivedClone() {
//		try {
//			Method m = type.getMethod("clone");
//			assertEquals(m + " must have a return type of " + type.getSimpleName(), m.getReturnType(), type);
//		} catch (Exception e) {
//			assertTrue("Failed to get clone method (bad visibility ?)", false);
//		}
//	}
	@Test
	public void checkGettersAndSetters() {
		Element element = newElement();
		GettersAndSettersHelper helper = element.getGettersAndSetters();//new GettersAndSettersHelper(type);
		
		for (Map.Entry<String, GetterAndSetterInfo> e : helper.gettersAndSetters.entrySet()) {
			GetterAndSetterInfo p = e.getValue();
			String fieldName = e.getKey();
			Type fieldType = helper.getFieldType(fieldName );
			
			assertNotNull("Field " + fieldName + " does not have a getter !", p.getter);
			if (!fieldsExcludedFromGetterSetterChecks.contains(fieldName))
				assertNotNull("Field " + fieldName + " does not have a setter !", p.setter);
			
			if (p.setter == null)
				continue;
			
			helper.assertConsistentPair(p);
			
//			System.err.println("Testing field " + type.getSimpleName() + "." + fieldName);// + ": ");
			
			if (fieldType instanceof Class<?> && Element.class.isAssignableFrom((Class<?>) fieldType)) {
				testSetNewInstancesOf(fieldType, implementations, element, fieldName, p.setter, p.getter, null);
				//System.err.println();
			} else if (fieldType instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) fieldType;
				Type rawType = paramType.getRawType();
				if (rawType instanceof Class<?>) {
					Class<?> rawClass = (Class<?>) rawType;
					boolean isList = List.class.isAssignableFrom(rawClass);
					if (isList || Set.class.isAssignableFrom(rawClass)) {
						Collection<Element> container = isList ? new ArrayList<Element>() : new TreeSet<Element>(new Comparator<Element>() {
							public int compare(Element o1, Element o2) {
								return o1.toString().compareTo(o2.toString());
							}
						});
						Type[] typeArguments = paramType.getActualTypeArguments();
						if (typeArguments.length == 1 && typeArguments[0] instanceof Class<?> && Element.class.isAssignableFrom((Class<?>) typeArguments[0])) {
							Class<?> elementType = (Class<?>) typeArguments[0];
							testSetNewInstancesOf(elementType, implementations, element, fieldName, p.setter, p.getter, container);
						}
					} else {
						//assertTrue("Field '" + fieldName + "' of type " + fieldType + " cannot be tested !" , false);
					}
				}
			} else if (String.class.isAssignableFrom(as(fieldType, Class.class))) {
				Element instance = newElement();
				BeansUtils.set(instance, fieldName, String.class, "");
				Object v = BeansUtils.get(instance, fieldName);
				assertEquals("Testing " + fieldName + " :get(set(\"\"))", v, "");
				BeansUtils.set(instance, fieldName, String.class, null);
				v = BeansUtils.get(instance, fieldName);
				assertEquals(v, null);
			}
		}
		
		
	}

	private void testSetNewInstancesOf(Type fieldType, Map<Class<?>, Set<Class<?>>> implementations2, Element element, String fieldName, Method setter, Method getter, Collection<Element> container) {
		Set<Class<?>> fieldTypeImplementations = implementations.get(fieldType);
		assertNotNull("Found no implementation of " + fieldType, fieldTypeImplementations);
		for (Class<?> fieldTypeImplClass : fieldTypeImplementations) {
			testSetNewInstanceOf(fieldTypeImplClass, element, fieldName, setter, getter, container);
		}
	}

	@SuppressWarnings("unchecked")
	private void testSetNewInstanceOf(Class<?> fieldTypeImplClass,
			Element element, String fieldName, Method setter, Method getter, Collection<Element> container) {
		
		Element arg = null, replace = null;
		try {
			arg = (Element) fieldTypeImplClass.newInstance();
			replace = (Element) fieldTypeImplClass.newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			assertFalse("Failed to create new " + fieldTypeImplClass.getName() + " : " + ex + "; cause : " + ex.getCause(), true);
		}
		
//		System.err.print("setter ");
		/// Setter
		
		Object valueToBeSet;
		if (container == null) {
			valueToBeSet = arg;
		} else {
			container.clear();
			container.add(arg);
			valueToBeSet = container;
		}
		
		try {
			setter.invoke(element, valueToBeSet);
			if (!fieldName.equals("parentElement")) {
				assertNotNull("Parent element not set in " + setter, arg.getParentElement());
				assertEquals("Parent element not set properly in " + setter, 
						element, arg.getParentElement());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertFalse("Failed to call " + setter + " with a " + fieldTypeImplClass.getName() + " : " + ex + "; cause : " + ex.getCause(), true);
		}
		
		if (!fieldName.equals("parentElement")) {
			final Set<Integer> visitedIds = new TreeSet<Integer>();
			element.accept(new Scanner() {
				@Override
				protected void visitElement(Element d) {
					if (d != null)
						visitedIds.add(d.getId());
					
					super.visitElement(d);
				}
			});
			assertTrue("Scanner did not visit child element " + fieldName + " with a " + fieldTypeImplClass.getName() , visitedIds.contains(arg.getId()));
		}
		
		
		if (getter != null) {
//			System.err.print("getter ");
			/// Getter
			try {
				Object got = getter.invoke(element);
				Element elementGot = container == null ? (Element)got : ((Collection<Element>)got).iterator().next();
				
				assertEquals("Getter of " + fieldName + " does not return same object as setter in " + type.getName(), arg, elementGot);
				
				if (!fieldName.equals("parentElement")) {
					
					if (container != null) {
						container.clear();
						container.add(replace);
					}
					
					arg.replaceBy(replace);
					Object got2 = getter.invoke(element);
					Element elementGot2 = container == null ? (Element)got2 : ((Collection<Element>)got2).iterator().next();
					
//					if (arg != elementGot2)
//						arg =arg; // debug: break here
					assertFalse("Replacement of field " + fieldName + " in " + type.getName() + " failed", arg == elementGot2);
					assertEquals("Replacement of field " + fieldName + " in " + type.getName() + " failed", replace, elementGot2);
					assertNull("Parent element not removed in " + setter, elementGot.getParentElement());
					assertNotNull("Parent element not set in " + setter, elementGot2.getParentElement());
					
					assertEquals("Parent element not set properly in " + setter, 
							element, elementGot2.getParentElement());
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
				assertFalse("Failed to call " + setter + " with a " + fieldTypeImplClass.getName() + " : " + ex + "; cause : " + ex.getCause(), true);
			}
			
		}
	}

	private Element newElement() {
		try {
			return type.newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			assertFalse("Failed to create new " + type.getName() + " : " + ex + "; cause : " + ex.getCause(), true);
			return null;
		}
	}


	@Parameters
	public static List<Object[]> getClassesToTest() throws IOException {
		Set<Class<?>> elementClasses = new HashSet<Class<?>>();
		for (Method method : Scanner.class.getMethods()) {
			for (Class<?> argType : method.getParameterTypes())
				if (Element.class.isAssignableFrom(argType))
					elementClasses.add(argType);
		
			Class<?> returnType = method.getReturnType();
			if (Element.class.isAssignableFrom(returnType))
				elementClasses.add(returnType);		
		}
		
		Map<Class<?>, Set<Class<?>>> implementations = new HashMap<Class<?>, Set<Class<?>>>();
		for (Class<?> c : elementClasses) {
			if ((c.getModifiers() & Modifier.ABSTRACT) != 0)
				continue;
			
			Class<?> s = c;
			do {
				Set<Class<?>> cs = implementations.get(s);
				if (cs == null)
					implementations.put(s, cs = new HashSet<Class<?>>());
				cs.add(c);
				s = s.getSuperclass();
			} while (s != null);
		}
		
		List<Object[]> ret = new ArrayList<Object[]>();
		for (Class<?> c : elementClasses) {
			if ((c.getModifiers() & Modifier.ABSTRACT) != 0)
				continue;
			ret.add(new Object[] {c.getName(), c, implementations});
		}
		return ret;
	}
	public static class TestSiblings {
		@Test
		public void addSibling() {
			VariablesDeclaration d = new VariablesDeclaration();
			Declarator s1 = new Declarator.DirectDeclarator(), s2 = new Declarator.DirectDeclarator();
			
			d.setDeclarators(Arrays.asList(s1));
			s1.insertSibling(s2, false);
			List<Declarator> list = d.getDeclarators();
			assertEquals("Failed to add after", 2, list.size());
			assertSame("Added, but not after", s1, list.get(0));
			assertSame(s2, list.get(1));
			
			s2.replaceBy(null);
			list = d.getDeclarators();
			assertEquals("Failed to remove added element", 1, list.size());
			assertSame("Removed bad element", s1, list.get(0));
			
			d.setDeclarators(Arrays.asList(s1));
			s1.insertSibling(s2, true);
			list = d.getDeclarators();
			assertEquals("Failed to add before", 2, list.size());
			assertSame("Added, but not before", s2, list.get(0));
	 		assertSame(s1, list.get(1));
	 		
	 		s2.replaceBy(null);
			list = d.getDeclarators();
			assertEquals("Failed to remove added element", 1, list.size());
			assertSame("Removed bad element", s1, list.get(0));			
		}
	}
}
