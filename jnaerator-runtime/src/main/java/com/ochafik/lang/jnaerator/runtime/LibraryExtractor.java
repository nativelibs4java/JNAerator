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
package com.ochafik.lang.jnaerator.runtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ochafik.net.URLUtils;
import com.sun.jna.Platform;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @see http://landonf.bikemonkey.org/static/soylatte/
 * @author ochafik
 *
 */
public class LibraryExtractor {
	static {
		if (Platform.isWindows()) {
			File ff;
			String path = System.getProperty("java.library.path");
			String winPath = "c:\\Windows\\" + (Platform.is64Bit() ? "SysWOW64" : "System32");
			if (path == null)
				path = winPath;
			else
				path = path + ";" + winPath;
			System.setProperty("java.library.path", path);
		}
	}
	public static String getCurrentOSAndArchString() {
		String os = System.getProperty("os.name"), arch = System.getProperty("os.arch");

		if (os.equals("Mac OS X")) {
			os = "darwin";
			arch = "fat";
			//arch = Platform.is64Bit() ? "64" : "32";
		} else if (os.startsWith("Windows")) {
			return "win" + (Platform.is64Bit() ? "64" : "32");
		} else if (os.matches("SunOS|Solaris"))
			os = "solaris";
		
		return os + "-" + arch;
	}
	public static String getLibraryPath(String libraryName, boolean extractAllLibraries, Class<?> cl) {
		try {
			String customPath = System.getProperty("library." + libraryName);
			if (customPath == null)
				customPath = System.getenv(libraryName.toUpperCase() + "_LIBRARY");
			if (customPath != null) {
				File f = new File(customPath);
				if (!f.exists())
					System.err.println("Library file '" + customPath + "' does not exist !");
				else
					return f.getAbsolutePath();
			}
			//ClassLoader cl = LibraryExtractor.class.getClassLoader();
			String prefix = "(?i)" + (Platform.isWindows() || Platform.isWindowsCE() ? "" : "lib") + libraryName + "[^A-Za-z_].*";
			String libsuffix = "(?i).*\\.(so|dll|dylib|jnilib)";
			String othersuffix = "(?i).*\\.(pdb)";
			
			URL sourceURL = null;
			List<URL> otherURLs = new ArrayList<URL>();
			

			String arch = getCurrentOSAndArchString();
			//System.out.println("libURL = " + libURL);
			List<URL> list = URLUtils.listFiles(URLUtils.getResource(cl, "libraries/" + arch), null);
			if (list.isEmpty()) {
				for (URL u : URLUtils.listFiles(URLUtils.getResource(cl, "libraries"), null)) {
					String f = u.getFile();
					int i = f.lastIndexOf('/');
					if (i >= 0)
						f = f.substring(i + 1);
					if (arch.startsWith(f)) {
						list = URLUtils.listFiles(u, null);
						break;
					}
				}
				
			}
			for (File f : new File(".").listFiles())
				if (f.isFile())
					list.add(f.toURI().toURL());
			
			for (URL url : list) {
				String fileName = new File(url.toString()).getName();
				boolean pref = fileName.matches(prefix), suff = fileName.matches(libsuffix); 
				if (pref && suff)
					sourceURL = url;
				else if (suff || fileName.matches(othersuffix))
					otherURLs.add(url);
			}
			List<File> files = new ArrayList<File>();
			if (extractAllLibraries) {
				for (URL url : otherURLs)
					files.add(extract(url));
			}

			if (System.getProperty("javawebstart.version") != null) {
				if (Platform.isWindows()) {
					//File f = new File("c:\\Windows\\" + (Platform.is64Bit() ? "SysWOW64\\" : "System32\\") + libraryName + ".dll");
					File f = new File("c:\\Windows\\" + "System32\\" + libraryName + ".dll");
					if (f.exists())
						return f.toString();
				} else if (Platform.isMac()) {
					File f = new File("/System/Library/Frameworks/" + libraryName + ".framework/" + libraryName);
					if (f.exists())
						return f.toString();
				}
			}


			if (sourceURL == null)
				return libraryName;
			else {
				File file = extract(sourceURL);
				files.add(file);
				
				int lastSize;
				do {
					lastSize = files.size();
					for (Iterator<File> it = files.iterator(); it.hasNext();) {
						File f = it.next();
						if (!f.getName().matches(libsuffix))
							continue;
						
						try {
							System.load(f.toString());
							it.remove();
						} catch (Throwable ex) {
							System.err.println("Loading " + f.getName() + " failed (" + ex + ")");
						}
					}
				} while (files.size() < lastSize);
				
				return file.getCanonicalPath();
	        }
		} catch (Throwable ex) {
			System.err.println("ERROR: Failed to extract library " + libraryName);
			ex.printStackTrace();
			return libraryName;
		}
	}
	private static File extract(URL url) throws IOException {
		File localFile;
		if ("file".equals(url.getProtocol()))
			localFile = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
		else {
			File f = new File(System.getProperty("user.home"));
			f = new File(f, ".jnaerator");
			f = new File(f, "extractedLibraries");
			if (!f.exists())
				f.mkdirs();
			
			localFile = new File(f, new File(url.getFile()).getName());
			URLConnection c = url.openConnection();
			if (localFile.exists() && localFile.lastModified() > c.getLastModified()) {
				c.getInputStream().close();
			} else {
				System.out.println("Extracting " + url);
				InputStream in = c.getInputStream();
				OutputStream out = new FileOutputStream(localFile);
				int len;
				byte[] b = new byte[1024];
				while ((len = in.read(b)) > 0)
					out.write(b, 0, len);
				out.close();
				in.close();
			}
		}
		return localFile;
	}
	public static void loadLibrary(String libraryName, boolean extractAllLibraries, Class<?> cl) {
		System.loadLibrary(getLibraryPath(libraryName, extractAllLibraries, cl));
	}

    public static boolean shouldTraceCalls(String libraryName) {
        return  "true".equals(System.getProperty("jna.traceCalls")) || "true".equals(System.getProperty(libraryName.toLowerCase() + ".traceCalls"));
    }
    public static final Object getTracingLibrary(final Object original, Class libraryClass) {
        try {
            final String pref = "[" + libraryClass.getSimpleName() + "]";
            InvocationHandler handler = new InvocationHandler() {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    StringBuilder b = new StringBuilder(pref);
                    b.append(method.getDeclaringClass().getName() + "." + method.getName() + "(");

                    for (int i = 0; i < args.length; i++) {
                        Object arg = args[i];
                        if (i != 0)
                            b.append(", ");
                        b.append(toString(arg));
                    }
                    b.append(")");
                    System.err.print(b);
                    Object ret = method.invoke(original, args);
                    if (method.getReturnType().equals(Void.class))
                        return null;

                    System.err.println(" => " + toString(ret));
                    return ret;
                }

                String toString(Object arg) {
                    if (arg instanceof Object[])
                        return Arrays.toString((Object[])arg);
                    else
                        return String.valueOf(arg);
                }

            };
            Class proxyClass = Proxy.getProxyClass(libraryClass.getClassLoader(), libraryClass);
            return proxyClass.getConstructor(InvocationHandler.class).newInstance(handler);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create trace library");
        }
    }
    public static final Object getLibrary(String name, String path, final Class libraryClass) {
        Object original = com.sun.jna.Native.loadLibrary(path, libraryClass, MangledFunctionMapper.DEFAULT_OPTIONS);
        return shouldTraceCalls(name) ?
            LibraryExtractor.getTracingLibrary(original, libraryClass) : original;
    }
}
