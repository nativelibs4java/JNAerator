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
package com.ochafik.lang.jnaerator.studio;

import java.io.PrintWriter;
import java.io.StringWriter;

class ResultContent {
	StringWriter out = new StringWriter();
	final String path;
	
	public ResultContent(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return path;
	}
	public String getContent() {
		return out.toString();
	}
	public PrintWriter getPrintWriter() {
		return new PrintWriter(out) {
			@Override
			public void close() {
				super.close();
				closed();
			}
		};
	}
	protected void closed() {
		
	}
}