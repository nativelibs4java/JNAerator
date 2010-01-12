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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ochafik.util.listenable.Adapter;
import com.ochafik.util.listenable.Pair;

public class RegexUtils {
	static final String[][] simplePatternReplacements=new String[][] {
			{"(\\.|\\+|\\[|\\]|\\{|\\})","\\\\."},
			{"(\\*|\\?)",".$1"},
			/*{"[a\u00e0\u00e2\u00e4]","[a\u00e0\u00e2\u00e4]"},
			{"[e\u00e9\u00e8\u00ea\u00eb]","[e\u00e9\u00e8\u00ea\u00eb]"},
			{"[i\u00ee\u00ef\u00ec]","[i\u00ee\u00ef\u00ec]"},
			{"[o\u00f4\u00f6\u00f2]","[o\u00f4\u00f6\u00f2]"},
			{"[u\u00fb\u00fc\u00f9]","[u\u00fb\u00fc\u00f9]"},
			{"[y\u00ff]","[y\u00ff]"},
			{"[c\u00e7]","[c\u00e7]"},*/
			{",","|"}
	};
	static final Pattern[] simplePatternReplacementPatterns;
	static {
		simplePatternReplacementPatterns=new Pattern[simplePatternReplacements.length];
		for (int i=simplePatternReplacements.length;i--!=0;) {
			simplePatternReplacementPatterns[i]=Pattern.compile(simplePatternReplacements[i][0]);
		}
	}
	private static final String replaceSimplePatterByRegex(String string) {
		for (int i=simplePatternReplacements.length;i--!=0;) {
			string=simplePatternReplacementPatterns[i].matcher(string).replaceAll(simplePatternReplacements[i][1]);
		}
		return string;
	}
	public static final Pattern simplePatternToRegex(String pattern) {
		try {
			return Pattern.compile(replaceSimplePatterByRegex(pattern),Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
		} catch (Exception ex) {
			return Pattern.compile(pattern,Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.LITERAL);
		}
	}
	public static String regexReplace(Pattern pattern, String string, MessageFormat replacement) {
		StringBuffer b=new StringBuffer(string.length());
		int iLastCommitted=0;
		Matcher matcher=pattern.matcher(string);
		while (matcher.find(iLastCommitted)) {
			int start=matcher.start();
			String s = string.substring(iLastCommitted,start);
			String g = matcher.group(0);
			b.append(s);
			if (replacement!=null)
				b.append(replacement.format(getGroups(matcher),b,null));
			else
				b.append(g);
			
			iLastCommitted = start + g.length();
		}
		b.append(string.substring(iLastCommitted));
		return b.toString();
	}
	public static final boolean findLast(Matcher matcher) {
		matcher.reset();
		int n = 0;
		while (matcher.find()) {
			n++;
		}
		matcher.reset();
		for (int i = 0; i < n; i++) matcher.find();
		return n > 0;
	}
	
	public static final List<Pair<String, String[]>> grep(Collection<String> list, Pattern p) {
		List<Pair<String, String[]>> ret = new ArrayList<Pair<String, String[]>>(list.size());
		for (String s : list) {
			Matcher matcher = p.matcher(s);
			if (matcher.find())
				ret.add(new Pair<String, String[]>(s, getGroups(matcher)));
		}
		return ret;
	}
	
	public static String[] getGroups(Matcher matcher) {
		String[] groups = new String[matcher.groupCount() + 1];
		for (int i = groups.length; i-- != 0;) {
			groups[i] = matcher.group(i);
		}
		return groups;
	}
	public static final List<String[]> grep(String s, Pattern p) {
		List<String[]> ret = new ArrayList<String[]>();
		Matcher matcher = p.matcher(s);
		while (matcher.find())
			ret.add(getGroups(matcher));
		
		return ret;
	}
	public static final String[] match(String s, Pattern p) {
		Matcher matcher = p.matcher(s);
		if (matcher.matches())
			return getGroups(matcher);
		
		return null;
	}
	public static List<String[]> find(String string, String pattern) {
		return find(string, Pattern.compile(pattern));
	}
	
	public static List<String[]> find(String string, Pattern pattern)  {
		List<String[]> ret = new LinkedList<String[]>();
		
		Matcher matcher = pattern.matcher(string);
		while (matcher.find())
			ret.add(getGroups(matcher));
		
		return ret;
	}
	public static Collection<String> find(String string, String pattern, final int iGroup) {
		return find(string, Pattern.compile(pattern), iGroup);
	}
	public static Collection<String> find(String string, Pattern pattern, final int iGroup) {
		List<String> ret=  new ArrayList<String>();
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			ret.add(matcher.group(iGroup));
		}
		return ret;
	}
	public static String findFirst(String string, Pattern pattern, int iGroup) {
		if (string == null)
			return null;
		
		Matcher matcher = pattern.matcher(string);
		return matcher.find() ? matcher.group(iGroup) : null;
	}
	public static String regexReplace(String pat, String text, String rep) {
		return regexReplace(Pattern.compile(pat), text, new MessageFormat(rep));
	}
	public static String regexReplace(Pattern pattern, String string, Adapter<String[], String> adapter) {
		StringBuffer b=new StringBuffer(string.length());
		int iLastCommitted=0;
		Matcher matcher=pattern.matcher(string);
		while (matcher.find(iLastCommitted)) {
			int start=matcher.start();
			String s = string.substring(iLastCommitted,start);
			String g = matcher.group(0);
			b.append(s);
			if (adapter!=null)
				b.append(adapter.adapt(getGroups(matcher)));
			else
				b.append(g);
			iLastCommitted = start + g.length();
		}
		b.append(string.substring(iLastCommitted));
		return b.toString();
	}
	
}
