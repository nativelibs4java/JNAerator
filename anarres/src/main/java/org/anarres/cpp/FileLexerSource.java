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

import java.util.List;
import java.util.Iterator;

import static org.anarres.cpp.Token.*;

/**
 * A {@link Source} which lexes a file.
 *
 * The input is buffered.
 *
 * @see Source
 */
public class FileLexerSource extends LexerSource {
	// private File	file;
	private String	path;

	/**
	 * Creates a new Source for lexing the given File.
	 *
	 * Preprocessor directives are honoured within the file.
	 */
	public FileLexerSource(File file, String path)
						throws IOException {
		super(
			new BufferedReader(
				new DeferredFileReader(
					file
				)
			),
			true
		);

		// this.file = file;
		this.path = path;
	}

	public FileLexerSource(File file)
						throws IOException {
		this(file, file.getPath());
	}

	public FileLexerSource(String path)
						throws IOException {
		this(new File(path));
	}

	@Override
	/* pp */ 
	public String getPath() {
		return path;
	}

	@Override
	/* pp */ String getName() {
		return getPath();
	}

	public String toString() {
		return "file " + path;
	}
}
