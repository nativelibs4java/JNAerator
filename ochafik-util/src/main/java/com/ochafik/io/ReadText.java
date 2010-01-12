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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLSession;

import com.ochafik.util.string.RegexUtils;
public class ReadText { 
	public static final String readText(Reader in) throws IOException {
		int len;
		char ch[]=new char[1024];
		StringBuffer b=new StringBuffer();
		while ((len=in.read(ch))>0) b.append(ch,0,len);
		return b.toString();
	}
	public static final String readText(File f) {
		if (f == null)
			return null;

		try {
			BufferedReader in=new BufferedReader(new FileReader(f));
			String l;
			java.lang.StringBuffer buff=new java.lang.StringBuffer((int)f.length());
			boolean first = true;
			while ((l=in.readLine())!=null) {
				if (first)
					first = false;
				else
					buff.append('\n');
				buff.append(l);
			}
			return buff.toString();
		} catch (Exception except) { 
			except.printStackTrace();
			return null;
		}
	}
	public static final void writeProperties(Properties properties, File file) throws IOException {
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) parentFile.mkdirs();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		properties.store(out, file.getName());
		out.close();
	}
	public static final Properties readProperties(File file, boolean failIfNotExists) throws IOException {
		Properties properties = new Properties();
		if (!file.exists()) {
			if (failIfNotExists) {
				return null;
			}
		} else {
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			properties.load(in);
			in.close();
		}
		return properties;
	}
	private static final Pattern charsetPattern = Pattern.compile(";\\s*charset\\s*=\\s*([\\w-_]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	public static final String readText(URL u) throws IOException {
		if (u == null) return null;

		String encoding = null;
		URLConnection con = u.openConnection();
//		if (con instanceof HttpsURLConnection) {
//			((HttpsURLConnection)con).setHostnameVerifier(new HostnameVerifier() {
//				public boolean verify(String hostname, SSLSession session) {
//					return true;
//				}
//			});
//		}
		if (con instanceof HttpURLConnection) {
			String contentType = con.getContentType();
			encoding = RegexUtils.findFirst(contentType, charsetPattern, 1);
		}
		InputStream in = con.getInputStream();
		String text = readText(in, encoding);
		in.close();
		return text;
	}
	static Pattern[] embeddedCharsetPatterns = new Pattern[] {
		Pattern.compile("CONTENT\\s*=\\s*\"text/\\w+;\\s*charset\\s*=\\s*([^\";\\s]+)\\s*\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
		Pattern.compile("<?\\s*xml\\s+[^>]*encoding\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
	};
	private static String readText(InputStream in, String encoding) throws IOException {
		if (in == null)
			return null;

		if (encoding == null) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			IOUtils.readWrite(in, bout);
			byte[] bytes = bout.toByteArray();
			String ascii = new String(bytes, "ascii");
			
			for (Pattern charsetPat : embeddedCharsetPatterns) {
				encoding = RegexUtils.findFirst(ascii, charsetPat, 1);
				if (encoding != null)
					break;
			}
			if (encoding != null) {
				try {
					return new String(bytes, encoding);
				} catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
				}
			}
			return new String(bytes);
		} else {
		
			BufferedReader rin = new BufferedReader(encoding == null ? new InputStreamReader(in) : new InputStreamReader(in, encoding));
			String l;
			StringBuffer buff=new StringBuffer();
			boolean first = true;
			while ((l = rin.readLine()) != null) {
				if (first)
					first = false;
				else
					buff.append('\n');
				buff.append(l);
			}
			return buff.toString();
		}
	}
	public static final String readText(InputStream in) throws IOException {
		return readText(in, null);
	}
	public static final List<String> readLines(String s) throws IOException {
		return readLines(new FileReader(s));
	}
	public static final List<String> readLines(URL u) throws IOException {
		InputStream in = u.openStream();
		List<String> r = readLines(in);
		in.close();
		return r;
	}
	public static final List<String> readLines(InputStream in) throws IOException {
		if (in == null)
			return null;
		List<String> r = readLines(new InputStreamReader(in));
		in.close();
		return r;
	}
	public static final List<String> readLines(Reader r) throws IOException {
		List<String> v=new ArrayList<String>();
		BufferedReader in=new BufferedReader(r);
		String l;
		while ((l=in.readLine())!=null) //if (!(l=l.trim()).equals("")) 
			v.add(l);
		return v;
	}
	public static final String[] readLinesArray(String s) throws IOException {
		List<String> v=readLines(s);
		String ret[]=new String[v.size()];
		int i=0;
		for (String line : v) {
			ret[i++]=line;
		}
		return ret;
	}
}
