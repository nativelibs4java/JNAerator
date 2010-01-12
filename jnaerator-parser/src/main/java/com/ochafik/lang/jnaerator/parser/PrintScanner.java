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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.ochafik.util.string.StringUtils;

public class PrintScanner extends Scanner {
	StringWriter sout = new StringWriter();
	protected PrintWriter out = new PrintWriter(sout);
	protected final MessageFormat invalidFormat;
	protected List<Exception> exceptions = new ArrayList<Exception>();
	
	public PrintScanner(String invalidFormatString) {
		this(new MessageFormat(invalidFormatString));
	}
	public PrintScanner(MessageFormat invalidFormat) {
		this.invalidFormat = invalidFormat;
	}
	public PrintScanner() {
		this("/*\nErrors occurred :\n{0}\n{1}\n*/");
	}

	String indent = "";
	public String getIndent() {
		return indent;
	}
	public String setIndent(String indent) {
		return this.indent = indent;
	}
	public String incrIndent() {
		return setIndent(getIndent() + "\t");
	}
	public String decrIndent() {
		String s = getIndent();
		int len = s.length();
		if (len > 0 && s.charAt(len - 1) == '\t')
			return s.substring(0, len - 1);
		return s;
	}
	
	protected void println(Object... os) {
		if (os.length == 0)
			out.println();
		
		for (Object o : os)
			out.println(o);
	}
	protected void print(Object... os) {
		for (Object o : os)
			out.print(o);
	}
	protected void addException(Exception ex) {
		exceptions.add(ex);
	}
	
	@Override
	public String toString() {
		String s = sout.toString();
		if (exceptions.isEmpty())
			return s;
		else
			return invalidFormat.format(new Object[] {StringUtils.implode(exceptions, "\n").replace("*/", "* /"), s.replace("*/", "* /")}, new StringBuffer(), null).toString();
	}
}
