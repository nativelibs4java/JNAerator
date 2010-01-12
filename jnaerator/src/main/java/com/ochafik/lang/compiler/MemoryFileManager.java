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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import com.ochafik.io.IOUtils;


public class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	public final Map<String, MemoryJavaFile> inputs = new HashMap<String, MemoryJavaFile>();
	public final Map<String, FileObject> outputs = new HashMap<String, FileObject>();

	public void writeJar(File outputJar, boolean outputSources, Map<String, File> additionalFiles) throws IOException {

		File jarDir = outputJar.getAbsoluteFile().getParentFile();
		if (!jarDir.isDirectory())
			jarDir.mkdirs();
		writeJar(new FileOutputStream(outputJar), outputSources, additionalFiles);
	}
	public void writeJar(OutputStream out, boolean outputSources, Map<String, File> additionalFiles) throws IOException {
		JarOutputStream jout = new JarOutputStream(out);
		if (outputSources)
			for (Map.Entry<String, MemoryJavaFile> e : inputs.entrySet()) {
//				if (e.getKey().contains(".class"))
//					continue;
				writeEntry(e.getKey(), e.getValue(), jout);
			}
		for (Map.Entry<String, FileObject> e : outputs.entrySet())
			writeEntry(e.getKey(), e.getValue(), jout);
		
		if (additionalFiles != null)
			for (Map.Entry<String, File> additionalFile : additionalFiles.entrySet()) {
				String path = additionalFile.getKey();
				if (path.startsWith("file:///"))
					path = path.substring("file:///".length());

				FileInputStream in = new FileInputStream(additionalFile.getValue());
				JarEntry e = new JarEntry(path);
				jout.putNextEntry(e);
				IOUtils.readWrite(in, jout);
				in.close();
				jout.closeEntry();
			}
		jout.close();
	}
	protected void writeEntry(String path, FileObject o, JarOutputStream jout) throws IOException {
		if (path.startsWith("/"))
			path = path.substring(1);
		if (path.startsWith("file:///"))
			path = path.substring("file:///".length());

		if (o instanceof MemoryFileObject) {
			MemoryFileObject mo = (MemoryFileObject)o;
			byte[] c = mo.getContent();
			if (c == null)
				return;
	
//			String path = mo.getPath();
//			if (path.startsWith("file:///"))
//				path = path.substring("file:///".length());
			JarEntry e = new JarEntry(path);
			jout.putNextEntry(e);
			jout.write(c);
			jout.closeEntry();
		} else if (o instanceof URLFileObject) {
			URLFileObject uo = (URLFileObject)o;
//			String path = uo.url.getFile();
//			if (path.startsWith("/"))
//				path = path.substring(1);
			
			JarEntry e = new JarEntry(path);
			jout.putNextEntry(e);
			InputStream in = uo.url.openStream();
			if (in == null)
				throw new FileNotFoundException(path);
			IOUtils.readWrite(in, jout);
			in.close();
			jout.closeEntry();
		} else
			throw new UnsupportedOperationException("Dunno how to deal with " + o);
	}
	public MemoryFileManager(JavaFileManager fm) {
		super(fm);
	}

	public MemoryJavaFile addSourceInput(String path, String content) {
		if (!path.startsWith("file:///"))
			path = "file:///" + path;
		
		MemoryJavaFile mjf = new MemoryJavaFile(path, content, JavaFileObject.Kind.SOURCE);
		inputs.put(path, mjf);
		return mjf;
	}
	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		return a.toString().equals(b.toString());
	}
	public Iterable<? extends JavaFileObject> getJavaFileObjects() {
		return new ArrayList<JavaFileObject>(inputs.values());
	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
//		System.out.println("getJavaFileForInput(className = " + className + ", location = " + location + ", kind = " + kind + ")");
		if (kind == JavaFileObject.Kind.SOURCE) {
			return inputs.get(className);
		}
		return super.getJavaFileForInput(location, className, kind);
	}

	public static String getFullPathForClass(String className, String extension) {
		return "file:///" + getSimplePathForClass(className, extension);
	}
	public static String getSimplePathForClass(String className, String extension) {
		return className.replace('.', '/') + "." + extension;
	}
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
//		System.out.println("getJavaFileForOutput(className = " + className + ", location = " + location + ", kind = " + kind + ")");
		MemoryJavaFile jo = null;
		if (kind == JavaFileObject.Kind.CLASS) {
			outputs.put(getFullPathForClass(className, "class"), jo = new MemoryJavaFile(getFullPathForClass(className, "class"), null, kind));
		} else if (kind == JavaFileObject.Kind.SOURCE) {
			inputs.put(getFullPathForClass(className, "java"), jo = new MemoryJavaFile(getFullPathForClass(className, "java"), null, kind));
		}

		return jo == null ? super.getJavaFileForInput(location, className, kind) : jo;
	}
	@Override
	public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
//		System.out.println("getFileForOutput(relativeName = " + relativeName + ")");
		if (relativeName.startsWith("file:///"))
			relativeName = relativeName.substring("file:///".length());
		
		FileObject out = outputs.get(relativeName);
		if (out == null) {
			out = new MemoryFileObject(relativeName, (String)null);
			outputs.put(relativeName, out);
		}
		return out;
	}
}