/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.anarres.cpp.CppReader;
import org.anarres.cpp.Feature;
import org.anarres.cpp.FileLexerSource;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Macro;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.PreprocessorListener;
import org.anarres.cpp.Source;
import org.anarres.cpp.StringLexerSource;
import org.anarres.cpp.Token;
import org.anarres.cpp.Warning;

import com.ochafik.io.ReadText;
import com.ochafik.io.WriteText;
import com.ochafik.lang.jnaerator.parser.Define;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;
import java.util.Collection;
import java.util.HashMap;

public class PreprocessorUtils {

    public static interface MacroUseCallback {
        void macroUsed(String path, String macroName);
    }
	public static String preprocessSources(JNAeratorConfig config, List<Define> defines, boolean verbose, TypeConversion typeConverter, MacroUseCallback macrosDependenciesOut) throws IOException, LexerException {

		Preprocessor preProcessor = PreprocessorUtils.createPreProcessor(config.preprocessorConfig, macrosDependenciesOut);
		for (File file : config.getFiles())
			preProcessor.addInput(file);
		
		for (String content : config.preprocessorConfig.includeStrings)
			preProcessor.addInput(new StringLexerSource(content, true));
		
		String sourceContent = ReadText.readText(new CppReader(preProcessor));
		preProcessor.close();
		
		Map<String, Macro> macros = preProcessor.getMacros();
		
		if (config.preprocessingOutFile != null) {
			if (config.verbose)
				System.out.println("Writing preprocessor output to '" + config.preprocessingOutFile + "'");
			WriteText.writeText(sourceContent, config.preprocessingOutFile);
		}
		
		if (config.macrosOutFile != null) {
			if (config.verbose)
				System.out.println("Writing preprocessor macros to '" + config.macrosOutFile + "'");
			WriteText.writeText(StringUtils.implode(macros.entrySet(), "\n"), config.macrosOutFile);
		}
		
		for (String k : config.preprocessorConfig.macros.keySet())
			macros.remove(k);
		
		PreprocessorUtils.addDefines(macros, defines, verbose, typeConverter);
		
		return sourceContent;
	}

	public static Preprocessor createPreProcessor(JNAeratorConfig.PreprocessorConfig config, final MacroUseCallback macrosDependenciesOut) throws IOException, LexerException {
		Preprocessor preprocessor = new Preprocessor() {
			Set<String> pragmaOnces = new HashSet<String>();
			@Override
			protected void pragma(Token name, List<Token> value)
					throws IOException, LexerException {
				if ("once".equals(name.getText())) {
					if (!pragmaOnces.add(getSource().toString()))
						pop_source();
				} else
					super.pragma(name, value);
			}
			@Override
			protected void pop_source() throws IOException {
				if (getSource() instanceof FileLexerSource) {
					
				}
				super.pop_source();
			}


            @Override
            protected Map<String, Macro> createMacro() {
                return new HashMap<String, Macro>() {

                    @Override
                    public boolean containsKey(Object key) {
                        if (key instanceof String)
                            used((String)key);
                        return super.containsKey(key);
                    }

                    @Override
                    public Macro get(Object key) {
                        if (key instanceof String)
                            used((String)key);
                        return super.get(key);
                    }

                    @Override
                    public void clear() {
                        super.clear();
                    }

                    @Override
                    public Macro put(String key, Macro value) {
                        if (key != null)
                            used(key);
                        return super.put(key, value);
                    }


                };
            }

            void used(String name) {
                if (macrosDependenciesOut != null) {
                    Source src = getSource();
                    macrosDependenciesOut.macroUsed(src == null ? null : src.getPath(), name);
                }
            }

		};
		preprocessor.setProperStringTokensInLinePragmas(true);
		//preprocessor.addFeatures(EnumSet.allOf(Feature.class));
		if (config.preprocess) {
			preprocessor.addFeature(Feature.KEEPCOMMENTS);
			preprocessor.addFeature(Feature.DIGRAPHS);
			preprocessor.addFeature(Feature.INCLUDENEXT);
			preprocessor.addFeature(Feature.OBJCSYNTAX);
			preprocessor.addFeature(Feature.TRIGRAPHS);
			//preprocessor.addFeature(Feature.CSYNTAX);
			preprocessor.addFeature(Feature.LINEMARKERS);
			//preprocessor.addFeature(Feature.DEBUG);
			
	
			preprocessor.addWarning(Warning.IMPORT);
		} else {
			preprocessor.getFeatures().clear();
		}
		
		preprocessor.setListener(new PreprocessorListener() {
			@Override
			public void handleWarning(Source source, int line, int column,
					String msg) throws LexerException {
				if (msg.contains("Unnecessary escape character "))
					return;
				
				if (msg.contains("#pragma"))
					return;
					
				super.handleWarning(source, line, column, msg);
			}
		});
		
		preprocessor.getSystemIncludePath().addAll(config.includes);
		//preprocessor.getSystemIncludePath().addAll(Arrays.asList(DEFAULT_INCLUDE_PATH.split(":")));
		preprocessor.getQuoteIncludePath().addAll(config.includes);
		//preprocessor.getFrameworksPath().addAll(Arrays.asList(DEFAULT_FRAMEWORKS_PATH.split(":")));
		preprocessor.getFrameworksPath().addAll(config.frameworksPath);
		for (Map.Entry<String, String> e : config.macros.entrySet()) {
			if (e.getValue() != null)
				preprocessor.addMacro(e.getKey(), e.getValue());
			else
				preprocessor.addMacro(e.getKey());
		}
		return preprocessor;
	}

	public static String removePreprocessorDirectives(String s) {
		s = s.replaceAll(";#line", ";\n#line"); /// hack !
		
		s = s.replaceAll("(?s)#\\s*(pragma|if|endif|error|ifdef|ifndef|else|elif|define|undef).*?\n", "\n");
		s = s.replaceAll("(?s)#\\s*(import|include).*?\n", "\n");
		return s;
	}

	public static String removeNastyDefines(String s) {
		/// Mac OS X
		s = s.replaceAll("DEPRECATED_IN_MAC_OS_X_VERSION_[A-Z0-9_]+_AND_LATER", " ");
		s = s.replaceAll("AVAILABLE_MAC_OS_X_VERSION_[A-Z0-9_]+_AND_LATER(_BUT_DEPRECATED_IN_MAC_OS_X_VERSION_[A-Z0-9_]+)?", " ");
		s = s.replaceAll("NS_REQUIRES_NIL_TERMINATION", " ");
		s = s.replaceAll("__MATH_H_ALWAYS_INLINE__", " ");
		s = s.replace("CSEXTERN", "extern");
		
		/// Windows
		s = s.replace("__inner_fallthrough_dec", " ");
		
		/// Others
		//s = s.replaceAll("__strong", " ");
		return s;
	}

	//static void addDefines(Preprocessor preProcessor, List<Define> defines) {
	//	for (Map.Entry<String, Macro> e : preProcessor.getMacros().entrySet()) {
	static void addDefines(Map<String, Macro> macros, List<Define> defines, boolean verbose, TypeConversion typeConverter) {
		for (Map.Entry<String, Macro> e : macros.entrySet()) {
			Macro macro = e.getValue();
			if (macro.getText() == null)
				continue;
		
			if (macro.isFunctionLike() && macro.getArgs() > 0)
				continue;
			
			//if (macro.getFile() == null)
			//	continue;
			
			try {
				Expression expression = JNAeratorParser.newObjCppParser(typeConverter, macro.getText(), verbose).expression().expr;
				if (expression == null)
					continue;
			
				Define define = new Define(e.getKey(), expression);
				if (macro.getSource() != null)
					define.setElementFile(macro.getSource().getPath());
				defines.add(define);
			} catch (Exception ex) {
				if (verbose)
					System.err.println("Failed to convert define '" + e.getValue() + ":\n" + ex);
				//ex.printStackTrace();
			}
		}
	}
	

}
