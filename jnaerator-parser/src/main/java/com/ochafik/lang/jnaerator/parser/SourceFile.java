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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceFile extends Element implements DeclarationsHolder {
	final List<Declaration> declarations = new ArrayList<Declaration>();
	//URL file;
	String framework, library;
//	String pathInFramework;
	
	private static Pattern frameworkPathPattern = Pattern.compile(".*/(\\w+)\\.framework/(?:.*/)?Headers/(?:.*/)?([^/]+)\\.[^/.]+$");
	private static Pattern bridgesupportFrameworkPattern = Pattern.compile("(?:^|/)(\\w+?)(?:Full)?\\.bridgesupport$");
	
//	public URL getFile() {
//		return file;
//	}

	@Override
	public SourceFile clone() {
		return (SourceFile)super.clone();
	}
	
	@Override
	public String toString(CharSequence indent) {
		return implode(declarations, "\n" + indent, indent);
	}
	@Override
	public Element getNextChild(Element child) {
		return getNextSibling(declarations, child);
	}

	@Override
	public Element getPreviousChild(Element child) {
		return getPreviousSibling(declarations, child);
	}

	@Override
	public boolean replaceChild(Element child, Element by) {
		return replaceChild(declarations, Declaration.class, this, child, by);
	}
	/*
	public void setFile(URL file) {
		this.file = file;
		if (framework == null) {
			Matcher matcher = frameworkPathPattern.matcher(file.toString());
			if (matcher.find()) {
				framework = matcher.group(1);
				pathInFramework = matcher.group(2);
			}
		}
	}
	public String getPathInFramework() {
		return pathInFramework;
	}*/
	
	@Override
	public void setElementFile(String file) {
		super.setElementFile(file);
		framework = null;
	}
	public String guessFramework() {
		if (framework == null) {
			if (getElementFile() != null) {
				Matcher matcher = frameworkPathPattern.matcher(getElementFile());
				if (matcher.find() || (matcher = bridgesupportFrameworkPattern.matcher(getElementFile())).find()) {
					framework = matcher.group(1);
				}
			}
		}
		return framework;
	}
//	public void setFramework(String framework) {
//		this.framework = framework;
//	}
//	
	public List<Declaration> getDeclarations() {
		return unmodifiableList(declarations);
	}
	public void setDeclarations(List<Declaration> declarations) {
		changeValue(this, this.declarations, declarations);
	}
	public void addDeclaration(Declaration d) {
		if (d == null)
			return;
			
		declarations.add(d);
		d.setParentElement(this);
	}
	public void addDeclarations(Collection<? extends Declaration> ds) {
		for (Declaration d : ds)
			addDeclaration(d);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitSourceFile(this);
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getLibrary() {
		return library;
	}
}
