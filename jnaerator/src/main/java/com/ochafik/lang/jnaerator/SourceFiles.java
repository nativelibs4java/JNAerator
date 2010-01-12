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
package com.ochafik.lang.jnaerator;

import java.util.ArrayList;
import java.util.List;

import com.ochafik.lang.jnaerator.parser.Define;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.SourceFile;
import com.ochafik.lang.jnaerator.parser.Visitor;

public class SourceFiles extends Element {
	public List<Define> defines = new ArrayList<Define>();
	public List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
	
	@Override
	public SourceFiles clone() {
		return (SourceFiles) super.clone();
	}
	public void accept(Visitor visitor) {
		for (SourceFile sourceFile : sourceFiles)
			sourceFile.accept(visitor);

		for (Define define : defines)
			define.accept(visitor);
	}
	
	public List<SourceFile> getSourceFiles() {
		return sourceFiles;
	}
	public List<Define> getDefines() {
		return defines;
	}
	public void add(SourceFile sourceFile) {
		sourceFiles.add(sourceFile);
	}
	@Override
	public String toString(CharSequence indent) {
		return implode(sourceFiles, "\n" + indent, indent);
	}

	@Override
	public Element getNextChild(Element child) {
		return getNextSibling(sourceFiles, child);
	}

	@Override
	public Element getPreviousChild(Element child) {
		return getPreviousSibling(sourceFiles, child);
	}

	@Override
	public boolean replaceChild(Element child, Element by) {
		if (replaceChild(defines, Define.class, this, child, by))
			return true;
		
		return replaceChild(sourceFiles, SourceFile.class, this, child, by);
	}
}