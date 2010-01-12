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
package com.ochafik.lang.jnaerator.parser;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

import com.ochafik.io.ReadText;
import com.ochafik.junit.ParameterizedWithDescription;
import com.ochafik.lang.SyntaxUtils;
import com.ochafik.lang.grammar.DummyDebugEventListener;
import com.ochafik.lang.jnaerator.JNAeratorConfig;
import com.ochafik.lang.jnaerator.Result;

/*
 include com/ochafik/lang/jnaerator/parser/ObjCppTest.mm
 */
@SuppressWarnings("unused")
@RunWith(Parameterized.class)
// @RunWith(Parameterized.class)
public class ObjCppParsingTest {
	static final String TEST_FILE = "com/ochafik/lang/jnaerator/parser/ObjCppTest.mm";

	enum TestOption {
		ParseAndPrettyPrint, ParseOnly, ParseMustFail
	};

	String string;
	TestOption testOption;

	public ObjCppParsingTest(String string, TestOption testOption) {
		this.string = string;
		this.testOption = testOption;
	}

	@Test
	public void test() throws RecognitionException, IOException {
		PrintStream originalOut = System.out;
		PrintStream originalErr = System.err;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(bout);
		System.setOut(pout);
		System.setErr(pout);

		boolean ok = false;
		SourceFile sourceFile = null;

		try {

			switch (testOption) {
			case ParseMustFail:
				try {
					sourceFile = newParser(string).sourceFile();// .sourceFile;
					assertTrue("Expression should not have been parsed : "
							+ string, false);
				} catch (Throwable t) {
					ok = true;
				}
				break;
			case ParseOnly:
			case ParseAndPrettyPrint:
				sourceFile = newParser(string).sourceFile();
				assertNotNull(string, sourceFile);
				if (testOption == TestOption.ParseAndPrettyPrint) {
					if (!SyntaxUtils.equal(string, sourceFile == null ? null
							: sourceFile.toString()))
						sourceFile = newParser(string).sourceFile();

					assertEqualsNewLineAware(string, string.trim(),
							sourceFile == null ? null : sourceFile.toString()
									.trim());
				}
				ok = true;
				break;
			}

			for (Declaration decl : sourceFile.getDeclarations()) {
				if (decl == null)
					continue;

				String declStr = decl.toString();
				Declaration clo = decl.clone();
				assertNotNull("Null clone !", clo);
				String cloStr = clo.toString();
				if (!SyntaxUtils.equal(declStr, cloStr)) {
					// go here again to debug
					clo = decl.clone();
				}
				assertEquals("Clone failed !", declStr, cloStr);
			}
		} finally {
			pout.flush();
			System.setOut(originalOut);
			System.setErr(originalErr);
			if (!ok) {
                                int i = string.trim().length(), j = String.valueOf(sourceFile).trim().length();
                                System.out.println("IN: " + i + " OUT: " + j );
				System.out.println("IN:\n" + string.trim());
				try {
					System.out.println("OUT:\n" + String.valueOf(sourceFile).trim());
					System.out.write(bout.toByteArray());
					System.out.println();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}

	}
        static String lineAware(String s) {
            return s.replaceAll("\n\r", "\n").replace("\r", "\n").replaceAll("\\s+", " ");
        }
        public static void assertEqualsNewLineAware(String message, String a, String b) {
            assertEquals(message, lineAware(a), lineAware(b));
        }

	@Parameters
	public static List<Object[]> readDataFromFile() throws IOException {
		List<String> lines = ReadText.readLines(ObjCppParsingTest.class.getClassLoader().getResource(TEST_FILE));
		TestOption testOption = TestOption.ParseAndPrettyPrint;
		List<Object[]> data = new ArrayList<Object[]>();

		StringBuilder b = new StringBuilder();
		for (String line : lines) {
			String trl = line.trim().replaceAll("\\s+", " ");
			if (trl.equals("#pragma reversible"))
				testOption = TestOption.ParseAndPrettyPrint;
			else if (trl.equals("#pragma parse"))
				testOption = TestOption.ParseOnly;
			else if (trl.equals("#pragma fail"))
				testOption = TestOption.ParseMustFail;
			else if (trl.startsWith("#pragma "))
				System.err.println("Unknown #pragma : " + line);
			else if (trl.startsWith("#")) {
				// skip
			} else if (trl.equals("--")) {
				if (b.length() > 0) {
					data.add(new Object[] { b.toString(), testOption });
					b.delete(0, b.length());
				}
			} else {
				if (b.length() > 0)
					b.append('\n');
				b.append(line);
			}
		}
		if (b.length() > 0)
			data.add(new Object[] { b.toString(), testOption });

		return data;
	}

	public static ObjCppParser newParser(String s) throws IOException {
		Result result = new Result(new JNAeratorConfig(), null, null);
		ObjCppParser parser = new ObjCppParser(new CommonTokenStream(new ObjCppLexer(
				new ANTLRReaderStream(new StringReader(s))))
		// , new DummyDebugEventListener()
		);
		parser.objCParserHelper = result.typeConverter;
		return parser;
	}
}
