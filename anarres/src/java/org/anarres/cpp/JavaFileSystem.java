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

/**
 * A virtual filesystem implementation using java.io.
 */
public class JavaFileSystem implements VirtualFileSystem {
	public VirtualFile getFile(String path) {
		return new JavaFile(path);
	}

	public VirtualFile getFile(String dir, String name) {
		return new JavaFile(dir, name);
	}

	private class JavaFile extends File implements VirtualFile {
		public JavaFile(String path) {
			super(path);
		}

		public JavaFile(String dir, String name) {
			super(dir, name);
		}

		/* private */
		public JavaFile(File dir, String name) {
			super(dir, name);
		}

/*
		@Override
		public String getPath() {
			return getCanonicalPath();
		}
*/

		@Override
		public JavaFile getParentFile() {
			String	parent = getParent();
			if (parent != null)
				return new JavaFile(parent);
			File	absolute = getAbsoluteFile();
			parent = absolute.getParent();
			/*
			if (parent == null)
				return null;
			*/
			return new JavaFile(parent);
		}

		public JavaFile getChildFile(String name) {
			return new JavaFile(this, name);
		}

		public Source getSource() throws IOException {
			return new FileLexerSource(this);
		}

	}

}
