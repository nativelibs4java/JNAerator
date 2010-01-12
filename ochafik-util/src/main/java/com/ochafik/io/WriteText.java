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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
public class WriteText {
	public static final void writeText(String t,File f) throws IOException {
		File p = f.getParentFile();
		if (p != null && !p.exists())
			p.mkdirs();
		
		PrintWriter out=new PrintWriter(new FileWriter(f));
		String l;
		BufferedReader in=new BufferedReader(new StringReader(t));
		while ((l=in.readLine())!=null) out.println(l);
		out.close();
		in.close();
	}
}
