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
package com.ochafik.util.string;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class StringUtils {
	public static String htmlize(String text) {
		return text.startsWith("<?") || text.startsWith("<html>") ? 
				text : "<html><body>"+
					text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n","<br>")+
				"</body></html>";
	}
	
	static Pattern spacePattern; 
	public static List<String> explode(String s) {
		if (spacePattern == null) {
			spacePattern = Pattern.compile("\\s+");
		}
		return explode(s, spacePattern);
	}
	public static List<String> explode(String s, String sep) {
		StringTokenizer st=new StringTokenizer(s,sep);
		List<String> v = new ArrayList<String>();
		for (;st.hasMoreTokens();) {
			v.add(st.nextToken());
		}
		return v;
	}
	
/*
	public static final String implode(Object[] strings, String separator) {
		return implode(Arrays.asList(strings), separator);
	}
*/
	public static String implode(double[] array, String separator) {
		StringBuffer out = new StringBuffer();
		boolean first = true;
		for (double v : array) {
			if (first) first = false;
			else out.append(separator);
			out.append(v);
		}
		return out.toString();
	}
	
	public static String implode(Object[] values) {
		return implode(values, ", ");
	}
	public static String implode(Object[] values, Object separator) {
		return implode(Arrays.asList(values), separator);
	}
	public static final <T> String implode(Iterable<T> elements, Object separator) {
		String sepStr = separator.toString();
		StringBuilder out = new StringBuilder();
		boolean first = true;
		for (Object s : elements) {
			if (s == null)
				continue;
			
			if (first) 
				first = false;
			else 
				out.append(sepStr);
			out.append(s);
		}
		return out.toString();
	}
	public static final String implode(Iterable<?> strings) {
		return implode(strings, ", ");
	}
	/*
	public static final String implode(Collection<?> strings, String separator) {
		int size = 0, n = strings.size();
		for (Object s : strings)
			if (s != null)
				size += s.toString().length();
		
		StringBuffer out = new StringBuffer(size + separator.length() * (n == 0 ? 0 : n - 1));
		boolean first = true;
		for (Object s : strings) {
			if (s == null)
				continue;
			
			if (first) first = false;
			else out.append(separator);
			out.append(s);
		}
		return out.toString();
	}
	*/
	public static final List<String> explode(String string, Pattern separator) {
		int lastIndex = 0, len = string.length();
		
		Matcher matcher = separator.matcher(string);
		List<String> ret = new LinkedList<String>();
		
		while (matcher.find()) {
			String s = string.substring(lastIndex, matcher.start());
			if (s.length() > 0) ret.add(s);
			lastIndex = matcher.end();
		}
		String s = string.substring(lastIndex, len);
		if (s.length() > 0) ret.add(s);
		
		return ret;
	}
	
	public static String replace(String pattern, String replace, String s) {
		return concatWithSeparator(explode(s,pattern).toArray(new String[0]),replace);
	}
	public static final String concat(String[] a) {
		StringBuffer b=new StringBuffer();
		for (int i=0;i<a.length;i++) b.append(a[i]);
		return b.toString();
	}
	public static final String concatln(String[] a) {
		StringBuffer b=new StringBuffer();
		int lenm=a.length-1;
		for (int i=0;i<lenm;i++) {
			b.append(a[i]);
			b.append("\n");
		}
		if (lenm!=-1) b.append(a[lenm]);
		return b.toString();
	}
	public static final String concatSpace(String[] a) {
		StringBuffer b=new StringBuffer();
		int lenm=a.length-1;
		for (int i=0;i<lenm;i++) {
			b.append(a[i]);
			b.append(" ");
		}
		if (lenm!=-1) b.append(a[lenm]);
		return b.toString();
	}
	public static final String concatWithSeparator(String[] a,String sep) {
		StringBuffer b=new StringBuffer();
		int lenm=a.length-1;
		for (int i=0;i<lenm;i++) {
			b.append(a[i]);
			b.append(sep);
		}
		if (lenm!=-1) b.append(a[lenm]);
		return b.toString();
	}
	public static final String javaEscape(String s) {
		if (s == null)
			return null;
		char c;
		int len=s.length();
		StringBuffer b=new StringBuffer(len);
		for (int i=0;i<len;i++) {
			c=s.charAt(i);
			switch (c) {
				case '\n':
					b.append("\\n");
					break;
				case '\t':
					b.append("\\t");
					break;
				case '\r':
					b.append("\\r");
					break;
				case '"':
					b.append("\"");
					break;
				case '\\':
					b.append("\\\\");
					break;
				default:
					if (c>127||Character.isISOControl(c)) {
						b.append("\\u");
						String nb=Integer.toString((int)c,16);
						int nblen=nb.length();
						switch (nblen) {
							case 1:
								b.append(0);
							case 2:
								b.append(0);
							case 3:
								b.append(0);
							case 4:
								b.append(nb);
								break;
							default:
								throw new IllegalArgumentException("Should not happen !");
						}
					} else b.append(c);
			}
		}
		return b.toString();
	}
	public static final String javaUnEscape(String s) {
		char c;
		int len=s.length();
		StringBuffer b=new StringBuffer(len);
		for (int i=0;i<len;i++) {
			c=s.charAt(i);
			if (c=='\\') {
				c=s.charAt(++i);
				switch (c) {
					case 'n':
						b.append('\n');
						break;
					case 'r':
						b.append('\r');
						break;
					case 't':
						b.append('\t');
						break;
					case '\\':
						b.append('\\');
						break;
					case '"':
						b.append('"');
						break;
					case '\'':
						b.append('\'');
						break;
					case 'u':
						try {
							String nb=s.substring(i+1,i+5);
							int n=Integer.parseInt(nb,16);
							b.append((char)n);
							i+=4;
						} catch (Exception ex) {
							throw new IllegalArgumentException("Illegal unicode escaping in string \"" + s + "\" at index " + i, ex);
						}
						break;
					default:
						throw new IllegalArgumentException("Unknown character: \"\\"+String.valueOf(c)+"...\"");
				}
			} else b.append(c);
		}
		return b.toString();
	}

	public static String capitalize(String string) {
		return string == null ? null : string.length() == 0 ? "" : Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}
	public static String capitalize(List<String> strings, String separator) {
		List<String> cap = new ArrayList<String>(strings.size());
		for (String s : strings)
			cap.add(capitalize(s));
		return implode(cap, separator);
	}
	
	public static String uncapitalize(String string) {
		return string.length() == 0 ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
	}
	public static final String LINE_SEPARATOR;
	static {
		LINE_SEPARATOR = System.getProperty("line.separator");
	}
	
}
