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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.FileObject;

public class MemoryFileObject implements FileObject {
	String path;
	byte[] content;

	public MemoryFileObject(String path, String content) {
		this.path = path;
		this.content = content == null ? null : content.getBytes();
	}
	public MemoryFileObject(String path, byte[] content) {
		this.path = path;
		this.content = content;
	}
	public String getPath() {
		return path;
	}
	public byte[] getContent() {
		return content;
	}
	public boolean delete() {
		content = null;
		return true;
	}
	public String getCharContent(boolean ignoreEncodingErrors) {
		return new String(content);
	}
	public long getLastModified() {
		return System.currentTimeMillis();
	}
	public String getName() {
		return getPath();
	}
	public InputStream	openInputStream() {
		if (content == null)
			return null;

		return new ByteArrayInputStream(content);
	}
	public OutputStream	openOutputStream() {
		return new ByteArrayOutputStream() {
			public void close() throws IOException {
				super.close();
				content = toByteArray();
			}
		};
	}
	public Reader openReader(boolean ignoreEncodingErrors) {
		InputStream in = openInputStream();
		return in == null ? null : new InputStreamReader(in);
	}
	public Writer openWriter() {
		OutputStream out = openOutputStream();
		return out == null ? null : new OutputStreamWriter(out);
	}
	public URI	toUri() {
		try {
			return new URI(//"file:" + 
					getPath());
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	@Override
	public String toString() {
		return getPath() + ":\n" + getCharContent(true);
	}
}