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

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import javax.tools.*;
import javax.tools.Diagnostic.Kind;

import com.ochafik.io.IOUtils;
import com.ochafik.util.listenable.Adapter;
import com.ochafik.util.string.RegexUtils;
import com.ochafik.util.string.StringUtils;

public class CompilerUtils {
	public static class CompilationError extends IOException {
		public final String compilerClass;
		//public final String bootclasspath;
		public final List<Diagnostic<? extends JavaFileObject>> diagnostics;
		public final Map<String, MemoryJavaFile> inputs;
		private CompilationError(String text, List<Diagnostic<? extends JavaFileObject>> diagnostics, Map<String, MemoryJavaFile> inputs, String compilerClass/*, String bootclasspath*/) {
			super(text);
			this.diagnostics = diagnostics;
			this.inputs = inputs;
			this.compilerClass = compilerClass;
			//this.bootclasspath = bootclasspath;
		}
		public static void throwErrors(List<Diagnostic<? extends JavaFileObject>> diagnostics, Map<String, MemoryJavaFile> inputs, String compilerClass/*, String bootclasspath*/) throws CompilationError, IOException {
			List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<Diagnostic<? extends JavaFileObject>>(diagnostics.size());
			StringBuilder sb = new StringBuilder();

			for (final Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
				if (diagnostic == null)
					continue;
				if (diagnostic.getKind() == Kind.ERROR) {
					errors.add(diagnostic);
					sb.append("Error in " + diagnostic.getSource().toUri() + " at line " + diagnostic.getLineNumber() + ", col " + diagnostic.getColumnNumber() + " :\n\t" + diagnostic.getMessage(Locale.getDefault()) + "\n");//.toUri());
					sb.append(RegexUtils.regexReplace(Pattern.compile("\n"), "\n" +  diagnostic.getSource().getCharContent(true), new Adapter<String[], String>() {
						int line = 0;

						@Override
						public String adapt(String[] value) {
							line++;
							return "\n" + line + ":" + (diagnostic.getLineNumber() == line ? ">>>" : "") +"\t\t";
						}
					}) + "\n");
				}
				//System.out.println("Error on line " + diagnostic.getLineNumber() + ":" + diagnostic.getColumnNumber() + " in " + (diagnostic.getSource() == null ? "<unknown source>" : diagnostic.getSource().getName()) + ": " + diagnostic.getMessage(Locale.getDefault()));
			}
			if (errors.isEmpty())
				return;

			throw new CompilationError(sb.toString(), errors, inputs, compilerClass/*, bootclasspath*/);
		}
	}
	public static String getClassPath(Class<?> c, File cacheDirectory) throws MalformedURLException, IOException {

		URL resource = c.getResource(c.getSimpleName() + ".class");
		if (resource != null) {
			String resstr = resource.toString();
//			if (resstr.contains("Prog/"))
//				resstr = "jar:http://ochafik.free.fr/Java/jnaerator.jar!/...";
			
			if (resstr.matches("jar:.*!.*"))
				resstr = resstr.substring("jar:".length(), resstr.indexOf("!"));
			else {
				String p = '/' + c.getName().replace('.', '/') + ".class";
				if (resstr.endsWith(p))
					resstr = resstr.substring(0, resstr.length() - p.length());
			}
			return getLocalFile(new URL(resstr), cacheDirectory).toString();
		}
		/*
		if (resource != null) {
			String resstr = resource.toString();
			if (resstr.matches("jar:file:.*!.*"))
				return resstr.substring("jar:file:".length(), resstr.indexOf("!"));
			else if (resstr.matches("jar:http:.*!.*"))
				return resstr.substring("jar:".length(), resstr.indexOf("!"));
			else {
				String p = '/' + c.getName().replace('.', '/') + ".class";
				if (resstr.endsWith(p))
					return resstr.substring(0, resstr.length() - p.length());
			}
		}*/
		return null;
	}
	public static Set<String> getClassPaths(File cacheDirectory, Class<?>... cs) throws MalformedURLException, IOException {
		Set<String> ret = new TreeSet<String>();
		for (Class<?> c : cs) {
			String cp ;
			if (c == null || (cp = getClassPath(c, cacheDirectory)) == null)
				continue;
			ret.add(cp);
		}
		return ret;
	}
	static Map<String, File> localURLCaches = new HashMap<String, File>();
	static File getLocalFile(URL remoteFile, File cacheDirectory) throws IOException {
		if ("file".equals(remoteFile.getProtocol()))
			return new File(URLDecoder.decode(remoteFile.getFile(), "utf-8"));
		
		String remoteStr = remoteFile.toString();
		File f = localURLCaches.get(remoteStr);
		if (f == null) {
			String fileName = new File(remoteStr).getName();
			URLConnection con = null;
			try {
				con = remoteFile.openConnection();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (cacheDirectory != null) {
				f = new File(cacheDirectory, fileName);
				if (f.exists() && (con == null || f.lastModified() > con.getLastModified())) {
					System.out.println("Reusing cached file " + f);
					if (con != null)
						con.getInputStream().close();
					return f;
				}
			} else {
				f = File.createTempFile(fileName, ".jar");
				f.deleteOnExit();
			}
			System.out.print("Downloading file " + remoteFile + " to " + f);
			
			InputStream in = new BufferedInputStream(remoteFile.openStream());
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
				try {
					//System.out.print("Downloading file '" + remoteStr + "'...");
					long length = IOUtils.readWrite(in, out);
					System.out.println(" OK (" + length + " bytes)");
					localURLCaches.put(remoteStr, f.getAbsoluteFile());
				} finally {
					out.close();
				}
			} finally {
				in.close();
			}
		}
		return f;
	}
	public static void compile(JavaCompiler compiler, MemoryFileManager fileManager, DiagnosticCollector<JavaFileObject> diagnostics, String sourceCompatibility, File cacheDirectory, Class<?>...classpathHints) throws MalformedURLException, IOException {
		//JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		//System.out.println("compiler = " + (compiler == null ? "<none found>" : compiler.getClass().getName()));
		Set<String> bootclasspaths = getClassPaths(cacheDirectory, classpathHints);
		bootclasspaths.addAll(getClassPaths(cacheDirectory, String.class));
		String bootclasspath = StringUtils.implode(bootclasspaths, File.pathSeparator);
		//System.out.println("bootclasspath = " + bootclasspath);
		Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects();  
		List<String> options = sourceCompatibility == null ? null : Arrays.asList(
			"-target", sourceCompatibility, 
			"-source", sourceCompatibility,
			"-bootclasspath", bootclasspath, //"/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Classes/classes.jar",//bootclasspath,
			"-classpath", bootclasspath //"/Users/ochafik/Prog/Java/bin/jnaerator.jar"//
				//"http://ochafik.free.fr/Java/jnaerator.jar"//bootclasspath
		);  
//		DebugUtils.println(fileManager.inputs.values());
		compiler.getTask(null, fileManager, diagnostics, options, null, fileObjects).call();
		
//		for (final Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
//			if (diagnostic == null)
//				continue;
//			//diagnostic.getKind()
//			//System.out.format("Error on line %d in %d%n", diagnostic.getLineNumber(), diagnostic.getSource());//.toUri());
//			if (diagnostic.getKind() == Kind.ERROR) {
//				System.err.println("\n" +  diagnostic.getSource().toUri() + ":");
//				System.err.println(RegexUtils.regexReplace(Pattern.compile("\n"), "\n" +  diagnostic.getSource().getCharContent(true), new Adapter<String[], String>() {
//					int line = 0;
//
//					@Override
//					public String adapt(String[] value) {
//						line++;
//						return "\n" + line + ":" + (diagnostic.getLineNumber() == line ? ">>>" : "") +"\t\t";
//					}
//				}));
//			}
////				System.out.println("Error on line " + diagnostic.getLineNumber() + ":" + diagnostic.getColumnNumber() + " in " + (diagnostic.getSource() == null ? "<unknown source>" : diagnostic.getSource().getName()) + ": " + diagnostic.getMessage(Locale.getDefault()));
//		}
	}
	
	public static JavaCompiler getJavaCompiler(boolean preferJavac) throws FileNotFoundException {
		JavaCompiler compiler;
		if (preferJavac) {
			compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler != null)
				return compiler;
		}
		try {
			compiler = (JavaCompiler)Class.forName("org.eclipse.jdt.internal.compiler.tool.EclipseCompiler").newInstance();
		} catch (Exception e) {
			compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null)
				throw new FileNotFoundException("No Java compiler found (not run from JDK, no Eclipse Compiler in classpath)");
		}
		return compiler;
	}
	public static void main2(String[] args) {
		try {
			String jarOut = args.length == 0 ? "out.jar" : args[0];

			JavaCompiler compiler = getJavaCompiler(false);
			
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			MemoryFileManager fileManager = new MemoryFileManager(compiler.getStandardFileManager(diagnostics, null, null));
			fileManager.addSourceInput("test/Main.java", "package test; public class Main { }");
			fileManager.close();

			compile(compiler, fileManager, diagnostics, null, null);
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				//diagnostic.getKind()
				//System.out.format("Error on line %d in %d%n", diagnostic.getLineNumber(), diagnostic.getSource());//.toUri());
				System.out.format("Error on line " + diagnostic.getLineNumber() + ":" + diagnostic.getLineNumber() + " in " + diagnostic.getSource());//.toUri());
			}

			boolean outputSources = true;
			System.out.println("Writing " + jarOut + (outputSources ? " with" : " without") + " sources");
			fileManager.writeJar(new FileOutputStream(jarOut), outputSources, null);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

