/*
 * Anarres C Preprocessor
 * Copyright (c) 2007-2008, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.anarres.cpp;

import java.io.File;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import java.util.Set;

import static org.anarres.cpp.Token.*;

/** Does not handle digraphs. */
public class LexerSource extends Source {
	private static final boolean	DEBUG = false;

	private JoinReader		reader;
	private boolean			ppvalid;
	private boolean			bol;
	private boolean			include;

	private boolean			digraphs;

	/* Unread. */
	private int				u0, u1;
	private int				ucount;

	private int				line;
	private int				column;
	private int				lastcolumn;
	private boolean			cr;

	/* ppvalid is:
	 * false in StringLexerSource,
	 * true in FileLexerSource */
	public LexerSource(Reader r, boolean ppvalid) {
		this.reader = new JoinReader(r);
		this.ppvalid = ppvalid;
		this.bol = true;
		this.include = false;

		this.digraphs = true;

		this.ucount = 0;

		this.line = 1;
		this.column = 0;
		this.lastcolumn = -1;
		this.cr = false;
	}

	@Override
	/* pp */ void init(Preprocessor pp) {
		super.init(pp);
		this.digraphs = pp.getFeature(Feature.DIGRAPHS);
		this.reader.init(pp, this);
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return column;
	}

	@Override
	/* pp */ boolean isNumbered() {
		return true;
	}

/* Error handling. */

	private final void _error(String msg, boolean error)
						throws LexerException {
		int	_l = line;
		int	_c = column;
		if (_c == 0) {
			_c = lastcolumn;
			_l--;
		}
		else {
			_c--;
		}
		if (error)
			super.error(_l, _c, msg);
		else
			super.warning(_l, _c, msg);
	}

	/* Allow JoinReader to call this. */
	/* pp */ final void error(String msg)
						throws LexerException {
		_error(msg, true);
	}

	/* Allow JoinReader to call this. */
	/* pp */ final void warning(String msg)
						throws LexerException {
		_error(msg, false);
	}

/* A flag for string handling. */

	/* pp */ void setInclude(boolean b) {
		this.include = b;
	}

/*
	private boolean _isLineSeparator(int c) {
		return Character.getType(c) == Character.LINE_SEPARATOR
				|| c == -1;
	}
*/

	/* XXX Move to JoinReader and canonicalise newlines. */
	private static final boolean isLineSeparator(int c) {
		switch ((char)c) {
			case '\r':
			case '\n':
			case '\u2028':
			case '\u2029':
			case '\u000B':
			case '\u000C':
			case '\u0085':
				return true;
			default:
				return (c == -1);
		}
	}


	private int read()
						throws IOException,
								LexerException {
		assert ucount <= 2 : "Illegal ucount: " + ucount;
		switch (ucount) {
			case 2:
				ucount = 1;
				return u1;
			case 1:
				ucount = 0;
				return u0;
		}

		if (reader == null)
			return -1;

		int	c = reader.read();
		switch (c) {
			case '\r':
				cr = true;
				line++;
				lastcolumn = column;
				column = 0;
				break;
			case '\n':
				if (cr) {
					cr = false;
					break;
				}
				/* fallthrough */
			case '\u2028':
			case '\u2029':
			case '\u000B':
			case '\u000C':
			case '\u0085':
				cr = false;
				line++;
				lastcolumn = column;
				column = 0;
				break;
			default:
				cr = false;
				column++;
				break;
		}

/*
		if (isLineSeparator(c)) {
			line++;
			lastcolumn = column;
			column = 0;
		}
		else {
			column++;
		}
*/

		return c;
	}

	/* You can unget AT MOST one newline. */
	private void unread(int c)
						throws IOException {
		/* XXX Must unread newlines. */
		if (c != -1) {
			if (isLineSeparator(c)) {
				line--;
				column = lastcolumn;
				cr = false;
			}
			else {
				column--;
			}
			switch (ucount) {
				case 0:
					u0 = c;
					ucount = 1;
					break;
				case 1:
					u1 = c;
					ucount = 2;
					break;
				default:
					throw new IllegalStateException(
							"Cannot unget another character!"
								);
			}
			// reader.unread(c);
		}
	}

	/* Consumes the rest of the current line into an invalid. */
	private Token invalid(StringBuilder text, String reason)
						throws IOException,
								LexerException {
		int	d = read();
		while (!isLineSeparator(d)) {
			text.append((char)d);
			d = read();
		}
		unread(d);
		return new Token(INVALID, text.toString(), reason);
	}

	private Token ccomment()
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder("/*");
		int				d;
		do {
			do {
				d = read();
				text.append((char)d);
			} while (d != '*');
			do {
				d = read();
				text.append((char)d);
			} while (d == '*');
		} while (d != '/');
		return new Token(CCOMMENT, text.toString());
	}

	private Token cppcomment()
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder("//");
		int				d = read();
		while (!isLineSeparator(d)) {
			text.append((char)d);
			d = read();
		}
		unread(d);
		return new Token(CPPCOMMENT, text.toString());
	}

	private int escape(StringBuilder text)
						throws IOException,
								LexerException {
		int		d = read();
		switch (d) {
			case 'a': text.append('a'); return 0x07;
			case 'b': text.append('b'); return '\b';
			case 'f': text.append('f'); return '\f';
			case 'n': text.append('n'); return '\n';
			case 'r': text.append('r'); return '\r';
			case 't': text.append('t'); return '\t';
			case 'v': text.append('v'); return 0x0b;
			case '\\': text.append('\\'); return '\\';

			case '0': case '1': case '2': case '3':
			case '4': case '5': case '6': case '7':
				int	len = 0;
				int	val = 0;
				do {
					val = (val << 3) + Character.digit(d, 8);
					text.append((char)d);
					d = read();
				} while (++len < 3 && Character.digit(d, 8) != -1);
				unread(d);
				return val;

			case 'x':
				len = 0;
				val = 0;
				do {
					val = (val << 4) + Character.digit(d, 16);
					text.append((char)d);
					d = read();
				} while (++len < 2 && Character.digit(d, 16) != -1);
				unread(d);
				return val;

			/* Exclude two cases from the warning. */
			case '"': text.append('"'); return '"';
			case '\'': text.append('\''); return '\'';

			default:
				warning("Unnecessary escape character " + (char)d);
			case 'p':
				text.append((char)d);
				return d;
		}
	}

	private Token character()
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder("'");
		int				d = read();
		if (d == '\\') {
			text.append('\\');
			d = escape(text);
		}
		else if (isLineSeparator(d)) {
			unread(d);
			return new Token(INVALID, text.toString(),
							"Unterminated character literal");
		}
		else if (d == '\'') {
			text.append('\'');
			return new Token(INVALID, text.toString(),
							"Empty character literal");
		}
		else if (!Character.isDefined(d)) {
			text.append('?');
			return invalid(text, "Illegal unicode character literal");
		}
		else {
			text.append((char)d);
		}

		int		e = read();
		if (e != '\'') {
			// error("Illegal character constant");
			/* We consume up to the next ' or the rest of the line. */
			for (;;) {
				if (isLineSeparator(e)) {
					unread(e);
					break;
				}
				text.append((char)e);
				if (e == '\'')
					break;
				e = read();
			}
			return new Token(INVALID, text.toString(),
							"Illegal character constant " + text);
		}
		text.append('\'');
		/* XXX It this a bad cast? */
		return new Token(CHARACTER,
				text.toString(), Character.valueOf((char)d));
	}

	private Token string(char open, char close)
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder();
		text.append(open);

		StringBuilder	buf = new StringBuilder();

		for (;;) {
			int	c = read();
			if (c == close) {
				break;
			}
			else if (c == '\\') {
				text.append('\\');
				if (!include) {
					char	d = (char)escape(text);
					buf.append(d);
				}
			}
			else if (c == -1) {
				unread(c);
				// error("End of file in string literal after " + buf);
				return new Token(INVALID, text.toString(),
						"End of file in string literal after " + buf);
			}
			else if (isLineSeparator(c)) {
				unread(c);
				// error("Unterminated string literal after " + buf);
				return new Token(INVALID, text.toString(),
						"Unterminated string literal after " + buf);
			}
			else {
				text.append((char)c);
				buf.append((char)c);
			}
		}
		text.append(close);
		return new Token(close == '>' ? HEADER : STRING,
						text.toString(), buf.toString());
	}

	private Token _number(StringBuilder text, long val, int d)
						throws IOException,
								LexerException {
		int	bits = 0;
		for (;;) {
			/* XXX Error check duplicate bits. */
			if (d == 'U' || d == 'u') {
				bits |= 1;
				text.append((char)d);
				d = read();
			}
			else if (d == 'L' || d == 'l') {
				if ((bits & 4) != 0)
					/* XXX warn */ ;
				bits |= 2;
				text.append((char)d);
				d = read();
			}
			else if (d == 'I' || d == 'i') {
				if ((bits & 2) != 0)
					/* XXX warn */ ;
				bits |= 4;
				text.append((char)d);
				d = read();
			}
			else if (Character.isLetter(d)) {
				unread(d);
				return new Token(INVALID, text.toString(),
						"Invalid suffix \"" + (char)d +
						"\" on numeric constant");
			}
			else {
				unread(d);
				return new Token(INTEGER,
					text.toString(), Long.valueOf(val));
			}
		}
	}

	/* We already chewed a zero, so empty is fine. */
	private Token number_octal()
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder("0");
		int				d = read();
		long			val = 0;
		while (Character.digit(d, 8) != -1) {
			val = (val << 3) + Character.digit(d, 8);
			text.append((char)d);
			d = read();
		}
		return _number(text, val, d);
	}

	/* We do not know whether know the first digit is valid. */
	private Token number_hex(char x)
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder("0");
		text.append(x);
		int				d = read();
		if (Character.digit(d, 16) == -1) {
			unread(d);
			// error("Illegal hexadecimal constant " + (char)d);
			return new Token(INVALID, text.toString(),
					"Illegal hexadecimal digit " + (char)d +
					" after "+ text);
		}
		long	val = 0;
		do {
			val = (val << 4) + Character.digit(d, 16);
			text.append((char)d);
			d = read();
		} while (Character.digit(d, 16) != -1);
		return _number(text, val, d);
	}

	/* We know we have at least one valid digit, but empty is not
	 * fine. */
	/* XXX This needs a complete rewrite. */
	private Token number_decimal(int c)
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder((char)c);
		int				d = c;
		long			val = 0;
		do {
			val = val * 10 + Character.digit(d, 10);
			text.append((char)d);
			d = read();
		} while (Character.digit(d, 10) != -1);
		return _number(text, val, d);
	}

	private Token identifier(int c)
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder();
		int				d;
		text.append((char)c);
		for (;;) {
			d = read();
			if (Character.isIdentifierIgnorable(d))
				;
			else if (Character.isJavaIdentifierPart(d))
				text.append((char)d);
			else
				break;
		}
		unread(d);
		return new Token(IDENTIFIER, text.toString());
	}

	private Token whitespace(int c)
						throws IOException,
								LexerException {
		StringBuilder	text = new StringBuilder();
		int				d;
		text.append((char)c);
		for (;;) {
			d = read();
			if (ppvalid && isLineSeparator(d))	/* XXX Ugly. */
				break;
			if (Character.isWhitespace(d))
				text.append((char)d);
			else
				break;
		}
		unread(d);
		return new Token(WHITESPACE, text.toString());
	}

	/* No token processed by cond() contains a newline. */
	private Token cond(char c, int yes, int no)
						throws IOException,
								LexerException {
		int	d = read();
		if (c == d)
			return new Token(yes);
		unread(d);
		return new Token(no);
	}

	public Token token()
						throws IOException,
								LexerException {
		Token	tok = null;

		int		_l = line;
		int		_c = column;

		int		c = read();
		int		d;

		switch (c) {
			case '\n':
				if (ppvalid) {
					bol = true;
					if (include) {
						tok = new Token(NL, _l, _c, "\n");
					}
					else {
						int	nls = 0;
						do {
							nls++;
							d = read();
						} while (d == '\n');
						unread(d);
						char[]	text = new char[nls];
						for (int i = 0; i < text.length; i++)
							text[i] = '\n';
						// Skip the bol = false below.
						tok = new Token(NL, _l, _c, new String(text));
					}
					if (DEBUG)
						System.out.println("lx: Returning NL: " + tok);
					return tok;
				}
				/* Let it be handled as whitespace. */
				break;

			case '!':
				tok = cond('=', NE, '!');
				break;

			case '#':
				if (bol)
					tok = new Token(HASH);
				else
					tok = cond('#', PASTE, '#');
				break;

			case '+':
				d = read();
				if (d == '+')
					tok = new Token(INC);
				else if (d == '=')
					tok = new Token(PLUS_EQ);
				else
					unread(d);
				break;
			case '-':
				d = read();
				if (d == '-')
					tok = new Token(DEC);
				else if (d == '=')
					tok = new Token(SUB_EQ);
				else if (d == '>')
					tok = new Token(ARROW);
				else
					unread(d);
				break;

			case '*':
				tok = cond('=', MULT_EQ, '*');
				break;
			case '/':
				d = read();
				if (d == '*')
					tok = ccomment();
				else if (d == '/')
					tok = cppcomment();
				else if (d == '=')
					tok = new Token(DIV_EQ);
				else
					unread(d);
				break;

			case '%':
				d = read();
				if (d == '=')
					tok = new Token(MOD_EQ);
				else if (digraphs && d == '>')
					tok = new Token('}');	// digraph
				else if (digraphs && d == ':') PASTE: {
					d = read();
					if (d != '%') {
						unread(d);
						tok = new Token('#');	// digraph
						break PASTE;
					}
					d = read();
					if (d != ':') {
						unread(d);	// Unread 2 chars here.
						unread('%');
						tok = new Token('#');	// digraph
						break PASTE;
					}
					tok = new Token(PASTE);	// digraph
				}
				else
					unread(d);
				break;

			case ':':
				/* :: */
				d = read();
				if (digraphs && d == '>')
					tok = new Token(']');	// digraph
				else
					unread(d);
				break;

			case '<':
				if (include) {
					tok = string('<', '>');
				}
				else {
					d = read();
					if (d == '=')
						tok = new Token(LE);
					else if (d == '<')
						tok = cond('=', LSH_EQ, LSH);
					else if (digraphs && d == ':')
						tok = new Token('[');	// digraph
					else if (digraphs && d == '%')
						tok = new Token('{');	// digraph
					else
						unread(d);
				}
				break;

			case '=':
				tok = cond('=', EQ, '=');
				break;

			case '>':
				d = read();
				if (d == '=')
					tok = new Token(GE);
				else if (d == '>')
					tok = cond('=', RSH_EQ, RSH);
				else
					unread(d);
				break;

			case '^':
				tok = cond('=', XOR_EQ, '^');
				break;

			case '|':
				d = read();
				if (d == '=')
					tok = new Token(OR_EQ);
				else if (d == '|')
					tok = cond('=', LOR_EQ, LOR);
				else
					unread(d);
				break;
			case '&':
				d = read();
				if (d == '&')
					tok = cond('=', LAND_EQ, LAND);
				else if (d == '=')
					tok = new Token(AND_EQ);
				else
					unread(d);
				break;

			case '.':
				d = read();
				if (d == '.')
					tok = cond('.', ELLIPSIS, RANGE);
				else
					unread(d);
				/* XXX decimal fraction */
				break;

			case '0':
				/* octal or hex */
				d = read();
				if (d == 'x' || d == 'X')
					tok = number_hex((char)d);
				else {
					unread(d);
					tok = number_octal();
				}
				break;

			case '\'':
				tok = character();
				break;

			case '"':
				tok = string('"', '"');
				break;

			case -1:
				close();
				tok = new Token(EOF, _l, _c, "<eof>");
				break;
		}

		if (tok == null) {
			if (Character.isWhitespace(c)) {
				tok = whitespace(c);
			}
			else if (Character.isDigit(c)) {
				tok = number_decimal(c);
			}
			else if (Character.isJavaIdentifierStart(c)) {
				tok = identifier(c);
			}
			else {
				tok = new Token(c);
			}
		}

		if (bol) {
			switch (tok.getType()) {
				case WHITESPACE:
				case CCOMMENT:
					break;
				default:
					bol = false;
					break;
			}
		}

		tok.setLocation(_l, _c);
		if (DEBUG)
			System.out.println("lx: Returning " + tok);
		// (new Exception("here")).printStackTrace(System.out);
		return tok;
	}

	public void close()
						throws IOException {
		if (reader != null) {
			reader.close();
			reader = null;
		}
		super.close();
	}

}
