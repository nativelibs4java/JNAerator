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
package org.anarres.cpp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;

public class DeferredFileReader extends Reader {
	Reader in;
	File file;

	static java.util.HashSet<File> files = new java.util.HashSet<File>();
	public DeferredFileReader(File file) throws FileNotFoundException {
		this.file = file;
		if (!file.exists())
			throw new FileNotFoundException(file.toString());
		
		if (!files.add(file))
			in = new StringReader("");
			//new Exception("Creating " + file).printStackTrace();
	}
	
	protected void open() throws FileNotFoundException {
		if (in == null) {
			in = new FileReader(file);
			//new Exception("Opening " + file).printStackTrace();
			//System.out.println("< " + file);
		}
	}
	
	public File getFile() {
		return file;
	}
	
	@Override
	public void mark(int readAheadLimit) throws IOException {
		open();
		in.mark(readAheadLimit);
	}
	@Override
	public boolean markSupported() {
		try {
			open();
			return in.markSupported();
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	@Override
	public boolean ready() throws IOException {
		open();
		return in.ready();
	}
	
	@Override
	public void reset() throws IOException {
		open();
		in.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		open();
		return in.skip(n);
	}
	
	@Override
	public String toString() {
		return getClass().getName() + "[file=" + getFile() +"]";
	}
	
	@Override
	public int hashCode() {
		return getFile().hashCode();
	}
	
	@Override
	public void close() throws IOException {
		if (in != null) {
			in.close();
			//System.out.println("> " + file);
		}
	}

	@Override
	public int read(CharBuffer target) throws IOException {
		open();
		return in.read(target);
	}
	
	@Override
	public int read(char[] cbuf) throws IOException {
		open();
		return in.read(cbuf);
	}
	
	@Override
	public int read() throws IOException {
		open();
		return in.read();
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		open();
		return in.read(cbuf, off, len);
	}
}
