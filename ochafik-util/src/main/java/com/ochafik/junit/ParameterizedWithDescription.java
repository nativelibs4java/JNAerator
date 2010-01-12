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
package com.ochafik.junit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.internal.runners.ClassRoadie;
//import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestClass;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized.Parameters;

public class ParameterizedWithDescription {} /*extends CompositeRunner {
	static class TestClassRunnerForParameters extends JUnit4ClassRunner {
		private final Object[] fParameters;

		private final int fParameterSetNumber;

		private final Constructor<?> fConstructor;

		TestClassRunnerForParameters(TestClass testClass, Object[] parameters, int i) throws InitializationError {
			super(testClass.getJavaClass()); //todo
			fParameters= parameters;
			fParameterSetNumber= i;
			fConstructor= getOnlyConstructor();
		}

		@Override
		protected Object createTest() throws Exception {
			return fConstructor.newInstance(fParameters);
		}
		
		@Override
		protected String getName() {
			try {
				return String.valueOf(fParameters[0]).replaceAll("(?s)\n.*", "...");//replace('\n', ' ');
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return String.format("[%s]", fParameterSetNumber);
		}
		
		@Override
		protected String testName(final Method method) {
			try {
				return method.getName() + " : " + String.valueOf(fParameters[0]).replace('\n', ' ');
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return String.format("%s[%s]", method.getName(), fParameterSetNumber);
		}

		private Constructor<?> getOnlyConstructor() {
			Constructor<?>[] constructors= getTestClass().getJavaClass().getConstructors();
			Assert.assertEquals(1, constructors.length);
			return constructors[0];
		}
		
		@Override
		protected void validate() throws InitializationError {
			// do nothing: validated before.
		}
		
		@Override
		public void run(RunNotifier notifier) {
			runMethods(notifier);
		}
	}
	
	private final TestClass fTestClass;

	public ParameterizedWithDescription(Class<?> klass) throws Exception {
		super(klass.getName());
		fTestClass= new TestClass(klass);
		
		MethodValidator methodValidator= new MethodValidator(fTestClass);
		methodValidator.validateStaticMethods();
		methodValidator.validateInstanceMethods();
		methodValidator.assertValid();
		
		int i= 0;
		for (final Object each : getParametersList()) {
			if (each instanceof Object[])
				add(new TestClassRunnerForParameters(fTestClass, (Object[])each, i++));
			else
				throw new Exception(String.format("%s.%s() must return a Collection of arrays.", fTestClass.getName(), getParametersMethod().getName()));
		}
	}
	
	@Override
	public void run(final RunNotifier notifier) {
		new ClassRoadie(notifier, fTestClass, getDescription(), new Runnable() {
			public void run() {
				runChildren(notifier);
			}
		}).runProtected();
	}
	
	private Collection<?> getParametersList() throws IllegalAccessException, InvocationTargetException, Exception {
		return (Collection<?>) getParametersMethod().invoke(null);
	}
	
	private Method getParametersMethod() throws Exception {
		List<Method> methods= fTestClass.getAnnotatedMethods(Parameters.class);
		for (Method each : methods) {
			int modifiers= each.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class " + getName());
	}

	public static Collection<Object[]> eachOne(Object... params) {
		List<Object[]> results= new ArrayList<Object[]>();
		for (Object param : params)
			results.add(new Object[] { param });
		return results;
	}
}*/
