/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ochafik.admin.visualstudio;
import java.io.File;
import java.io.FileFilter;

/*
java -cp ../classes com.ochafik.admin.visualstudio.VisualStudioSolutionAndProjectsParser "C:\Documents and Settings\Administrator\My Documents\Visual Studio 2008\Projects\VSTest\VSTest.sln"

java -cp ../classes;../libraries/antlr-runtime-3.1.jar;../libraries/trove.jar;../libraries/anarres-cpp.jar com.ochafik.lang.jnaerator.JNAerator -project "C:\Documents and Settings\Administrator\My Documents\Visual Studio 2008\Projects\VSTest\VSTest.sln" "Release|Win32"
*/

public class VisualStudioSolutionAndProjectsParser {
	static FileFilter headersFileFilter = new FileFilter() { public boolean accept(File f) {
		String name = f.getName();
		return name.endsWith(".h") || name.endsWith(".hpp") || name.endsWith(".hxx");
	}};
	
	public static void main(String[] args) {
		try {
			/*if (args.length == 0) {
				args = new String[] {
					"/Users/ochafik/Prog/Java/test/SlnAndVCProjs/sln/WholeRisque533.sln"
				};
			}*/
			
			long initTime = System.currentTimeMillis();
			File solutionFile = new File(args[0]);
			
			final Solution solution = new Solution(solutionFile);
			
			solution.parseProjects(headersFileFilter);
			
			System.out.println("Elapsed Time: " + (System.currentTimeMillis() - initTime) + " ms");
			System.out.println("Files Count: " + solution.allFiles.size());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
