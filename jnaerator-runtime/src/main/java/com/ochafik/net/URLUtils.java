package com.ochafik.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.ochafik.util.listenable.Filter;

public class URLUtils {

	public static URL getResource(Class<?> cl, String path) throws IOException {
		String clp = cl.getName().replace('.', '/') + ".class";
		URL clu = cl.getClassLoader().getResource(clp);
		String s = clu.toString();
		if (s.endsWith(clp))
			return new URL(s.substring(0, s.length() - clp.length()) + path);
		
		if (s.startsWith("jar:")) {
			String[] ss = s.split("!");
			return new URL(ss[1] + "!/" + path);
		}
		return null;
	}
	
	public static List<URL> listFiles(URL directory, Filter<String> pathAccepter) throws IOException {
		List<URL> ret = new ArrayList<URL>();
		String s = directory.toString();
		if (s.startsWith("jar:")) {
			String[] ss = s.substring("jar:".length()).split("!");
			String path = ss[1];
			URL target = new URL(ss[0]);
			InputStream tin = target.openStream();
			try {
				JarInputStream jin = new JarInputStream(tin);
				JarEntry je;
				while ((je = jin.getNextJarEntry()) != null) {
					String p = "/" + je.getName();
					if (p.startsWith(path) && p.indexOf('/', path.length() + 1) < 0)
						if (pathAccepter == null || pathAccepter.accept(path))
							ret.add(new URL("jar:" + target + "!" + p));
				}
			} finally {
				tin.close();
			}
		} else if (s.startsWith("file:")) {
			File f = new File(directory.getFile());
			File[] ffs = f.listFiles();
			if (ffs != null)
				for (File ff : ffs)
					if (pathAccepter == null || pathAccepter.accept(ff.toString()))
						ret.add(ff.toURI().toURL());
		} else 
			throw new IOException("Cannot list contents of " + directory);
		
		return ret;
	}
}
