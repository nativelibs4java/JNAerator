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
package com.ochafik.lang.reflect;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.ochafik.io.StringBufferOutputStream;
import com.ochafik.lang.AssertUtils;

public class DebugUtils {

	public static abstract class FieldAccessor {
		public abstract Object access(Field f, Object target) throws IllegalArgumentException, IllegalAccessException;
		//public Object access(Field f, Object target) throws IllegalArgumentException, IllegalAccessException {
		//return f.get(target);
		//}
	}

	public static final void print(Object o, FieldAccessor accessor) {
		print(o, System.out, false, false, "", accessor);
	}

	public static final void printErr(Object o, FieldAccessor accessor) {
		print(o, System.err, false, false, "", accessor);
	}

	public static final void println(Object o, FieldAccessor accessor) {
		print(o, System.out, true, false, "", accessor);
	}

	public static final void printlnErr(Object o, FieldAccessor accessor) {
		print(o, System.err, true, false, "", accessor);
	}

	public static final void print(Object o) {
		print(o, System.out, false, false, "", null);
	}

	public static final void printErr(Object o) {
		print(o, System.err, false, false, "", null);
	}

	public static final void println(Object o) {
		print(o, System.out, true, false, "", null);
	}
	public static final void println(Object o, PrintStream out) {
		print(o, out, true, false, "", null);
	}

	public static final void printlnErr(Object o) {
		print(o, System.err, true, false, "", null);
	}

	protected static String escape(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\t", "\\t").replace("\r", "");
	}
	public static final void printAsCharSequence(CharSequence o, PrintStream out, boolean lines, boolean startIndent, String indent, FieldAccessor accessor) {
		out.print('"');
		out.print(escape(((CharSequence)o).toString()));
		out.print('"');
		if (lines) out.println();
	}

	public static final void printStructureInsides(Object o, PrintStream out, boolean lines, boolean startIndent, String indent, FieldAccessor accessor) {
		Class<?> type = o.getClass();
		Set<Field> fields = getFields(type);
		int i = 0;
		for (Field f : fields) {
			boolean inaccessibleValue = false;
			Object v;
			try {
				//f.isAccessible()
				v = accessor == null ? f.get(o) : accessor.access(f, o);
			} catch (IllegalAccessException ex) {
				// field is private and from a different package than the accessor ! Try using a getter
				String fn = f.getName();
				try {
					Method m = type.getMethod("get" + fn.substring(0,1).toUpperCase() + fn.substring(1), new Class[0]);
					v = m.invoke(o, new Object[0]);
				} catch (Exception e) {
					inaccessibleValue = true;
					v = null;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (lines) {
				out.print(indent);
			} else if (i++ != 0) out.print("; ");

			out.print(f.getName());
			out.print(" = ");
			if (inaccessibleValue) {
				out.print('?');
			} else {
				print(v, out, lines, false, indent, accessor);
			}
		}
	}

	public static final void print(Object o, PrintStream out, boolean lines, boolean startIndent, String indent, FieldAccessor accessor) {
		if (lines && startIndent) out.print(indent);

		if (o == null) {
			out.print(o);
			if (lines) out.println();
			return;
		} 
		Class<?> type = o.getClass();
		boolean isCollection = o instanceof Collection<?>;

		if (o instanceof CharSequence) {
			out.print('"');
			out.print(escape(o.toString()));
			out.print('"');
			if (lines) out.println();
			return;
		} else if (type == Character.class) {
			out.print('\'');
			out.print(escape(o.toString()));
			out.print('\'');
			if (lines) out.println();
			return;
		} else if (!isCollection && hasToStringMethod(type)) {
			out.print(o);
			if (lines) out.println();
			return;
		}
		out.print(type.getSimpleName());
		out.print(" {");
		if (lines) out.println();

		String newIndent = indent + "\t";

		if (type.isArray()) {
			for (int i = 0, n = Array.getLength(o); i < n; i++) {
				if (!lines && i != 0) out.print(", ");
				print(Array.get(o, i), out, lines, true, newIndent, accessor);
			}
		} else if (isCollection) {
			int i = 0;
			for (Object e : (Collection<?>)o) {
				if (!lines && i++ != 0) out.print(", ");
				print(e, out, lines, true, newIndent, accessor);
			}
		} else {
			printStructureInsides(o, out, lines, startIndent, newIndent, accessor);
		}
		if (lines) out.print(indent);
		out.print("}");
		if (lines) out.println();
	}

	public static Set<Field> getFields(Class<?> type) {
		Set<Field> fields = new TreeSet<Field>(new Comparator<Field>() { public int compare(Field o1, Field o2) { 
			return o1.getName().compareTo(o2.getName()); 
		}});
		fields.addAll(Arrays.asList(type.getFields()));
		do {
			fields.addAll(Arrays.asList(type.getDeclaredFields()));
		} while ((type = type.getSuperclass()) != null);
		return fields;
	}
	protected static void getFields_aux(Class<?> type, Set<Field> fields) {
		
	}
	
	public static boolean hasToStringMethod(Class<?> c) {
		if (c == Object.class) return false;
		for (Method m : c.getDeclaredMethods()) {
			if (m.getName().equals("toString") && m.getParameterTypes().length == 0) {
				return true;
			}
		}
		return hasToStringMethod(c.getSuperclass());
	}

	public static void main(String[] args) {
		//print(new int[] { 1, 2, 3, 4});
		println(new Object[] { new AssertUtils.Test(), }, new FieldAccessor() {
			public Object access(Field f, Object target) throws IllegalArgumentException, IllegalAccessException {
				return f.get(target);
			}
		});
	}

	public static String toString(Object object) {
		StringBufferOutputStream out = new StringBufferOutputStream();
		print(object, new PrintStream(out), false, false,"", null);
		return out.toString();
	}

}
