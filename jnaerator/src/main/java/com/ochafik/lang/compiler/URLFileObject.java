package com.ochafik.lang.compiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.tools.FileObject;

import com.ochafik.io.ReadText;

public class URLFileObject implements FileObject {
	URL url;

	public URLFileObject(URL url) {
		this.url = url;
	}
	
	public String getPath() {
		return url.getFile();
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return ReadText.readText(url);
	}

	@Override
	public long getLastModified() {
		try {
			URLConnection con = url.openConnection();
			long lastModified = con.getLastModified();
			con.getInputStream().close();
			return lastModified;
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public String getName() {
		return new File(url.getFile()).getName();
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return url.openStream();
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return null;
	}

	@Override
	public Reader openReader(boolean arg0) throws IOException {
		return new InputStreamReader(openInputStream());
	}

	@Override
	public Writer openWriter() throws IOException {
		return null;
	}

	@Override
	public URI toUri() {
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
}
