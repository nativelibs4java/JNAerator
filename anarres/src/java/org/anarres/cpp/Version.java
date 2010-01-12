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

/**
 * System version metadata for Anarres Java C Preprocessor ${version}.
 *
 * This class contains a main() and may be run to print the version.
 */
public class Version {

	/* Don't instantiate me */
	private Version() {
	}

	private static final String	VERSION = "${version}";

	private static final int	major;
	private static final int	minor;
	private static final int	patch;

	static {
		String[]	tmp = VERSION.split("\\.");
		major = Integer.parseInt(tmp[0]);
		minor = Integer.parseInt(tmp[1]);
		patch = Integer.parseInt(tmp[2]);
	}

	public static String getVersion() {
		return VERSION;
	}

	public static int getMajor() {
		return major;
	}

	public static int getMinor() {
		return minor;
	}

	public static int getPatch() {
		return patch;
	}

	public static void main(String[] args) {
		System.out.println("Version " + VERSION);
		System.out.println("getVersion() returns " + getVersion());
		System.out.println("getMajor() returns " + getMajor());
		System.out.println("getMinor() returns " + getMinor());
		System.out.println("getPatch() returns " + getPatch());
	}

}
