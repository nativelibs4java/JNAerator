package org.anarres.cpp;

import java.io.*;

import junit.framework.Test;

import static org.anarres.cpp.Token.*;

public class PreprocessorTestCase extends BaseTestCase {
	private OutputStreamWriter	writer;
	private Preprocessor		p;

	public void setUp() throws Exception {
		final PipedOutputStream	po = new PipedOutputStream();
		writer = new OutputStreamWriter(po);

		p = new Preprocessor();
		p.addInput(
			new LexerSource(
				new InputStreamReader(
					new PipedInputStream(po)
				),
				true
			)
		);
	}

	private static class I {
		private String	t;
		public I(String t) {
			this.t = t;
		}
		public String getText() {
			return t;
		}
		public String toString() {
			return getText();
		}
	}

	private static I I(String t) {
		return new I(t);
	}

/*
 * When writing tests in this file, remember the preprocessor
 * stashes NLs, so you won't see an immediate NL at the end of any
 * input line. You will see it right before the next nonblank on
 * the following input line.
 */

	public void testPreprocessor() throws Exception {
		/* Magic macros */
		testInput("line = __LINE__\n",
			I("line"), WHITESPACE, '=', WHITESPACE, INTEGER
			/*, NL - all nls deferred so as not to block the reader */
		);
		testInput("file = __FILE__\n", NL,	/* from before, etc */
			I("file"), WHITESPACE, '=', WHITESPACE, STRING
		);

		/* Simple definitions */
		testInput("#define A a /* a defined */\n", NL);
		testInput("#define B b /* b defined */\n", NL);
		testInput("#define C c /* c defined */\n", NL);

		/* Expansion of arguments */
		testInput("#define EXPAND(x) x\n", NL);
		testInput("EXPAND(a)\n", NL, I("a"));
		testInput("EXPAND(A)\n", NL, I("a"));

		/* Stringification */
		testInput("#define _STRINGIFY(x) #x\n", NL);
		testInput("_STRINGIFY(A)\n", NL, "A");
		testInput("#define STRINGIFY(x) _STRINGIFY(x)\n", NL);
		testInput("STRINGIFY(b)\n", NL, "b");
		testInput("STRINGIFY(A)\n", NL, "a");

		/* Concatenation */
		testInput("#define _CONCAT(x, y) x ## y\n", NL);
		testInput("_CONCAT(A, B)\n", NL, I("AB"));
		testInput("#define A_CONCAT done_a_concat\n", NL);
		testInput("_CONCAT(A, _CONCAT(B, C))\n", NL,
			I("done_a_concat"), '(', I("b"), ',', WHITESPACE, I("c"), ')'
		);
		testInput("#define CONCAT(x, y) _CONCAT(x, y)\n", NL);
		testInput("CONCAT(A, CONCAT(B, C))\n", NL, I("abc"));
		testInput("#define _CONCAT3(x, y, z) x ## y ## z\n", NL);
		testInput("_CONCAT3(a, b, c)\n", NL, I("abc"));
		testInput("_CONCAT3(A, B, C)\n", NL, I("ABC"));

/* Redefinitions, undefinitions. */
testInput("#define two three\n", NL);
testInput("one /* one */\n", NL, I("one"), WHITESPACE, CCOMMENT);
testInput("#define one two\n", NL);
testInput("one /* three */\n", NL, I("three"), WHITESPACE, CCOMMENT);
testInput("#undef two\n", NL);
testInput("#define two five\n", NL);
testInput("one /* five */\n", NL, I("five"), WHITESPACE, CCOMMENT);
testInput("#undef two\n", NL);
testInput("one /* two */\n", NL, I("two"), WHITESPACE, CCOMMENT);
testInput("#undef one\n", NL);
testInput("#define one four\n", NL);
testInput("one /* four */\n", NL, I("four"), WHITESPACE, CCOMMENT);
testInput("#undef one\n", NL);
testInput("#define one one\n", NL);
testInput("one /* one */\n", NL, I("one"), WHITESPACE, CCOMMENT);

		/* Variadic macros. */
		testInput("#define var(x...) a x b\n", NL);
		testInput("var(e, f, g)\n", NL,
			I("a"), WHITESPACE,
			I("e"), ',', WHITESPACE,
			I("f"), ',', WHITESPACE,
			I("g"), WHITESPACE,
			I("b")
		);

		testInput("#define _Widen(x) L ## x\n", NL);
		testInput("#define Widen(x) _Widen(x)\n", NL);
		testInput("#define LStr(x) _Widen(#x)\n", NL);
		testInput("LStr(x);\n", NL, I("L"), "x");

		writer.close();

		Token	t;
		do {
			t = p.token();
			System.out.println("Remaining token " + t);
		} while(t.getType() != EOF);
	}

	private void testInput(String in, Object... out)
						throws Exception {
		System.out.print("Input: " + in);
		writer.write(in);
		writer.flush();
		for (int i = 0; i < out.length; i++) {
			Token	t = p.token();
			System.out.println(t);
			Object	v = out[i];
			if (v instanceof String) {
				if (t.getType() != STRING)
					fail("Expected STRING, but got " + t);
				assertEquals((String)v, (String)t.getValue());
			}
			else if (v instanceof I) {
				if (t.getType() != IDENTIFIER)
					fail("Expected IDENTIFIER " + v + ", but got " + t);
				assertEquals( ((I)v).getText(), (String)t.getText());
			}
			else if (v instanceof Character)
				assertEquals( (int)((Character)v).charValue(), t.getType());
			else if (v instanceof Integer)
				assertEquals( ((Integer)v).intValue(), t.getType());
			else
				fail("Bad object " + v.getClass());
		}
	}
}
