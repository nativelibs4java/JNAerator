package org.anarres.cpp;

import java.io.*;

import junit.framework.Test;

import static org.anarres.cpp.Token.*;

public class JavaFileSystemTestCase extends BaseTestCase {

	public void testJavaFileSystem() throws Exception {
		JavaFileSystem	fs = new JavaFileSystem();
		VirtualFile		f;

		/* Anyone who has this file on their Unix box is messed up. */
		f = fs.getFile("/foo/bar baz");
		try {
			f.getSource();	/* drop on floor */
			assertTrue("Got a source for a non-file", f.isFile());
		}
		catch (FileNotFoundException e) {
			assertFalse("Got no source for a file", f.isFile());
		}

		/* We hope we have this. */
		f = fs.getFile("/usr/include/stdio.h");
		try {
			f.getSource();	/* drop on floor */
			System.out.println("Opened stdio.h");
			assertTrue("Got a source for a non-file", f.isFile());
		}
		catch (FileNotFoundException e) {
			System.out.println("Failed to open stdio.h");
			assertFalse("Got no source for a file", f.isFile());
		}

	}

}
