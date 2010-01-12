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
package com.ochafik.io;
import java.io.IOException;
import java.io.OutputStream;
public class StringBufferOutputStream extends OutputStream {
	StringBuffer buffer;
	public StringBufferOutputStream() {
		buffer=new StringBuffer();
	}
	public String toString() { return buffer.toString(); }
	public StringBufferOutputStream(StringBuffer b) {
		buffer=b;
	}
	public StringBufferOutputStream(String s) {
		buffer=new StringBuffer(s);
	}	
	public void close() throws IOException {
		buffer=null;
	}
	public void flush() throws IOException {}
	public void write(int i) throws IOException {
		buffer.append((char)i);
	}
	public void write(byte[] b,int off,int l) throws IOException {
		buffer.append(new String(b,off,l));
	}
	public void write(byte[] b) throws IOException {
		buffer.append(new String(b));
	}
}
		
