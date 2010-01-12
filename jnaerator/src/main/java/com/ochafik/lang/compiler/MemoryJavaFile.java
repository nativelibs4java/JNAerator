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
package com.ochafik.lang.compiler;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

public class MemoryJavaFile extends MemoryFileObject implements JavaFileObject {
	JavaFileObject.Kind kind;
	public MemoryJavaFile(String path, String content, JavaFileObject.Kind kind) {
		super(path, content);
		this.kind = kind;
	}
	public Modifier getAccessLevel() {
		return Modifier.PUBLIC;
	}
	public JavaFileObject.Kind getKind() {
		return kind;
	}
	public NestingKind getNestingKind() {
		return null;
	}
	public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind) {
		return true; // TODO
	}
	
}