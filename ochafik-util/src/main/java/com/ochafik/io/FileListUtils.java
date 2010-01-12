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
package com.ochafik.io;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class FileListUtils {
	
	public static Collection<String> resolveShellLikeFileList(String d) {
		HashSet<String> v=new HashSet<String>();
		File f=new File(d);
		String name=f.getName();
		File par=f.getParentFile();
		if (par==null) {
			par=new File(".");
		}
		String fs[]=par.list();
		if (fs==null) return v;
		for (int i=0; i<fs.length;i++) {
			String fsi=fs[i];
			if (complies(fsi,name)) 
				v.add((new File(par,fsi)).toString());
		}
		return v;
	}
	public static boolean complies(String s, String model) {
		int len=model.length(),slen=s.length();
		if (len==0) {
			return slen==0;
		} else if (slen==0) {
			return model.equals("") || model.equals("*");
		} else {
			char c=model.charAt(0);
			if (c=='*') {
				String smod=model.substring(1);
				for (int i=0;i<slen;i++) {
					if (complies(s.substring(i),smod)) return true;
				}
			} else if (c=='?') {
				String smod=model.substring(1);
				if (complies(s.substring(1),smod)) return true;
			} else {
				return s.charAt(0)==c && complies(s.substring(1),model.substring(1));
			}
		}
		return false;
	}
}
