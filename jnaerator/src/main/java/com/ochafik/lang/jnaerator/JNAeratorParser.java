/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
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
package com.ochafik.lang.jnaerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.anarres.cpp.LexerException;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.ochafik.io.WriteText;
import com.ochafik.lang.jnaerator.PreprocessorUtils.MacroUseCallback;
import com.ochafik.lang.jnaerator.parser.ObjCppLexer;
import com.ochafik.lang.jnaerator.parser.ObjCppParser;
import com.ochafik.lang.jnaerator.parser.SourceFile;
import com.ochafik.lang.reflect.DebugUtils;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.RegexUtils;
import java.util.Collection;

public class JNAeratorParser {

	static class Slice {
		public String file;
		public int line;
		public String text;
		public Slice(String file, int line, String text) {
			super();
			this.file = file;
			this.line = line;
			this.text = text;
		}
	}
	private static final boolean EASILY_DEBUGGABLE_BUT_FRAGILE_PARSING_MODE = false;
	
	private static List<Slice> cutSourceContentInSlices(String sourceContent, PrintStream originalOut) {
		StringBuffer currentEmptyLines = new StringBuffer();
		StringBuffer currentBuffer = new StringBuffer();
		
		boolean sliceGotContent = false;
		
		String[] lines = sourceContent.split("\n");
		int iLine = 0, nLines = lines.length, lastStart = 0;
		String lastFile = null;
		//int lastPercent = 0;
		
	
		Pattern fileInLinePattern = Pattern.compile("\"([^\"]+)\"");
		List<Slice> slices = new ArrayList<Slice>(nLines / 10);
		for (String line : lines) {
			/*int percent = (iLine + 1) * 100 / nLines;
			if (lastPercent != percent) {
				//originalOut.print("\b\b\b\b\b");
				originalOut.println(percent + "%");
				lastPercent = percent;
			}*/
			if (line.startsWith("#line")) {
				lastStart = iLine;
				lastFile = RegexUtils.findFirst(line, fileInLinePattern, 1);
				if (sliceGotContent) {
					//originalOut.println("Split: " + line.substring("#line".length()).trim());
					String content = currentBuffer.toString();
					slices.add(new Slice(lastFile, lastStart, content));
					//sourceFiles.add(newObjCppParser(content).sourceFile().sourceFile);
				}
				currentBuffer.setLength(0);
				//currentBuffer.append(currentEmptyLines);
				sliceGotContent = false;
			}
		
			if (!sliceGotContent)
				sliceGotContent = line.trim().length() > 0;
			currentBuffer.append(line);
			currentBuffer.append('\n');
			currentEmptyLines.append('\n');
			//deltaLines++;
	
			iLine++;
		}
		
		if (sliceGotContent) {
			String content = currentBuffer.toString();
			slices.add(new Slice(lastFile, lastStart, content));
			//sourceFiles.add(newObjCppParser(content).sourceFile().sourceFile);
		}
		return slices;
	}

	private static void parseSlices(final JNAeratorConfig config, final TypeConversion typeConverter, SourceFiles sourceFilesOut, List<Slice> slices, PrintStream originalOut, PrintStream originalErr, boolean multithreaded) throws InterruptedException {
        final Set<String> topLevelTypeDefs = Collections.synchronizedSet(new HashSet<String>());

        //ExecutorService executorService = Executors.newFixedThreadPool(multithreaded ? Runtime.getRuntime().availableProcessors() * 2 : 1);
        if (slices.isEmpty())
            originalOut.println("Slices are empty with the following config : \n" + DebugUtils.toString(config));

        for (final Slice slice : slices) {
            try {
                ObjCppParser parser = newObjCppParser(typeConverter, slice.text, config.verbose);
                parser.topLevelTypeIdentifiers = topLevelTypeDefs;
                SourceFile sourceFile = parser.sourceFile();//.sourceFile;
                //sourceFile.setElementFile(slice.file);
                sourceFilesOut.add(sourceFile);
            } catch (Throwable ex) {
                ex.printStackTrace();
                originalErr.println("Exception for " + slice.file + " at line " + slice.line + ":" + ex);
                ex.printStackTrace(originalErr);
            }
        }
    }

	public static SourceFiles parse(JNAeratorConfig config, TypeConversion typeConverter, MacroUseCallback macrosDependenciesOut) throws IOException, LexerException {
		SourceFiles sourceFiles = new SourceFiles();
		String sourceContent = PreprocessorUtils.preprocessSources(config, sourceFiles.defines, config.verbose, typeConverter, macrosDependenciesOut);
		
		PrintStream originalOut = System.out, originalErr = System.err;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(bout);
		System.setOut(pout);
		System.setErr(pout);
		try {
			if (!config.parseInChunks) {
				// easier to debug but any error might ruin all the rest of the parsing
				try {
					ObjCppParser parser = newObjCppParser(typeConverter, sourceContent, config.verbose);
					SourceFile sourceFile = parser.sourceFile();//.sourceFile;
					sourceFiles.add(sourceFile);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				// compartimented parsing (at each change of file)
				List<Slice> slices = cutSourceContentInSlices(sourceContent, originalOut);
				if (config.verbose)
					originalOut.println("Now parsing " + slices.size() + " text blocks");
				parseSlices(config, typeConverter, sourceFiles, slices, originalOut, originalErr, false);
			} 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			pout.flush();
			System.setOut(originalOut);
			System.setErr(originalErr);
			String errs = new String(bout.toByteArray()).trim();
			if (errs.length() > 0 && config.verbose)
				WriteText.writeText(errs, new File("out.errors.txt"));
		}
		return sourceFiles;
	}
	static ObjCppParser newObjCppParser(TypeConversion typeConverter, String s, final boolean verbose) throws IOException {
		ObjCppParser parser = new ObjCppParser(
				new CommonTokenStream(
						new ObjCppLexer(
								new ANTLRReaderStream(new StringReader(s))
						)
				)
//				, new DummyDebugEventListener()
		) {
			@Override
			public void reportError(RecognitionException arg0) {
				if (verbose)
					super.reportError(arg0);
			}
		};
		parser.setupScopes();
		parser.objCParserHelper = typeConverter;
		
		return parser;
	}
}
