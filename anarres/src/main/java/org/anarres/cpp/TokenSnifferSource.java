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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import static org.anarres.cpp.Token.*;

@Deprecated
/* pp */ class TokenSnifferSource extends Source {
	private List<Token>	target;

	/* pp */ TokenSnifferSource(List<Token> target) {
		this.target = target;
	}

	public Token token()
						throws IOException,
								LexerException {
		Token	tok = getParent().token();
		if (tok.getType() != EOF)
			target.add(tok);
		return tok;
	}

	public String toString() {
		return getParent().toString();
	}
}
