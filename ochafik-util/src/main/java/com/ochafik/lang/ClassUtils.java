package com.ochafik.lang;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class ClassUtils {
	public static URL getClassPath(Class<?> c) {
		String classFile = c.getName().replace('.', '/') + ".class";
		URL url = c.getClassLoader().getResource(classFile);
		if (url == null)
			return null;
		
		if (url.getProtocol().equals("jar")) {
			String urlFile = url.getFile();
			int i = urlFile.indexOf("!");
			if (i > 0) {
				try {
					URL jarURL = new URL(URLDecoder.decode(urlFile.substring(0, i), "UTF-8"));
					return jarURL;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		String urlString = url.toString();
		if (urlString.endsWith(classFile)) {
			try {
				return new URL(urlString.substring(0, urlString.length() - classFile.length()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
