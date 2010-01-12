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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.ochafik.util.progress.ProgressModel;


public class IOUtils {

	public static final long readWrite(InputStream in, OutputStream out) throws IOException {
		byte[] b=new byte[4096];
		int len;
		long total=0;
		while ((len=in.read(b))>0) {
			out.write(b, 0, len);
			total+=len;
		}
		return total;
	}
	
	public static final long readWrite(InputStream in, OutputStream out, int maxLen) throws IOException {
		int bLen = 4096;
		byte[] b=new byte[bLen];
		int len;
		long total=0;
		
		int allowed = bLen <= maxLen ? bLen : maxLen;  
		while (allowed > 0 && (len=in.read(b, 0, allowed)) > 0) {
			out.write(b, 0, len);
			total+=len;
			maxLen -= len;
			allowed = bLen <= maxLen ? bLen : maxLen;
		}
		return total;
	}
	

	public static final long readWrite(InputStream in, OutputStream out, ProgressModel progressModel) throws IOException {
		byte[] b=new byte[1024];
		int len;
		long total=0;
		while ((len=in.read(b))>0) {
			out.write(b, 0, len);
			total+=len;
			progressModel.addProgress(len);
		}
		return total;
	}

	public static void readWrite(Reader in, Writer out) throws IOException {
		char[] b = new char[1024];
		int len;
		long total=0;
		while ((len = in.read(b))>0) {
			out.write(b, 0, len);
			total+=len;
		}
	}
	
}
