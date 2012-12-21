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

import com.ochafik.lang.jnaerator.JNAeratorCommandLineArgs.PathType;
import org.bridj.CRuntime;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import org.bridj.ann.Ptr;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;

import org.anarres.cpp.LexerException;
import org.antlr.runtime.RecognitionException;
//import org.junit.runner.JUnitCore;
import org.rococoa.cocoa.foundation.NSClass;
import org.rococoa.Rococoa;

import org.bridj.BridJ;
import org.bridj.cpp.CPPRuntime;
import com.ochafik.io.FileListUtils;
import com.ochafik.io.IOUtils;
import com.ochafik.io.ReadText;
import com.ochafik.io.WriteText;
import com.ochafik.lang.compiler.CompilerUtils;
import com.ochafik.lang.compiler.MemoryFileManager;
import com.ochafik.lang.compiler.MemoryJavaFile;
import com.ochafik.lang.compiler.URLFileObject;
import com.ochafik.lang.jnaerator.JNAeratorCommandLineArgs.OptionDef;
import com.ochafik.lang.jnaerator.JNAeratorConfig.OutputMode;
import com.ochafik.lang.jnaerator.nativesupport.DllExport;
import com.ochafik.lang.jnaerator.parser.Annotation;
import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.Declaration;
import com.ochafik.lang.jnaerator.parser.Declarator;
import com.ochafik.lang.jnaerator.parser.Define;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.ModifiableElement;
import com.ochafik.lang.jnaerator.parser.Modifier;
import com.ochafik.lang.jnaerator.parser.ModifierType;
import com.ochafik.lang.jnaerator.parser.ObjCppParser;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.SourceFile;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRefStyle;
import com.ochafik.lang.jnaerator.parser.Identifier.QualificationSeparator;
import com.ochafik.lang.jnaerator.parser.Identifier.QualifiedIdentifier;
import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import com.ochafik.lang.jnaerator.parser.ModifierKind;
import com.ochafik.lang.jnaerator.parser.ObjCppLexer;
import com.ochafik.lang.jnaerator.parser.Statement;
import com.ochafik.lang.jnaerator.parser.Struct.Type;
import com.ochafik.lang.jnaerator.runtime.LibraryExtractor;
import com.ochafik.lang.jnaerator.runtime.MangledFunctionMapper;
import com.ochafik.lang.jnaerator.runtime.Mangling;
import com.ochafik.lang.jnaerator.studio.JNAeratorStudio;
import com.ochafik.lang.jnaerator.studio.JNAeratorStudio.SyntaxException;
import com.ochafik.util.listenable.Adapter;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.RegexUtils;
import com.ochafik.util.string.StringUtils;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.win32.StdCallLibrary;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.logging.Logger;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import static com.ochafik.lang.jnaerator.nativesupport.NativeExportUtils.*;
import com.ochafik.lang.jnaerator.parser.Function.SignatureType;
import java.io.*;
/*
//include com/ochafik/lang/jnaerator/parser/*.mm
//include com/ochafik/lang/jnaerator/parser/ObjCpp.g
 */


/**

mvn -o compile exec:java -Dexec.mainClass=com.ochafik.lang.jnaerator.JNAerator
 * java -Xmx2000m -jar ../bin/jnaerator.jar `for F in /System/Library/Frameworks/*.framework ; do echo $F| sed -E 's/^.*\/([^/]+)\.framework$/-framework \1/' ; done` -out apple-frameworks.jar
 */

public class JNAerator {
    public static final String JNAERATOR_URL = "http://code.google.com/p/jnaerator/";
    public static String DONATE_URL = "http://sourceforge.net/donate/index.php?group_id=266856";
    public static String ABOUT_JNAERATOR_URL = "http://code.google.com/p/jnaerator/wiki/AboutJNAerator";
    public static String ABOUT_ROCOCOA_URL = "http://code.google.com/p/rococoa";
    public static String ABOUT_BRIDJ_URL = "http://code.google.com/p/bridj/wiki";
    public static String ABOUT_JNA_URL = "https://github.com/twall/jna";

    public static interface Feedback {
		void setStatus(final String string);
		void setFinished(File toOpen);
		void setFinished(Throwable e);
		void sourcesParsed(SourceFiles sourceFiles);
		void wrappersGenerated(Result result);
	}
	private static Pattern argTokenPattern = Pattern.compile("(?m)\"[^\"]*\"|[^\\s]+");
	private static Pattern argVariablePattern = Pattern.compile("\\$\\(([^)]+)\\)");
	protected final JNAeratorConfig config;
	
	public JNAerator(JNAeratorConfig config) {
		this.config = config;
	}

    protected static void fixConfig(JNAeratorConfig config) {
        if (config.sourceFiles.isEmpty() && config.bridgeSupportFiles.isEmpty() && !config.libraryFiles.isEmpty())
            config.extractLibSymbols = true;

        Collection<File> inputFiles = config.getInputFiles();
        File firstFile = inputFiles.isEmpty() ? null : inputFiles.iterator().next().getAbsoluteFile();
        String firstFileName = firstFile == null ? null : firstFile.getName();
        Set<String> libraries = config.getLibraries();
        String entry = 
            config.entryName != null ? config.entryName :
            libraries.size() == 1 ? libraries.iterator().next() : 
            null;//RegexUtils.findFirst(firstFileName, fileRadixPattern, 1); 
        //if (entry != null)
        //	entry = config.result.typeConversion.getValidJavaIdentifier(ident(entry)).toString();

        if (config.outputDir == null)
            config.outputDir = new File(".");

        if (config.sourcesOutputDir == null) {
            if (config.outputMode == OutputMode.Maven)
                config.sourcesOutputDir = new File(config.outputDir, "src/main/java");
            else
                config.sourcesOutputDir = config.outputDir;
        }
        if (config.resourcesOutputDir == null) {
            if (config.outputMode.isMaven())
                config.resourcesOutputDir = new File(config.outputDir, "src/main/resources");
            else
                config.resourcesOutputDir = config.outputDir;
        }
        if (config.outputJar == null && config.outputMode.isJar())
            config.outputJar = new File(config.outputDir, (entry == null ? "out" : entry) + ".jar");

        if (config.mavenArtifactId == null && entry != null)
            config.mavenArtifactId = entry;
        if (config.mavenGroupId == null && config.rootPackageName != null)
            config.mavenGroupId = config.rootPackageName;

        config.cacheDir = getDir("cache");

    }
	static final Pattern definePattern = Pattern.compile("#\\s*define\\s+(\\w+)\\s+(.*)");
	static final boolean fullFilePathInComments = true;
	
	private static final String DEFAULT_CONFIG_FILE = "config.jnaerator";
    private static final String POM_FILENAME = "pom.xml";
	protected static final Pattern fileRadixPattern = Pattern.compile("(?:[/\\\\]|^)(.*?)(?:Full\\.bridgesupport|\\.[^.]+)$");
	private static final Pattern classAndMethodNamePattern = Pattern.compile("(.+?)::([^:]+)");
	
	public static String[] getJNAeratorArgsFromPref() {
		String argsPref = System.getProperty("jnaerator.args");
		if (argsPref == null)
			return null;
		return argsPref.split(",");
	}

    static Logger logger = Logger.getLogger(JNAerator.class.getName());
    public static void main(String[] argsArray) {
        //argsArray = new String[] { "/Users/ochafik/src/boost_1_48_0/config.jnaerator", "-v" };
        main(new JNAerator(new JNAeratorConfig()), argsArray);
    }

	public static void main(final JNAerator jnaerator, String[] argsArray) {
        final Feedback[] feedback = new Feedback[1];
				
		try {
		
			String[] jnAeratorArgsFromPref = getJNAeratorArgsFromPref();
			if (jnAeratorArgsFromPref != null) {
				ArrayList<String> list = new ArrayList<String>();
				list.addAll(Arrays.asList(jnAeratorArgsFromPref));
				list.addAll(Arrays.asList(argsArray));
				argsArray = list.toArray(new String[list.size()]);
			}
			if (argsArray.length == 0) {
				if (new File(DEFAULT_CONFIG_FILE).exists()){
					argsArray = new String[] { "@", DEFAULT_CONFIG_FILE };
				} else {
					argsArray = new String[] { "-studio" };
				}
			}
		
			List<String> args = new ArrayList<String>(Arrays.asList(argsArray));
			
			final JNAeratorConfig config = jnaerator.config;//new JNAeratorConfig();
			config.preprocessorConfig.frameworksPath.addAll(JNAeratorConfigUtils.DEFAULT_FRAMEWORKS_PATH);
			new JNAeratorCommandLineArgs.ArgsParser() {

				
				boolean simpleGUI = false;
				NativePlatform arch = NativePlatform.getCurrentPlatform();//LibraryExtractor.getCurrentOSAndArchString();
                File libraryFileForCurrentArch = null;
				String currentLibrary = null;
				
				@Override
				List<String> parsed(ParsedArg a) throws Exception {
					switch (a.def) {					
					
					case AddIncludePath:
						File includedFile = a.getFileParam(0);
						if (includedFile.isFile())
							parsedActualFile(includedFile, false);
						else
							config.preprocessorConfig.includes.add(includedFile.toString());
                        
                        getPath(PathType.SourcePath).add(includedFile);
						break;
                    case DontCastConstants:
                        config.castConstants = false;
                        break;
					case CallbackInvokeName:
						config.callbackInvokeMethodName = a.getStringParam(0);
						break;
					case AddFrameworksPath:
						config.preprocessorConfig.frameworksPath.add(a.getFileParam(0).toString());
						break;
					case NoPreprocessing:
						config.preprocessorConfig.preprocess = false;
						break;
					case MaxConstructedFields:
						config.maxConstructedFields = a.getIntParam(0);
						break;
					case BeanStructs:
						config.beanStructs = true;
						break;
					case NoPrimitiveArrays:
						config.noPrimitiveArrays = true;
						break;
					case IfRegexMatch:
						String javaProperty = a.getStringParam(0),
								regex = a.getStringParam(1),
								thenCmd = a.getStringParam(2),
								elseCmd = a.getStringParam(3);
						String propValue = System.getProperty(javaProperty);
						if (propValue == null)
							propValue = "";
						return Arrays.asList(propValue.matches(regex) ? thenCmd : elseCmd);
                    case AddRootDir:
                        config.addRootDir(a.getFileParam("dir"));
                        break;
                    case ConvertBodies:
                        config.convertBodies = true;
                        break;
                    case NoAutoImports:
                        config.noAutoImports = true;
                        break;
                    case ForceOverwrite:
                        config.forceOverwrite = true;
                        break;
                    case MavenGroupId:
                        config.mavenGroupId = a.getStringParam(0);
                        break;
                    case MavenArtifactId:
                        config.mavenArtifactId = a.getStringParam(0);
                        break;
                    case MavenVersion:
                        config.mavenVersion = a.getStringParam(0);
                        break;
                    case OutputMode:
                        config.outputMode = a.getEnumParam(0, OutputMode.class);
                        break;
                    case NoJAR:
                        config.legacyNoJar = true;
                        break;
                    case NoCompile:
						config.legacyNoCompile = true;
						break;
                    case SkipLibInstance:
                    	config.skipLibraryInstanceDeclarations = true;
                    	break;
                    case COM:
                        config.microsoftCOM = true;
                        break;
					case NoStringReturns:
						config.stringifyConstCStringReturnValues = false;
						break;
					case SkipIncludedFrameworks:
						config.skipIncludedFrameworks = true;
						break;
					case GenPrivateMembers:
						config.skipPrivateMembers = false;
						break;
					case CPlusPlusGen:
						config.genCPlusPlus = true;
						break;
					case CurrentLibrary:
						currentLibrary = a.getStringParam(0);
						break;
					case CurrentPackage:
						config.packageName = a.getStringParam(0);
						break;
                    case OnlineDocURLFormat:
                        config.onlineDocumentationURLFormats.add(new Pair<MessageFormat, MessageFormat>(a.getMessageFormatParam(0), a.getMessageFormatParam(1)));
                        break;
                    case BeautifyNames:
                    	config.beautifyNames = true;
                    	break;
					case NoLibBundle:
						config.bundleLibraries = false;
						break;
					case DefaultLibrary:
						config.defaultLibrary = a.getStringParam(0);
						break;
					case RecursedExtensions:
						config.fileFilter = "*".equals(a.getStringParam(0)) ? null : new JNAeratorConfigUtils.FileExtensionFilter(a.getStringParam(0).split("[:;"));
						break;
					case DefineMacro:
						config.preprocessorConfig.macros.put(a.getStringParam(0), a.getStringParam(1));
						break;
					case DefineType:
						config.preprocessorConfig.forcedTypeDefs.put(a.getStringParam(0), a.getStringParam(1));
						break;
					case Direct:
						config.useJNADirectCalls = true;
						break;
					case EntryName:
						config.entryName = a.getStringParam(0);
						break;
					case ExtractSymbols:
						config.extractLibSymbols = true;
						break;
                    case Runtime:
                        config.runtime = a.getEnumParam(0, JNAeratorConfig.Runtime.class);
                        break;
                    case LibFile:
                        config.addLibraryFile(a.getFileParam(0), arch);
                        break;
					case File:
						return parsedFile(a);
					case FrameworksPath:
						config.preprocessorConfig.frameworksPath.clear();
						config.preprocessorConfig.frameworksPath.addAll(Arrays.asList(a.getStringParam(0).split(":")));
						break;
					case GUI:
						simpleGUI = true;
						break;
					case Help:
					case WikiDoc:
						JNAeratorCommandLineArgs.displayHelp(a.def == OptionDef.WikiDoc);
						throw new ExitException(0);
					case WCharAsShort:
						config.wcharAsShort = true;
						break;
					case Synchronized:
						config.synchronizedMethods = true;
						break;
					case JarOut:
						config.outputJar = a.getFileParam(0);
						break;
                    case ScalaStructSetters:
                        config.scalaStructSetters = true;
                        break;
					case NoComments:
						config.noComments = true;
						break;
					case LimitComments:
						config.limitComments = true;
						break;
					case MacrosOut:
						config.macrosOutFile = a.getFileParam(0);
						break;
					case GCCLong:
						config.gccLong = true;
						break;
					case SizeAsLong:
						config.sizeAsLong = true;
						break;
                    case GenRawBindings:
                        config.genRawBindings = true;
                        break;
                    case Undefine:
						config.undefines.add(a.getStringParam(0));
						break;
					case NoAuto:
						config.autoConf = false;
						break;
                    case Reification:
                        config.reification = true;
                        break;
					case NoCPP:
						config.noCPlusPlus = true;
						break;
					case ScalaOut:
						config.scalaOut = a.getFileParam(0);
						break;
//					case NoRuntime:
//						config.bundleRuntime = false;
//						break;
					case OutputDir:
						config.outputDir = a.getFileParam(0);
						break;
                    case LibraryNamingPrefixes:
                        config.libraryNamingPrefixes = a.getStringParam(0).split(",");
                        break;
					case PreferJavac:
						config.preferJavac = true;
						break;
					case BridgeSupportOutFile:
						config.bridgesupportOutFile = a.getFileParam(0);
						break;
					case ChoicesOut:
						config.choicesOutFile = a.getFileParam(0);
						break;
					case ChoicesIn:
						config.choicesInputFile = a.getFileParam(0);
						break;
					case PreprocessingOut:
						config.preprocessingOutFile = a.getFileParam(0);
						break;
					case ExtractionOut:
						config.extractedSymbolsOut = a.getFileParam(0);
						break;
						
					case Project:
						JNAeratorConfigUtils.readProjectConfig(a.getFileParam(0), a.getStringParam(1), config);
						break;
					case RootPackage:
						config.rootPackageName = a.getStringParam(0);
						break;
                    case RemoveInlineAsm:
                        config.removeInlineAsm = true;
                        break;
					case StructsInLibrary:
						config.putTopStructsInSeparateFiles = false;
						break;
                    case EmptyStructsAsForwardDecls:
                        config.treatEmptyStructsAsForwardDecls = true;
                        break;
                    case ParseInChunks:
                        config.parseInChunks = true;
                        break;
					case Studio:
						try {
							JNAeratorStudio.main(new String[0]);
							return null;
						} catch (Exception ex) {
							ex.printStackTrace();
							throw new ExitException(1);
						}
//					case Test:
//						try {
//							JUnitCore.main(JNAeratorTests.class.getName());
//							System.exit(0);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//							System.exit(1);
//						}
//						break;
					case Verbose:
						config.verbose = true;
						break;
					case Framework:
						config.frameworks.add(a.getStringParam(0));
						break;
					case IncludeArgs:
						return parsedArgsInclude(a);
					case Arch:
						arch = a.getEnumParam(0, NativePlatform.class);
                        libraryFileForCurrentArch = null;
						break;
					
					}
					return Collections.emptyList();
				}
				private void parsedActualFile(File file, boolean retainAsTarget) throws Exception {
					String lib = currentLibrary;
					String fn = file.getName();
                    String lfn = fn.toLowerCase();
                    if (!lfn.matches(".*?\\.(c|cpp|h|hpp|hxx|m)")) {
                        throw new CommandLineException("File '" + file + "' does not seem to be a C, C++ or Objective-C source file, nor a dynamic library, nor a *.jnaerator file.");
                    }
					if (lib == null) {
						String name = fn;
						int i = name.indexOf('.');
						if (i >= 0)
							name = name.substring(0, i).trim();
						if (name.length() > 0)
							lib = name;
						System.out.println("Warning: no -library option for file '" + fn + "', using \"" + lib + "\".");
					}
					config.addSourceFile(file, lib, !file.isFile(), retainAsTarget);
				}
				private List<String> parsedFile(ParsedArg a) throws Exception {
					File file = a.getFileParam(0);
					if (file != null) {
						String fn = file.getName();
						if (fn.startsWith("-") && !file.exists()) {
							JNAeratorCommandLineArgs.displayHelp(false);
                            throw new CommandLineException("Invalid switch : " + file);
						}
                        if (file.isFile() && fn.matches(".*\\.jnaerator"))
							return parsedArgsInclude(a);
                        else {
                            parsedArgs.add(Pair.create(OptionDef.File, Arrays.asList(a.getFileParam(0).toString())));
                            if (file.isDirectory() && fn.matches(".*\\.framework"))
                                config.frameworks.add(file.toString());
                            else if (fn.matches(".*\\.bridgesupport"))
                                config.bridgeSupportFiles.add(file);
                            else if (file.isFile() && isLibraryFile(file)) {
                                if (arch == null) {
                                    throw new CommandLineException("No arch defined for file " + file + " !\n" +
                                            "Please use the option " + OptionDef.Arch.clSwitch + " *on each binary file* with one of " + StringUtils.implode(NativePlatform.getPossiblePlatformsOfLibraryFile(file.toString()), ", "));
                                }
                                if (!arch.pattern.matcher(file.toString()).matches()) {
                                    throw new CommandLineException("File file " + file + " doesn't look like a native library for arch " + arch + " (expected file extension = '" + arch.extension + "') !");
                                }

                                if (config.verbose)
                                    System.out.println("Adding file '" + file + "' for arch '" + arch +"'.");
                                config.addLibraryFile(file, arch);

    //							if (libraryFileForCurrentArch == null) {
    //                                if (config.verbose)
    //                                    System.out.println("Adding file '" + file + "' for arch '" + arch +"'.");
    //                                config.addLibraryFile(currentLibrary, file, arch);
    //                                libraryFileForCurrentArch = file;
    //                            } else {
    //                                if (config.verbose)
    //                                    System.out.println("Adding dependency '" + file + "' for arch '" + arch +"'.");
    //                                config.addDependentLibraryFile(currentLibrary, file, arch);
    //                            }

                            } else {
                                String lib = currentLibrary;
                                if (file.isDirectory() && fn.endsWith(".xcode") ||
                                    file.isFile() && fn.toLowerCase().endsWith(".sln")) 
                                {
                                    JNAeratorConfigUtils.readProjectConfig(file, null, config);
                                } else {
                                    parsedActualFile(file, true);
                                }
                            }
                        }
                    }
					return Collections.emptyList();
				}

				private List<String> parsedArgsInclude(ParsedArg a) throws IOException {
					final File argsFile = a.getFileParam(0);
					
					String argsFileContent = ReadText.readText(argsFile);
					Adapter<String[], String> argVariableReplacer = new Adapter<String[], String>() {
						@Override
						public String adapt(String[] value) {
							String n = value[1];
							String v = System.getProperty(n);
							if (v == null)
								v = System.getenv(n);
							if (v == null && n.equals("DIR"))
								v = argsFile.getAbsoluteFile().getParent();
							return v;
						}
					};
					
					// Strip comments out
                    argsFileContent = argsFileContent.replaceAll("http://", "http:\\\\");
					argsFileContent = argsFileContent.replaceAll("(?m)//[^\n]*(\n|$)", "\n");
					argsFileContent = argsFileContent.replaceAll("http:\\\\", "http://");
					argsFileContent = argsFileContent.replaceAll("(?m)/\\*([^*]|\\*[^/])*\\*/", "");
					
					// Replace variables
					argsFileContent = RegexUtils.regexReplace(argVariablePattern, argsFileContent, argVariableReplacer);
					
					List<String> ret = new ArrayList<String>();
					List<String[]> tokens = RegexUtils.find(argsFileContent, argTokenPattern);
					for (String[] tokenMatch : tokens) {
						String token = tokenMatch[0];
						token = token.trim();
						if (token.startsWith("\"") && token.endsWith("\""))
							token = token.substring(1, token.length() - 1);
						
						if (token.length() == 0 || token.matches("^(//|#).*"))
							continue;
						
						boolean allowMissing = token.endsWith("?");
						if (token.contains("*")) {
							Collection<String> rs = FileListUtils.resolveShellLikeFileList(allowMissing ? token.substring(0, token.length() - 1) : token);
							for (String r : rs)
								ret.add(allowMissing ? r + "?" : r);
							if (!rs.isEmpty())
								continue;
						}
						ret.add(token);
					}
					return ret;
				}

				@Override
				void finished() throws IOException {
                    if (config.outputMode == null) {
                        if (config.legacyNoCompile && config.legacyNoJar) {
                            config.outputMode = OutputMode.Directory;
                            System.err.println("WARNING: legacy options " + OptionDef.NoJAR.clSwitch + " and " + OptionDef.NoCompile.clSwitch + " used, defaulting " + OptionDef.OutputMode.clSwitch + " to " + config.outputMode.name());                            
                        }
                        if (config.outputMode == null)
                            throw new CommandLineException("Missing output mode parameter " + OptionDef.OutputMode.clSwitch + " !");
                    }
                    
                    //System.out.println("Mode = " + config.outputMode.name());
                    config.parsedArgs = parsedArgs;
                    
					for (String framework : config.frameworks)
						JNAeratorConfigUtils.addFramework(config, framework);
					
					config.addRootDir(new File("."));
					for (String i : config.preprocessorConfig.includes) {
						try {
							config.addRootDir(new File(i));
						} catch (Exception ex) {}
					}	
					
					fixConfig(config);
                    
					if (config.verbose) {
						if (config.rawParsedSourcesOutFile == null)
							config.rawParsedSourcesOutFile = new File("_jnaerator.rawParsed.cpp");
						if (config.normalizedParsedSourcesOutFile == null)
							config.normalizedParsedSourcesOutFile = new File("_jnaerator.normalizedParsed.cpp");
						if (config.macrosOutFile == null)
							config.macrosOutFile = new File("_jnaerator.macros.cpp");
						if (config.choicesOutFile == null)
							config.choicesOutFile = new File("_jnaerator.choices");
						if (config.preprocessingOutFile == null)
							config.preprocessingOutFile = new File("_jnaerator.preprocessed.c");
						if (config.extractedSymbolsOut == null)
							config.extractedSymbolsOut = new File("_jnaerator.extractedSymbols.h");
						if (config.bridgesupportOutFile == null)
							config.bridgesupportOutFile = new File("_jnaerator.bridgesupport.h");
					}
					
					
					if (simpleGUI) {
						SimpleGUI gui = new SimpleGUI(config);
						feedback[0] = gui;
						gui.show();
					} else {
						feedback[0] = new Feedback() {
							
							@Override
							public void setStatus(String string) {
								//if (config.verbose)
									System.out.println(string);
							}
							
							@Override
							public void setFinished(Throwable e) {
								e.fillInStackTrace();
								throw new ExitException(1, e);
							}
							
							@Override
							public void setFinished(File toOpen) {
								System.out.println("#");
								System.out.println("# SUCCESS: JNAeration completed !");
								System.out.println("# Output mode is '" + config.outputMode.name() + "(" + config.outputMode.getDescription() + ")");
								System.out.println("#");
								if (config.outputMode.isMaven()) {
									System.out.println("# To build the resulting Maven project, use commands like :");
									System.out.println("#\tcd " + toOpen.getAbsolutePath());
									System.out.println("#\tmvn install");
								} else {
									System.out.println("# => '" + toOpen.getAbsolutePath() + "'");
								}
								System.out.println("#");
								throw new ExitException(0);
							}

							@Override
							public void sourcesParsed(SourceFiles sourceFiles) {
								
							}

							@Override
							public void wrappersGenerated(
									com.ochafik.lang.jnaerator.Result result) {
								// TODO Auto-generated method stub
								
							}
						}; 
					}

                    jnaerator.jnaerate(feedback[0]);
					if (!simpleGUI)
						throw new ExitException(0);
				}
				
			}.parse(args);
			
		} catch (ExitException e) {
			if (e.errorCode != 0) {
                finalError(e.getCause());
            }
		} catch (Exception e) {
            finalError(e);
		}
	}
    private static void finalError(Throwable e) {
        if (e != null)
            e.printStackTrace();
        System.err.println("#");
        System.err.println("# ERROR: JNAeration failed !");
        System.err.println("#");
        if (e != null) {
            if (e instanceof CommandLineException) {
                System.err.println("#\t" + e.getMessage().replaceAll("\n", "\n#\t"));
                System.err.println("#\tPlease use -h for help on the command-line options available.");
            } else {
                System.err.println("#\t" + e.toString().replaceAll("\n", "\n#\t"));
            }
            System.err.println("#");
        }
    }
    static class CommandLineException extends RuntimeException {
        public CommandLineException(String msg) {
            this(msg, null);
        }
        public CommandLineException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
	static class ExitException extends RuntimeException {
		int errorCode;
		public ExitException(int errorCode) {
            this(errorCode, null);
        }
        public ExitException(int errorCode, Throwable cause) {
			super(cause);
			this.errorCode = errorCode;
		}
	}

	public PrintWriter getClassSourceWriter(ClassOutputter outputter, String className) throws IOException {
        if (outputter == null)
            return newFileWriter(new File(config.sourcesOutputDir, className.replace('.', File.separatorChar) + ".java"));
		return outputter.getClassSourceWriter(className);
	}
	private static boolean isLibraryFile(File file) {
		String arg = file.getName().toLowerCase();
		return 
			arg.endsWith(".dll") || 
			arg.endsWith(".pdb") || 
			arg.endsWith(".dylib") || 
			arg.endsWith(".so") || 
			arg.endsWith(".jnilib");
	}
    protected void autoConfigure() {
        JNAeratorConfigUtils.autoConfigure(config);
    }
    
    PrintWriter newFileWriter(File file) throws IOException {
        return newFileWriter(file, false);
    }
    PrintWriter newFileOverwriter(File file) throws IOException {
        return newFileWriter(file, true);
    }
    PrintWriter newFileWriter(File file, boolean overwrite) throws IOException {
        if (file.exists()) {
            if (config.forceOverwrite || overwrite)
                System.out.println("Overwriting file '" + file + "'");
            else
                throw new IOException("File '" + file + "' already exists (use " + JNAeratorCommandLineArgs.OptionDef.ForceOverwrite.clSwitch + " to force overwrite).");
        }
        file.getAbsoluteFile().getParentFile().mkdirs();
        return new PrintWriter(file) {
            @Override
            public void print(String s) {
                super.print(s.replace("\r", "").replace("\n", StringUtils.LINE_SEPARATOR));
            }
        };
    }
	public void jnaerate(final Feedback feedback) {
		try {
			if (config.autoConf) {
				feedback.setStatus("Auto-configuring parser...");
				autoConfigure();
			}
            fixConfig(config);
            config.preprocessorConfig.macros.keySet().removeAll(config.undefines);
			
			if (config.verbose)
				JNAeratorConfigUtils.logger.log(Level.INFO, "Include path : \n\t" + StringUtils.implode(config.preprocessorConfig.includes, "\n\t"));
			
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			JavaCompiler javaCompiler = null;
			final MemoryFileManager javaCompilerMemoryFileManager;
			
			final ClassOutputter[] classOutputter = new ClassOutputter[1];
			if (config.outputMode.isJar()) {
                javaCompiler = CompilerUtils.getJavaCompiler(config.preferJavac);
                javaCompilerMemoryFileManager = new MemoryFileManager(javaCompiler.getStandardFileManager(diagnostics, null, null));
				classOutputter[0] = new ClassOutputter() {
					@Override
					public PrintWriter getClassSourceWriter(String className) throws FileNotFoundException {
						String path = "file:///" + className.replace('.', '/') + ".java";
						MemoryJavaFile c = new MemoryJavaFile(path, (String)null, JavaFileObject.Kind.SOURCE);
						javaCompilerMemoryFileManager.inputs.put(c.getPath().toString(), c);
						return new PrintWriter(c.openWriter());
					}
				};
			} else {
                javaCompilerMemoryFileManager = null;
                if (config.outputMode.isDirectory()) {
                    if (config.outputMode.isMaven()) {
                        String pom = config.runtime.generateMavenProjectModel(config.mavenGroupId, config.mavenArtifactId, config.mavenVersion);
                        PrintWriter pomOut = newFileWriter(new File(config.outputDir, POM_FILENAME));
                        pomOut.println(pom);
                        pomOut.close();
                    }
                    classOutputter[0] = new ClassOutputter() {
                        public PrintWriter getClassSourceWriter(String className) throws IOException {
                            File file = new File(JNAerator.this.config.sourcesOutputDir, className.replace('.', File.separatorChar) + ".java");
                            File parent = file.getParentFile();
                            if (!parent.exists())
                                parent.mkdirs();

                            feedback.setStatus("Generating " + file.getName());

                            return newFileWriter(file);
                        }
                    };
                }
			} 
			
            Result result = createResult(new ClassOutputter() {
                @Override
                public PrintWriter getClassSourceWriter(String className)
                        throws IOException {
                    PrintWriter w = JNAerator.this.getClassSourceWriter(classOutputter[0], className);
                    return new PrintWriter(w) {
                        StringBuilder bout = new StringBuilder();
                        @Override
                        public void print(String s) {
                            escapeUnicode(s, bout);
                            super.print(bout.toString());
                        }
                    };
                }
            }, feedback);
			
            if (config.outputMode == OutputMode.AutoGeneratedMaven) {
                String[] paths = new String[] {
                    "src/main/java",
                    "src/main/resources",
                    "src/test/java",
                    "src/test/resources"
                };
                for (String path : paths)
                    new File(config.outputDir, path).mkdirs();
                
                if (config.parsedArgs != null) {
                    List<String> lines = new ArrayList<String>();
                    lines.add("// Generated by JNAerator (" + JNAERATOR_URL + ")");
                    for (Pair<OptionDef, List<String>> parsedArg : config.parsedArgs) {
                        OptionDef opt = parsedArg.getKey();
                        List<String> switches = parsedArg.getValue();
                        switch (opt) {
                            case OutputMode:
                            case OutputDir:
                            case ForceOverwrite:
                                break;
                            default:
                                lines.add(StringUtils.implode(switches, " "));
                        }
                    }
                    //lines.add(OptionDef.OutputMode.format(OutputMode.AutoGeneratedMaven));
                    PrintWriter out = newFileWriter(new File(config.outputDir, "src/main/jnaerator/" + DEFAULT_CONFIG_FILE));
                    out.println(StringUtils.implode(lines, "\n"));
                    out.close();
                }
            } else if (config.outputMode.generatesSources()) {
                SourceFiles sourceFiles = parseSources(feedback, result.typeConverter);
                if (config.extractLibSymbols)
                    parseLibSymbols(sourceFiles, result);

                feedback.sourcesParsed(sourceFiles);

                ScalaGenerator sgen = null;
                if (config.scalaOut != null)
                    sgen = new ScalaGenerator(result);

                jnaerationCore(sourceFiles, result);
                if (sgen != null)
                	sgen.jnaerationCompleted();
                
                feedback.wrappersGenerated(result);
			
                if (javaCompiler != null) {
                    for (Map.Entry<String, String> cnAndSrc : config.extraJavaSourceFilesContents.entrySet()) {
                        javaCompilerMemoryFileManager.addSourceInput(cnAndSrc.getKey(), cnAndSrc.getValue());
                    }
                    feedback.setStatus("Compiling JNAerated files...");
                    CompilerUtils.compile(javaCompiler, javaCompilerMemoryFileManager, diagnostics, "1.5", config.cacheDir, config.runtime.libraryClass, 
                            JNAerator.class, NSClass.class, Rococoa.class, Mangling.class,
                            BridJ.class);
                    CompilerUtils.CompilationError.throwErrors(diagnostics.getDiagnostics(), javaCompilerMemoryFileManager.inputs, javaCompiler.getClass().getName());

                    if (config.outputMode == OutputMode.StandaloneJar) {
                        feedback.setStatus("Copying runtime classes...");
                        addRuntimeClasses(result, javaCompilerMemoryFileManager);
                    }
                }
                if (config.outputMode.isJar()) {
                    feedback.setStatus("Generating " + config.outputJar.getName());
                    javaCompilerMemoryFileManager.writeJar(config.outputJar, config.bundleSources, getResourceFiles());
                } else if (config.outputMode.isDirectory()) {
                    for (Map.Entry<String, File> e : getResourceFiles().entrySet()) {
                        File inFile = e.getValue();
                        File outFile = new File(config.resourcesOutputDir, e.getKey());
                        outFile.getAbsoluteFile().getParentFile().mkdirs();
                        
                        FileInputStream in = new FileInputStream(inFile);
                        FileOutputStream out = new FileOutputStream(outFile);
                        try {
                            IOUtils.readWrite(in, out);
                        } finally {
                            in.close();
                            out.close();
                        }
                    }
                }
            }
//			if (true)
//				throw new RuntimeException("no luck !");
			feedback.setFinished(config.outputJar != null ? config.outputJar : config.outputDir);
		} catch (ExitException ex) {
			throw ex;
		} catch (Throwable th) {
			feedback.setFinished(th);
		}
	}
	
	public void parseLibSymbols(SourceFiles sourceFiles, Result result) throws FileNotFoundException, IOException {
		PrintWriter fileOut = null;
		if (config.extractedSymbolsOut != null) {
			if (config.verbose)
				System.out.println("Writing symbols extracted from libraries to '" + config.extractedSymbolsOut + "'");
			fileOut = newFileOverwriter(config.extractedSymbolsOut);
		}
		
		for (File libFile : config.libraryFiles) {
			if (libFile.getName().toLowerCase().endsWith(".dll")) {
				try {
					result.feedback.setStatus("Extracting symbols from " + libFile.getName() + "...");
					
					SourceFile sf = new SourceFile();
					sf.setElementFile(libFile.toString());
					List<ParsedExport> dllExports = DllExport.parseDllExports(libFile);
					Map<String, Struct> cppClasses = new HashMap<String, Struct>();
					Pattern pubPat = Pattern.compile("(public|private|protected):(.*)");
					for (ParsedExport dllExport : dllExports) {
						//dllExport.mangling
						String dem = dllExport.demangled;
						Matcher m = pubPat.matcher(dem);
						String pub = null;
						if (m.matches()) {
							dem = m.group(2);
							pub = m.group(1);
						}
						String text = "// @mangling " + dllExport.mangling + "\n" + 
							dem + ";";
						ObjCppParser parser = new JNAeratorParser().newObjCppParser(result.typeConverter, text, false, null);//config.verbose);
						parser.setupScopes();
						Declaration decl = parser.declarationEOF();
						if (decl == null)
							continue;
						
						//for (Declaration decl : decls) {
							if (decl instanceof VariablesDeclaration && decl.getValueType() != null)
								decl.getValueType().addModifiers(ModifierType.Extern);
							decl.addModifiers(ModifierType.parseModifier(pub));
							if (decl instanceof Function) {
								Function f = (Function)decl;
								List<SimpleIdentifier> si = new ArrayList<SimpleIdentifier>(f.getName().resolveSimpleIdentifiers());
								Identifier ci;
								if (si.size() == 1) {
									String name = si.get(0) == null ? null : si.get(0).toString();
									String[] cm = name == null ? null : RegexUtils.match(name, classAndMethodNamePattern);
									if (cm == null) {
										sf.addDeclaration(decl);
										continue;
									}
									ci = ident(cm[0]);
									f.setName(ident(cm[1]));
								} else {
									si.remove(si.size() - 1);
									ci = new QualifiedIdentifier(QualificationSeparator.Colons, si);
								}
								if (dem.contains("__thiscall"))
									f.addModifiers(ModifierType.__thiscall);
								if (dem.contains("__fastcall"))
									f.addModifiers(ModifierType.__fastcall);
								
								Struct s = cppClasses.get(ci.toString());
								if (s == null) {
									s = new Struct();
									cppClasses.put(ci.toString(), s);
									s.setType(Struct.Type.CPPClass);
									s.setTag(ci.clone());
									sf.addDeclaration(decl(s));
								}
								Identifier n = f.getName().resolveLastSimpleIdentifier();
//										String ns = n.toString();
//										if (ns.startsWith("_"))
//											n = ident(ns.substring(1));
								f.setName(n);
								s.addDeclaration(f);
							} else
								sf.addDeclaration(decl);
						//}
					}
					if (!sf.getDeclarations().isEmpty()) {
						sourceFiles.add(sf);
						if (fileOut != null)
							fileOut.println(sf);
					}
					
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}
		if (fileOut != null)
			fileOut.close();
	}
	private Map<String, File> getResourceFiles() {

		Map<String, File> additionalFiles = new HashMap<String,File>();

		if (config.bundleLibraries) {
			for (Map.Entry<NativePlatform, List<File>> e : config.libraryFilesByArch.entrySet()) {
				NativePlatform arch = e.getKey();
				for (File libraryFile : e.getValue())
					additionalFiles.put(
						"lib/" + arch.name() + "/" + libraryFile.getName(), 
						libraryFile
					);
			}
//			for (String library : new HashSet<String>(config.libraryByFile.values())) {
//				String libraryFileName = System.mapLibraryName(library);
//				File libraryFile = new File(libraryFileName); 
//				//TODO lookup in library path
//				if (!libraryFile.exists() && libraryFileName.endsWith(".jnilib"))
//					libraryFile = new File(libraryFileName = libraryFileName.substring(0, libraryFileName.length() - ".jnilib".length()) + ".dylib");
//					
//				String key = "libraries/" + LibraryExtractor.getCurrentOSAndArchString() + "/" + libraryFile.getName();
//				if (additionalFiles.containsKey(key))
//					continue;
//				
//				if (libraryFile.exists()) {
//					System.out.println("Bundling " + libraryFile);
//					additionalFiles.put(key, libraryFile);
//				}// else {
//					//System.out.println("File " + libraryFileName + " not found");
//				//}
//			}
		}
		return additionalFiles;
	}
	protected static Map<String, URL> getRuntimeFiles(JNAeratorConfig.Runtime runtime, boolean needsObjCRuntime) throws IOException {
		ClassLoader classLoader = JNAerator.class.getClassLoader();
		Map<String, URL> ret = new HashMap<String, URL>();
		
		String listingFile = runtime.runtimeFilesListFileName;
        if (listingFile == null)
            return ret;

		List<String> files = new ArrayList<String>();
        for (String s : listingFile.split(","))
            files.addAll(ReadText.readLines(classLoader.getResourceAsStream(s.trim())));
		
		if (files == null) {
			throw new FileNotFoundException("Warning: Could not find JNAerator listing file '" + listingFile + "' : JNAerated files will need JNAerator in the path to execute.");
		}
		
		for (String file : files) {
            if (file.startsWith("Archive: "))
                continue;

            if (file.startsWith("META-INF/"))
                continue;

			if (!needsObjCRuntime) {
				if (file.contains("rococoa"))
					continue;
			}
			
			URL url = classLoader.getResource(file);
			if (url == null) {
				if (file.matches("com/sun/jna/[^/]+/(lib\\w+\\.(jnilib|so)|\\w+\\.dll)")) {
					System.out.println("JNA library missing : " + file);
					continue;
				}
				if (file.matches("com/ochafik/lang/jnaerator/runtime/scala/.*\\.part")) {
					System.out.println("Scala code missing : " + file);
					continue;
				}
                if (file.toLowerCase().contains("android"))
                    continue;
                
				throw new FileNotFoundException(file);
			}
			ret.put(file, url);
		}
		return ret;
	}
	protected void addRuntimeClasses(Result result, MemoryFileManager mfm) throws IOException {
		
		Map<String, URL> files = getRuntimeFiles(result.config.runtime, result.hasObjectiveC());
		
		boolean needsObjCRuntime = result.hasObjectiveC();
		for (Map.Entry<String, URL> e : files.entrySet()) {
            String file = e.getKey();
            URL url = e.getValue();
			
			file = "file:///" + file;
			if (!mfm.outputs.containsKey(file)) {
				mfm.outputs.put(file, new URLFileObject(url));
			}
		}
	}
	public static File getDir(String name) {
		File dir = new File(getDir(), name);
		dir.mkdirs();
		return dir;
	}
	public static File getDir() {
		File dir = new File(System.getProperty("user.home"));
		dir = new File(dir, ".jnaerator");
		dir = new File(dir, "temp");
		dir.mkdirs();
		return dir;
	}
	static boolean isHexDigit(char c) {
		return 
			c >= 'A' && c <= 'F' ||
			c >= 'a' && c <= 'f' ||
			Character.isDigit(c);
	}
	static void escapeUnicode(String s, StringBuilder bout) {
        if (s == null)
            return;
		bout.setLength(0);
		char[] chars = s.toCharArray();
		for (int iChar = 0, nChars = chars.length; iChar < nChars; iChar++) {
			char c = chars[iChar];
			int v = (int)c;
			if (v > 127) {
				bout.append("\\u");
				String h = Integer.toHexString(v);
				for (int i = 4 - h.length(); i-- != 0;)
					bout.append('0');
				bout.append(h);
			} else {
				// handle \\uXXXX -> \\uuXXXX transformation :
//				if (c == '\\' && 
//						iChar < nChars - 5 && 
//						chars[iChar + 1] == 'u' &&
//						isHexDigit(chars[iChar + 2]) &&
//						isHexDigit(chars[iChar + 3]) &&
//						isHexDigit(chars[iChar + 4]) &&
//						isHexDigit(chars[iChar + 5])
//				) {
//					bout.append("\\u");
//				}
				bout.append(c);
			}
		}
	}
			
	public SourceFiles parseSources(Feedback feedback, TypeConversion typeConverter) throws IOException, LexerException {
		feedback.setStatus("Parsing native headers...");
        return createJNAeratorParser().parse(config, typeConverter, null);
	}
    protected JNAeratorParser createJNAeratorParser() {
        return new JNAeratorParser();
    }

	
	public void addFile(File file, List<File> out) throws IOException {
		if (file.isFile()) {
			out.add(file);
		} else {
			File[] fs = file.listFiles();
			if (fs != null) {
				for (File f : fs) {
					addFile(f, out);
				}
			}
		}
	}
	class LibraryMapping {
        public Struct interf;
    }
    class JNALibraryMapping extends LibraryMapping {

    }
    protected void generateLibraryFiles(SourceFiles sourceFiles, Result result) throws IOException {
        switch (result.config.runtime) {
            case JNA:
            case JNAerator:
//            case JNAeratorNL4JStructs:
                generateJNALibraryFiles(sourceFiles, result);
                break;
            case BridJ:
                generateNL4JLibraryFiles(sourceFiles, result);
                break;
            default:
                throw new UnsupportedOperationException("Unexpected runtime : " + result.config.runtime);

        }
    }
    protected void generateNL4JLibraryFiles(SourceFiles sourceFiles, Result result) throws IOException {
        for (String library : result.libraries) {
			if (library == null)
				continue; // to handle code defined in macro-expanded expressions
//				library = "";

			Identifier javaPackage = result.javaPackageByLibrary.get(library);
			Identifier simpleLibraryClassName = result.getLibraryClassSimpleName(library);

			Identifier fullLibraryClassName = result.getLibraryClassFullName(library);//ident(javaPackage, libraryClassName);
			//if (!result.objCClasses.isEmpty())
			//	out.println("import org.rococoa.ID;");


			Struct interf = new Struct();
            interf.setType(Type.JavaClass);
			interf.addToCommentBefore("Wrapper for library <b>" + library + "</b>",
					result.declarationsConverter.getFileCommentContent(result.config.libraryProjectSources.get(library), null)
			);
			interf.addModifiers(ModifierType.Public);
			interf.setTag(simpleLibraryClassName);
            interf.addParent(ident(config.runtime.libraryClass, expr(typeRef(simpleLibraryClassName))));
            interf.addDeclaration(new Function(Function.Type.StaticInit, null, null).setBody(block(
				stat(methodCall(
					expr(typeRef(BridJ.class)),
					MemberRefStyle.Dot,
					"register"
				))
			)).addModifiers(ModifierType.Static));

//            String libFileOrDirArgName = "libraryFileOrDirectory";
//            Function constr = new Function(Function.Type.JavaMethod, fullLibraryClassName.resolveLastSimpleIdentifier().clone(), null, new Arg(libFileOrDirArgName, typeRef(File.class)));
//            constr.addModifiers(ModifierType.Public);
//            constr.setBody(block(stat(methodCall("super", varRef(libFileOrDirArgName)))));
//            interf.addDeclaration(constr);
//
//            constr = new Function(Function.Type.JavaMethod, fullLibraryClassName.resolveLastSimpleIdentifier().clone(), null);
//            constr.addModifiers(ModifierType.Public);
//            constr.addThrown(typeRef(FileNotFoundException.class));
//            constr.setBody(block(stat(methodCall("super", classLiteral(typeRef(fullLibraryClassName.clone()))))));
//            interf.addDeclaration(constr);

            fillLibraryMapping(result, sourceFiles, interf, library, javaPackage, fullLibraryClassName, varRef("this"));
        }
    }
	protected void generateJNALibraryFiles(SourceFiles sourceFiles, Result result) throws IOException {
		
		Struct librariesHub = null;
		PrintWriter hubOut = null;
		if (result.config.entryName != null) {
			librariesHub = new Struct();
			librariesHub.addToCommentBefore("JNA Wrappers instances");
			librariesHub.setType(Type.JavaClass);
			librariesHub.addModifiers(ModifierType.Public, ModifierType.Abstract);
			Identifier hubName = result.getHubFullClassName();
			librariesHub.setTag(hubName.resolveLastSimpleIdentifier());
			hubOut = result.classOutputter.getClassSourceWriter(hubName.toString());
			hubOut.println("package " + hubName.resolveAllButLastIdentifier() + ";");
			for (Identifier pn : result.javaPackages)
				if (!pn.equals(""))
					hubOut.println("import " + pn + ".*;");
		}
		for (String library : result.libraries) {
			if (library == null)
				continue; // to handle code defined in macro-expanded expressions
//				library = "";
			
			Identifier javaPackage = result.javaPackageByLibrary.get(library);
			Identifier simpleLibraryClassName = result.getLibraryClassSimpleName(library);
			
			Identifier fullLibraryClassName = result.getLibraryClassFullName(library);//ident(javaPackage, libraryClassName);
			//if (!result.objCClasses.isEmpty())
			//	out.println("import org.rococoa.ID;");
			
			
			Struct interf = new Struct();
			interf.addToCommentBefore("JNA Wrapper for library <b>" + library + "</b>",
					result.declarationsConverter.getFileCommentContent(result.config.libraryProjectSources.get(library), null)
			);
			if (hubOut != null)
				interf.addToCommentBefore("@see " + result.config.entryName + "." + library);
			
			interf.addModifiers(ModifierType.Public);
			interf.setTag(simpleLibraryClassName);
			
			Expression nativeLibFieldExpr = null;
			if (!result.config.skipLibraryInstanceDeclarations) {
				Expression libNameExpr = opaqueExpr(result.getLibraryFileExpression(library));
				TypeRef libTypeRef = typeRef(fullLibraryClassName);
				Expression libClassLiteral = result.typeConverter.typeLiteral(libTypeRef);
				
				boolean isJNAerator = result.config.runtime == JNAeratorConfig.Runtime.JNAerator;
				
				Expression libraryPathGetterExpr;
				if (isJNAerator)
					libraryPathGetterExpr = methodCall(
						expr(typeRef(LibraryExtractor.class)),
						MemberRefStyle.Dot,
						"getLibraryPath",
						libNameExpr,
						expr(true),
						libClassLiteral
					);
				else
					libraryPathGetterExpr = libNameExpr;
				
				String libNameStringFieldName = "JNA_LIBRARY_NAME", nativeLibFieldName = "JNA_NATIVE_LIB";
				interf.addDeclaration(new VariablesDeclaration(typeRef(String.class), new Declarator.DirectDeclarator(
					libNameStringFieldName,
					libraryPathGetterExpr
				)).addModifiers(ModifierType.Public, ModifierType.Static, ModifierType.Final));
				
				Expression libraryNameFieldExpr = memberRef(expr(libTypeRef.clone()), MemberRefStyle.Dot, ident(libNameStringFieldName));
				Expression optionsMapExpr = memberRef(expr(typeRef(MangledFunctionMapper.class)), MemberRefStyle.Dot, "DEFAULT_OPTIONS");
				Expression[] getInstArgs = isJNAerator ?
					new Expression[] { libraryNameFieldExpr.clone(), optionsMapExpr.clone() } :
					new Expression[] { libraryNameFieldExpr.clone() }
				;
				interf.addDeclaration(new VariablesDeclaration(typeRef(NativeLibrary.class), new Declarator.DirectDeclarator(
					nativeLibFieldName,
					methodCall(
						expr(typeRef(NativeLibrary.class)),
						MemberRefStyle.Dot,
						"getInstance",
						getInstArgs
					)
				)).addModifiers(ModifierType.Public, ModifierType.Static, ModifierType.Final));
				nativeLibFieldExpr = memberRef(expr(libTypeRef.clone()), MemberRefStyle.Dot, ident(nativeLibFieldName));
					
				if (result.config.useJNADirectCalls) {
					interf.addDeclaration(new Function(Function.Type.StaticInit, null, null).setBody(block(
						stat(methodCall(
							expr(typeRef(Native.class)),
							MemberRefStyle.Dot,
							"register",
							libraryNameFieldExpr.clone()
						))
					)).addModifiers(ModifierType.Static));
				} else {
					Expression[] loadLibArgs = isJNAerator ?
						new Expression[] { libraryNameFieldExpr.clone(), libClassLiteral, optionsMapExpr.clone() } :
						new Expression[] { libraryNameFieldExpr.clone(), libClassLiteral }
					;
					VariablesDeclaration instanceDecl = new VariablesDeclaration(libTypeRef, new Declarator.DirectDeclarator(
						librariesHub == null ? "INSTANCE" : library,
						cast(
							libTypeRef, 
							methodCall(
								expr(typeRef(Native.class)),
								MemberRefStyle.Dot,
								"loadLibrary",
								loadLibArgs
							)
						)
					)).addModifiers(ModifierType.Public, ModifierType.Static, ModifierType.Final);
					if (librariesHub != null) {
						librariesHub.addDeclaration(instanceDecl);
						librariesHub.addProtocol(fullLibraryClassName.clone());
					} else
						interf.addDeclaration(instanceDecl);
				}
			}

            boolean stdcall = false;
            List<Function> functions = result.functionsByLibrary.get(library);
            if (functions != null)
                for (Function function : functions)
                    if (function.hasModifier(ModifierType.__stdcall)) {
                        stdcall = true;
                        break;
                    }

            Identifier libSuperInter = ident(stdcall ? StdCallLibrary.class : config.runtime.libraryClass);

            if (result.config.useJNADirectCalls) {
				interf.addProtocol(libSuperInter);
				interf.setType(Type.JavaClass);
			} else {
				interf.addParent(libSuperInter);
				interf.setType(Type.JavaInterface);
			}

			fillLibraryMapping(result, sourceFiles, interf, library, javaPackage, fullLibraryClassName, nativeLibFieldExpr);
		}
		if (hubOut != null) {
			hubOut.println(librariesHub.toString());
			hubOut.close();
		}
	}

    protected void fillLibraryMapping(Result result, SourceFiles sourceFiles, Struct interf, String library, Identifier javaPackage, Identifier fullLibraryClassName, Expression nativeLibFieldExpr) throws IOException {

        Signatures signatures = result.getSignaturesForOutputClass(fullLibraryClassName);
        result.typeConverter.allowFakePointers = true;
        result.declarationsConverter.convertEnums(result.enumsByLibrary.get(library), signatures, interf, fullLibraryClassName);
        result.declarationsConverter.convertConstants(library, result.definesByLibrary.get(library), sourceFiles, signatures, interf, fullLibraryClassName);
        result.declarationsConverter.convertStructs(result.structsByLibrary.get(library), signatures, interf, fullLibraryClassName, library);
        result.declarationsConverter.convertCallbacks(result.callbacksByLibrary.get(library), signatures, interf, fullLibraryClassName);
        result.declarationsConverter.convertFunctions(result.functionsByLibrary.get(library), signatures, interf, fullLibraryClassName);

        if (result.globalsGenerator != null)
            result.globalsGenerator.convertGlobals(result.globalsByLibrary.get(library), signatures, interf, nativeLibFieldExpr, fullLibraryClassName, library);

        result.typeConverter.allowFakePointers = false;

        Set<String> fakePointers = result.fakePointersByLibrary.get(fullLibraryClassName);
        if (fakePointers != null)
        for (String fakePointerName : fakePointers) {
            if (fakePointerName.contains("::"))
                continue;

            Identifier fakePointer = ident(fakePointerName);
            if (!signatures.addClass(fakePointer))
                continue;

            Struct ptClass = result.declarationsConverter.createFakePointerClass(fakePointer);
            if (result.config.noComments)
                ptClass.addToCommentBefore("Pointer to unknown (opaque) type");
        
            interf.addDeclaration(decl(ptClass));
                
            if (result.config.reification)
                result.reifier.reifyFakePointer(ptClass, fullLibraryClassName, fakePointerName, signatures);

        }

        Set<String> undefinedTypes = result.undefinedTypesByLibrary.get(fullLibraryClassName);
        if (undefinedTypes != null)
            for (String undefinedTypeName : undefinedTypes) {
                if (undefinedTypeName.contains("::"))
                    continue;

                Identifier fakePointer = ident(undefinedTypeName);
                if (!signatures.addClass(fakePointer))
                    continue;

                Struct ptClass = result.declarationsConverter.publicStaticClass(fakePointer, null, Struct.Type.JavaInterface, null);
                ptClass.addToCommentBefore("Undefined type");
                interf.addDeclaration(decl(ptClass));
            }

        if (result.config.runtime == JNAeratorConfig.Runtime.BridJ) {
	        interf.addAnnotation(new Annotation(org.bridj.ann.Library.class, expr(library)));
	        interf.addAnnotation(new Annotation(org.bridj.ann.Runtime.class, classLiteral(result.hasCPlusPlus ? CPPRuntime.class : CRuntime.class)));
        }
		
        interf = result.notifyBeforeWritingClass(fullLibraryClassName, interf, signatures, library);
        if (interf != null) {
            final PrintWriter out = result.classOutputter.getClassSourceWriter(fullLibraryClassName.toString());

            //out.println("///\n/// This file was autogenerated by JNAerator (http://jnaerator.googlecode.com/), \n/// a tool written by Olivier Chafik (http://ochafik.com/).\n///");
            result.printJavaClass(javaPackage, interf, out);
            out.close();
        }
    }
    //Pattern rxObj1 = Pattern.compile("(\\w+)_([\\w_]+)_.*");
    /// To be overridden
	public Result createResult(final ClassOutputter outputter, Feedback feedback) {
		return new Result(config, outputter, feedback);
	}

	public static ObjCppParser newParser(String s) throws IOException {
		Result result = new Result(new JNAeratorConfig(), null, null);
		ObjCppParser parser = new ObjCppParser(new CommonTokenStream(new ObjCppLexer(
				new ANTLRReaderStream(new StringReader(s))))
		// , new DummyDebugEventListener()
		);
		parser.objCParserHelper = result.typeConverter;
		return parser;
	}
	protected void readChoices(Result result) throws IOException, RecognitionException {
		BufferedReader in = new BufferedReader(new FileReader(result.config.choicesInputFile));
		String line;

		List<Function> functions = null;

		int iLine = 0;
		while ((line = in.readLine()) != null) {
			iLine++;
			line = line.trim();
			if (line.startsWith("//"))
				continue;
			if (line.length() == 0)
				functions = null;



			Function function = null;
			if (functions == null) {
				function = newParser(line).javaMethodDeclaration();
			} else {
				function = newParser(line).functionDeclaration().function;
			}
			if (function == null) {
				System.err.println("Error: failed to parse function at line " + iLine + ": '" + line + "'");
				continue;
			}
			if (functions == null)
				result.declarationsConverter.functionAlternativesByNativeSignature.put(
					function.computeSignature(SignatureType.JavaStyle),
					new Pair<Function, List<Function>>(
						function,
						functions = new ArrayList<Function>()
					)
				);
			else
				functions.add(function);
		}

		System.err.println("Read " + result.declarationsConverter.functionAlternativesByNativeSignature.size() + " custom declarations from " + result.config.choicesInputFile);
	}
		
	public void jnaerationCore(SourceFiles sourceFiles, Result result) throws IOException, LexerException, RecognitionException {
		result.feedback.setStatus("Normalizing parsed code...");

		if (result.config.choicesInputFile != null)
			readChoices(result);
		
		if (config.rawParsedSourcesOutFile != null) {
			if (config.verbose)
				System.out.println("Writing raw parsed sources to '" + config.rawParsedSourcesOutFile + "'");
			WriteText.writeText(sourceFiles.toString(), config.rawParsedSourcesOutFile);
		}
		
		/// Perform Objective-C-specific pre-transformation (javadoc conversion for enums + find name of enums based on next sibling integer typedefs)
		sourceFiles.accept(new ObjectiveCToJavaPreScanner(result));

		/// Explode declarations to have only one direct declarator each
		sourceFiles.accept(new CToJavaPreScanner());
		
		/// Give sensible names to anonymous function signatures, structs, enums, unions, and move them up one level as typedefs
		sourceFiles.accept(new MissingNamesChooser(result));
		
		/// Move storage modifiers up to the storage
		sourceFiles.accept(new Scanner() {
			@Override
			protected void visitTypeRef(TypeRef tr) {
				super.visitTypeRef(tr);
				Element parent = tr.getParentElement();
				if (parent instanceof TypeRef) {// || parent instanceof VariablesDeclaration) {
					List<Modifier> stoMods = getStoMods(tr.getModifiers());
					if (stoMods != null) {
						List<Modifier> newMods = new ArrayList<Modifier>(tr.getModifiers());
						newMods.removeAll(stoMods);
						tr.setModifiers(newMods);
						((ModifiableElement)parent).addModifiers(stoMods);
					}
				}
			}
			public List<Modifier> getStoMods(List<Modifier> mods) {
				List<Modifier> ret = null;
				for (Modifier mod : mods) {
					if (mod.isA(ModifierKind.StorageClassSpecifier)) {
						if (ret == null)
							ret = new ArrayList<Modifier>();
						ret.add(mod);
					}
				}
				return ret;
			}
		});
		
		/// Build JavaDoc comments where applicable
		sourceFiles.accept(new JavaDocCreator(result));
		
//        checkNoCycles(sourceFiles);

		if (config.normalizedParsedSourcesOutFile != null) {
			if (config.verbose)
				System.out.println("Writing normalized parsed sources to '" + config.normalizedParsedSourcesOutFile + "'");
			WriteText.writeText(sourceFiles.toString(), config.normalizedParsedSourcesOutFile);
		}
		
		//##################################################################
		//##### BEGINNING HERE, sourceFiles NO LONGER GETS MODIFIED ! ######
		//##################################################################

        if (!result.config.bridgeSupportFiles.isEmpty()) {
            if (result.feedback != null)
                result.feedback.setStatus("Parsing BridgeSupport files...");

            new BridgeSupportParser(result, sourceFiles).parseBridgeSupportFiles();
        }
		
		/// Gather Objective-C classes
		sourceFiles.accept(result);
		result.rehabilitateWeakTypeDefs();
		
		result.chooseLibraryClasses(config.packageName, config.rootPackageName);
		
		//TODO resolve variables in visual studio projects
		//TODO Propagate unconvertible expressions, mark corresponding elements / trees as "to be commented out"
		
		/// Resolution's first pass : define relevant chained environment for each element
//		final DefinitionsVisitor definitions = new DefinitionsVisitor();
//		sourceFiles.accept(definitions);
		
		/// Resolve references of variables and types (map id -> type)
//		ResolutionScanner resolutions = new ResolutionScanner(definitions, originalOut);
//		sourceFiles.accept(resolutions);
		

		/// Filter unused symbols from implicitely included files
//		if (config.symbolsAccepter != null) {
//			originalOut.println("Filtering unused symbols");
//			UnusedScanner unused = new UnusedScanner(resolutions, config.symbolsAccepter, null);//originalOut);
//			sourceFiles.accept(unused);
//			unused.removeUnused(null);	
//		}
		
		
		/// Spit Objective-C classes out
		if (!result.classes.isEmpty() && result.objectiveCGenerator != null) {
			result.feedback.setStatus("Generating Objective-C classes...");
			result.objectiveCGenerator.generateObjectiveCClasses();
		}
		
		result.feedback.setStatus("Generating libraries...");
		
		if (result.libraries.size() == 1) {
			List<Define> list = result.definesByLibrary.get(null);
			if (list != null) {
				String lib = result.libraries.iterator().next();
				Result.getList(result.definesByLibrary, lib).addAll(list);
			}
		}
		
		result.symbols = Symbols.resolveSymbols(sourceFiles);
		generateLibraryFiles(sourceFiles, result);

		if (config.verbose)
			for (String unknownType : result.typeConverter.unknownTypes) 
				System.out.println("Unknown Type: " + unknownType);

		if (result.config.choicesOutFile != null) {
			PrintWriter out = newFileOverwriter(result.config.choicesOutFile);
			for (Map.Entry<String, Pair<Function, List<Function>>> e : result.declarationsConverter.functionAlternativesByNativeSignature.entrySet()) {
				Function f = e.getValue().getKey();
				String ff = f.getElementFile();
				if (ff != null)
					out.println("// " + ff + (f.getElementLine() > 0 ? ":" + f.getElementLine() : ""));

				out.println(f);
				for (Function alt : e.getValue().getValue()) {
					out.println(alt);
				}
				out.println();
			}
			out.close();
		}
	}
	private boolean checkNoCycles(SourceFiles sourceFiles) {
		final HashMap<Integer, Throwable> ids = new HashMap<Integer, Throwable>(new Arg().getId());
		sourceFiles.accept(new Scanner() {
			@Override
			protected void visitElement(Element d) {
				if (d != null && ids.put(d.getId(), new RuntimeException().fillInStackTrace()) != null) {
                    Throwable previous = ids.get(d.getId());
					throw new RuntimeException("Cycle : " + d, previous);
                }
				super.visitElement(d);
			}
		});
		return true;
	}

}
