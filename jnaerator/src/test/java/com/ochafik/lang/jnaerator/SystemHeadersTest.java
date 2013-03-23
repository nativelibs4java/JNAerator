/*
	Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
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
package com.ochafik.lang.jnaerator;

import java.io.*;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.JNAerator.Feedback;
import com.ochafik.lang.jnaerator.JNAeratorConfig.OutputMode;
import com.ochafik.lang.jnaerator.studio.JNAeratorStudio.SyntaxException;

import org.anarres.cpp.LexerException;
import org.antlr.runtime.RecognitionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.ochafik.junit.ParameterizedWithDescription;

@RunWith(Parameterized.class)
public class SystemHeadersTest {
	String header, pref;
    
	public SystemHeadersTest(String header) {
		this.header = header;
		this.pref = "[include " + header + "] ";
	}
	
	@Test
	public void test() throws SyntaxException, IOException, LexerException, RecognitionException {
		JNAeratorConfig config = new JNAeratorConfig();
		config.defaultLibrary = header.replaceAll("[^\\w]+", "_");
		config.outputMode = null;
		config.parseInChunks = false;
		config.runtime = JNAeratorConfig.Runtime.BridJ;
		config.preferJavac = true;
		//config.preprocessorConfig.includes.add("/Users/ochafik/Downloads/mingw-w64-bin_i686-darwin_20121031/x86_64-w64-mingw32/include");
		config.libraryForElementsInNullFile = config.defaultLibrary;
		
		final String testFunctionName = "testFunction";
		config.preprocessorConfig.includeStrings.add("#include <" + header + ">\n\nvoid " + testFunctionName + "();");
		 
		new JNAerator(config).jnaerate(new Feedback() {
			
			@Override
			public void sourcesParsed(SourceFiles sourceFiles) {
				List<SourceFile> files = sourceFiles.getSourceFiles();
				assertTrue(pref + "Parsing failed completely", !files.isEmpty());
				//assertTrue(pref + "Expected more than one source file", files.size() > 1);
				SourceFile lastFile = files.get(files.size() - 1);
				List<Declaration> declarations = lastFile.getDeclarations();
				assertTrue(pref + "Only parsed " + declarations.size() + " declarations", declarations.size() > 1);
				Declaration d = declarations.get(declarations.size() - 1);
				assertTrue(pref + "Not a function: " + d + " (" + (d == null ? "?" : d.getClass().getName()) + ")", d instanceof Function);
				Function f = (Function)d;
				assertEquals(pref + "Didn't find " + testFunctionName, testFunctionName, f.getName() + "");
			}
			
			@Override
			public void setStatus(String string) {}
			
			@Override
			public void setFinished(Throwable ex) {
                if (ex != null) ex.printStackTrace(System.out);
                assertNull(pref + "Error : " + ex, ex);
			}
			
			@Override
			public void setFinished(File toOpen) {}

			@Override
			public void wrappersGenerated(Result result) {}
		});
	}
	
	@Parameters
	public static List<Object[]> readParameters() throws IOException {
		List<Object[]> data = new ArrayList<Object[]>();
		List<String> headers = Arrays.asList(
			"assert.h",
			"complex.h",
			"ctype.h",
			"errno.h",
			"fenv.h",
			//"float.h",
			"inttypes.h",
			//"iso646.h",
			"limits.h",
			"locale.h",
			"math.h",
			"setjmp.h",
			"signal.h",
			//"stdalign.h",
			"stdarg.h",
			//"stdatomic.h",
			//"stdbool.h",
			"stddef.h",
			"stdint.h",
			"stdio.h",
			"stdlib.h",
			//"stdnoreturn.h",
			"string.h",
			"tgmath.h",
			//"threads.h",
			"time.h",
			//"uchar.h",
			"wchar.h",
			"wctype.h"
		);
		for (String header : headers) {
			data.add(new Object[] { header });
		}
		return data;
	}
}
