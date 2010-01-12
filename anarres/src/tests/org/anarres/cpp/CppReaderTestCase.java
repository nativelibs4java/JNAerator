package org.anarres.cpp;

import java.util.Collections;

import java.io.StringReader;
import java.io.BufferedReader;

import junit.framework.Test;

public class CppReaderTestCase extends BaseTestCase implements Test {

	private void testCppReader(String in, String out)
						throws Exception {
		System.out.println("Testing " + in + " => " + out);
		StringReader	r = new StringReader(in);
		CppReader		p = new CppReader(r);
		p.getPreprocessor().setSystemIncludePath(
				Collections.singletonList("src/input")
					);
		p.getPreprocessor().getFeatures().add(Feature.LINEMARKERS);
		BufferedReader	b = new BufferedReader(p);

		String	line;
		while ((line = b.readLine()) != null) {
			System.out.println(" >> " + line);
		}
	}

	public void testCppReader()
						throws Exception {
		testCppReader("#include <test0.h>\n", "ab");
	}

}
