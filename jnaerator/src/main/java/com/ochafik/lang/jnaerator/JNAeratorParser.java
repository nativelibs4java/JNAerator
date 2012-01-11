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
		}
		return slices;
	}
    
    static Callable<SourceFile> createParsingCallable(final JNAeratorConfig config, final TypeConversion typeConverter, final String source, final Set<String> topLevelTypeDefs, boolean isFull) {
        return new Callable<SourceFile>() {

            public SourceFile call() throws Exception {
                PrintStream originalOut = System.out, originalErr = System.err;
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                PrintStream pout = new PrintStream(bout);
                System.setOut(pout);
                System.setErr(pout);
                
                Exception error = null;
                try {
                    ObjCppParser parser = newObjCppParser(typeConverter, source, config.verbose, pout);
                    if (topLevelTypeDefs != null)
                        parser.topLevelTypeIdentifiers = topLevelTypeDefs;
                    SourceFile sourceFile = parser.sourceFile();//.sourceFile;
                    if (sourceFile == null)
                        throw new RuntimeException("parser.sourceFile() returned null");
                    return sourceFile;
                } catch (RuntimeException ex) {
                    if (ex.getCause() instanceof InterruptedException)
                        throw (InterruptedException)ex.getCause();
                    
                    error = ex;
                    
                } finally {
                    System.setOut(originalOut);
                    System.setErr(originalErr);
                }
                
                pout.flush();
                String errorOut = new String(bout.toByteArray()).trim();
                if (errorOut.length() > 0 && config.verbose)
                    WriteText.writeText(errorOut, new File("fullParsing.errors.txt"));
                
                throw new ParseError(source, errorOut, error);
                
            }
            
        };
    }
    public static class ParseError extends RuntimeException {
        private final String source;
        private final String errors;

        public ParseError(String source, String errors, Throwable cause) {
            super("Failed to parse because of " + cause, cause);
            this.source = source;
            this.errors = errors;
        }

        public String getErrors() {
            return errors;
        }

        public String getSource() {
            return source;
        }
        
    }

	public static SourceFiles parse(JNAeratorConfig config, TypeConversion typeConverter, MacroUseCallback macrosDependenciesOut) throws IOException, LexerException {
		SourceFiles sourceFiles = new SourceFiles();
        
        String sourceContent = PreprocessorUtils.preprocessSources(config, sourceFiles.defines, config.verbose, typeConverter, macrosDependenciesOut);
		
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            if (!config.parseInChunks) {
                try {
                    sourceFiles.add(executor.submit(createParsingCallable(config, typeConverter, sourceContent, null, true)).get(config.fullParsingTimeout, TimeUnit.MILLISECONDS));
                    return sourceFiles;
                } catch (Throwable ex) {
                    System.err.println("Parsing failed : " + ex);
                }

                System.gc();
            }
			System.err.println("Now trying to parse sources slice by slice.");
	
	
			// compartimented parsing (at each change of file)
			List<Slice> slices = cutSourceContentInSlices(sourceContent, System.out);
			if (config.verbose)
				System.out.println("Now parsing " + slices.size() + " slices");
	
			final Set<String> topLevelTypeDefs = Collections.synchronizedSet(new HashSet<String>());
			boolean firstFailure = true;
			for (Slice slice : slices) {
				try {
					sourceFiles.add(executor.submit(createParsingCallable(config, typeConverter, sourceContent, topLevelTypeDefs, false)).get(config.sliceParsingTimeout, TimeUnit.MILLISECONDS));
				} catch (Throwable ex) {
					if (firstFailure) {
						WriteText.writeText(slice.text, new File("splitParsing.firstFailure.source.txt"));
						if (ex.getCause() instanceof ParseError) {
							ParseError pe = (ParseError)ex.getCause();
							WriteText.writeText(pe.getErrors(), new File("splitParsing.firstFailure.errors.txt"));
							//ex.printStackTrace();
						}
						firstFailure = false;
					}
					System.gc();
					System.err.println("Parsing failed : " + ex);
				}
			}
			return sourceFiles;
		} finally {
			executor.shutdown();
		}
	}
	static ObjCppParser newObjCppParser(TypeConversion typeConverter, String s, final boolean verbose, final PrintStream errorOut) throws IOException {
		ObjCppParser parser = new ObjCppParser(
            new CommonTokenStream(
                new ObjCppLexer(
                    new ANTLRReaderStream(new StringReader(s))
                )
            )
//		    , new DummyDebugEventListener()
		) {

            @Override
            public void emitErrorMessage(String msg) {
                if (errorOut == null) {
//                    if (verbose)
//                        super.emitErrorMessage(msg);
                } else
                    errorOut.println(msg);
            }
            
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
