package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.JNAeratorConfig.Runtime;
import com.ochafik.util.string.StringUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;	

public class JNAeratorCommandLineArgs {
	static abstract class ArgsParser {
		public static class ParsedArg {
			public OptionDef def;
			public Object[] params;
			
			public File getFileParam(String name) {
				return (File)params[def.getParam(name).position];
			}
			public String getStringParam(String name) {
				return (String)params[def.getParam(name).position];
			}
			public int getIntParam(String name) {
				return (Integer)params[def.getParam(name).position];
			}
			public File getFileParam(int pos) {
				return (File)params[pos];
			}
			public String getStringParam(int pos) {
				return (String)params[pos];
			}
            public MessageFormat getMessageFormatParam(int pos) {
				return (MessageFormat)params[pos];
			}
			public int getIntParam(int pos) {
				return (Integer)params[pos];
			}

            public <E extends Enum<E>> E getEnumParam(int pos, Class<E> ec) {
                return ec.cast(params[pos]);
            }
		}
		
		public void parse(List<String> args) throws Exception {
			for (int i = 0; i < args.size(); i++) {
				String arg = args.get(i);
				OptionDef defaultOpt = null;
				for (OptionDef opt : OptionDef.values()) {
					if (opt.switchPattern == null) {
						defaultOpt = opt;
						continue;
					}
					Matcher m = opt.switchPattern.matcher(arg);
					if (m.matches()) {
						ParsedArg pa = new ParsedArg();
						pa.def = opt;
						pa.params = new Object[opt.args.length];
						int iArg = 0;
						for (int iGroup = 0; iGroup < m.groupCount(); iGroup++) {
							String gp = m.group(iGroup + 1);
							if (gp == null)
								continue;
							pa.params[iArg] = opt.args[iArg].convertArg(gp);
							iArg++;
						}
						for (; iArg < opt.args.length; iArg++)
							pa.params[iArg] = opt.args[iArg].convertArg(args.get(++i));
						
						List<String> parsed = parsed(pa);
						if (parsed == null)
							return;
						args.addAll(i + 1, parsed);
						defaultOpt = null;
						break;
					}
				}
				if (defaultOpt != null) {
					ParsedArg pa = new ParsedArg();
					pa.def = defaultOpt;
					pa.params = new Object[] { defaultOpt.args[0].convertArg(arg) };
					args.addAll(i + 1, parsed(pa));
				}
			}
			finished();
		}
		
		/// Returns a list that must be inserted in place of this argument
		abstract List<String> parsed(ParsedArg a) throws Exception;
		abstract void finished() throws IOException;
	}
	
	public enum OptionDef {
	
		IncludeArgs(		"@(.+)?",				"Read command-line arguments from a file. File may contain multiple lines (those beginning with \"//\" will be skipped), file wildcards will be resolved within the file content, as well as variables substitutions : $(someEnvOrJavaVarName), with $(DIR) being the parent directory of the current arguments file.", new ArgDef(Type.ExistingFile, "argumentsFile.jnaerator")),
		OutputDir(			"-o", 					"Output directory for all artifacts", new ArgDef(Type.OutputDir, "outDir")),
		ExtractSymbols(		"-scanSymbols", 		"Extract, unmangle and parse the symbols all listed shared libraries"),
		AddIncludePath(		"-I(.+)?", 				"Add a directory to the include path. See doc of JNAERATOR_INCLUDE_PATH", new ArgDef(Type.File, "dir")),
		AddFrameworksPath(	"-F(.+)?", 				"Add a directory to the frameworks path. See doc of JNAERATOR_FRAMEWORKS_PATH", new ArgDef(Type.File, "dir")),
		FrameworksPath(		"-frameworksPath", 		"See doc of JNAERATOR_FRAMEWORKS_PATH", new ArgDef(Type.String, "path1:path2...")),
		Framework(			"-framework", 			"JNAerate a framework using its headers and its *.bridgesupport files if available", new ArgDef(Type.String, "frameworkName")),
		LimitComments(		"-limitComments", 		"Avoid useless comments (source file + line, skipped items...)"),
		NoComments(			"-noComments", 			"Don't output any member comment."),
		NoMangling(			"-noMangling", 			"Don't output any C++ name mangling information (may cause C++-decorated symbols not to be found at execution time)."),
		AddRootDir(         "-addRootDir",          "Remove this directory from the path of descendant source files in the generated documentation.", new ArgDef(Type.ExistingDir, "dir")), 
        NoCPP(				"-nocpp",		 		"Do not define the __cplusplus symbol"),
		Undefine(           "-U(.+)",               "Undefine a preprocessor symbol after the autoconfiguration phase.", new ArgDef(Type.String, "symbolName")),
        GUI(				"-gui",		 			"Show minimalist progression GUI"),
		NoRuntime(			"-noRuntime",		 	"Don't copy runtime classes to JAR output"),
		JarOut(				"-jar",					"Jar file where all generated sources and the compiled classes go", new ArgDef(Type.OutputFile, "outFile")),
		WCharAsShort(		"-wcharAsShort",		"Force treatment of wchar_t as short (char by default)"),
		//Test(				"-test",				"Launch JNAerator's unit tests (DEBUG option)"),
		Studio(				"-studio",				"Launch JNAeratorStudio"),
		ScalaOut(			"-scalaOut",				"[Experimental] Output Scala wrappers (callbacks implicits...)", new ArgDef(Type.OutputDir, "outDir")),
		NoStringReturns(	"-noStringReturns",		"Prevent const char* and const wchar_t* return types from being converted to String and WString."),
		Project(			"-project",				"Read Visual Studio 2008 project or solution file and use the configuration specified (e.g. \"Release|Win32\").", new ArgDef(Type.ExistingFile, "solutionFile"), new ArgDef(Type.String, "\"Config|Platform\"")),
		NoAuto(				"-noAuto",		 		"No auto-configuration of preprocessor symbols and paths"),
		GCCLong(			"-gccLong",				"Use GCC convention for size of 'long' (4 bytes on 32 bits platforms, 8 bytes on 64 bits platforms)."),
		SizeAsLong(			"-sizeAsLong",			"Treat size_t and ptrdiff_t values as 'long' values. ONLY HERE FOR COMPATIBILITY WITH PREVIOUS VERSIONS, WILL EVENTUALLY BE REMOVED."),
		Direct(				"-direct",		 		"JNAerate libraries that use JNA's faster direct call convention"),
		PreferJavac(		"-preferJavac",		 	"Use Sun's Javac compiler instead of Eclipse's ecj, if possible"),
		StructsInLibrary(	"-structsInLibrary",	"Force structs to be JNAerated as inner classes of their declaring libraries (otherwise, each top-level structure is defined as a top-level class in its library's package)"),
        OnlineDocURLFormat( "-onlineDoc",           "Define a format for online documentation URLs (uses MessageFormat syntax, with arg 0 being the name of the function / structure).", new ArgDef(Type.MessageFormat, "linkDisplayFormat"), new ArgDef(Type.MessageFormat, "urlMessageFormat")),
		CurrentPackage(		"-package",				"Set the Java package in which all the output will reside (by default, set to the library name).", new ArgDef(Type.String, "forcedPackageName")),
		RecursedExtensions(	"-allowedFileExts", 	"Colon-separated list of file extensions used to restrict files used when recursing on directories, or \"*\" to parse all files (by default = " + JNAeratorConfig.DEFAULT_HEADER_EXTENSIONS + ")", new ArgDef(Type.String, "extensions")),
		SkipIncludedFrameworks(		"-skipIncludedFrameworks",		"Skip Included Frameworks"),
		Runtime(            "-runtime",             "Choose target runtime library between " + StringUtils.implode(JNAeratorConfig.Runtime.values(), ", ") + " (default: " + JNAeratorConfig.Runtime.DEFAULT + ").", new ArgDef(Type.Enum, "enum", JNAeratorConfig.Runtime.class)),
        IfRegexMatch(		"-ifRegexMatch",		"Conditional evaluation of an argument if a java system property matches a regular expression", new ArgDef(Type.String, "javaProperty"), new ArgDef(Type.String, "regex"), new ArgDef(Type.String, "thenArg"), new ArgDef(Type.String, "elseArg")),
		DefineMacro(		"-D([^=]*)(?:=(.*))?", 	"Define a macro symbol", new ArgDef(Type.String, "name"), new ArgDef(Type.String, "value")),
		NoAutoImports(      "-noAutoImport",        "Don't add import statements automatically to output java source files"),
        RootPackage(		"-root(?:Package)?", 	"Define the root package for all output classes", new ArgDef(Type.String, "package")),
		CurrentLibrary(		"-library", 			"Define the name of the output library. This is a state parameter, it will affect all files listed after it, until another -library switch is provided. It does not affect sources included from a project file (Visual Studio...).\n" + 
													"C functions exported in library \"test\" will end up in class \"TestLibrary\", for instance. \n" +
													"The name of the library is the one fed to JNA to find the shared library, so library \"test\" must be in \"test.dll\" on Windows, \"libtest.dylib\" on Mac OS X and  \"libtest.so\" on other Unices.\n" +
													"Note that a special hack is done for library \"c\" on Windows systems : the output name is set to \"msvcrt\" instead of \"c\".\n", 
													new ArgDef(Type.String, "libName")),
		DefaultLibrary(		"-defaultLibrary", 		"Name of output library for elements declared in files not covered by a ${CurrentLibrary} switch", new ArgDef(Type.String, "libName")),
		Help(				"-h(?:elp)?", 			"Show command line arguments help"),
		EntryName(			"-entryClass",			"Generate a class _entryclassName.EntryClassName_ that will contain all of the jnaerated libraries instances. User code will just need to static import or derive from this class to access to the instances.", new ArgDef(Type.String, "entryClassName")),
//		Undefine(			"-U(.*)?",				"Undefine a preprocessor symbol before ", new ArgDef(Type.String, "entryClassName")),
		Verbose(			"-v(?:erbose)?",		"Verbose output (both console and files)"),
		ChoicesOut(			"-choicesOut",			"Write the function alternative choices made (automatically set when ${Verbose} is used).", new ArgDef(Type.OutputFile, "outFile")),
		ChoicesIn(			"-choices",				"Read the function alternative choices from a file in the format used by -choicesOut.", new ArgDef(Type.ExistingFile, "choicesFile")),
		PreprocessingOut(	"-preprocessingOut", 	"Write the preprocessor output in a file (automatically set when ${Verbose} is used).", new ArgDef(Type.OutputFile, "outFile")),
		ExtractionOut(		"-extractionOut", 		"Write the symbols extracted from libraries in a file (automatically set when ${Verbose} is used).", new ArgDef(Type.OutputFile, "outFile")),
		BridgeSupportOutFile("-bridgeSupportOut", 	"Write the definitions extracted from bridgesupport files in a file (automatically set when ${Verbose} is used).", new ArgDef(Type.OutputFile, "outFile")),
		WikiDoc(			"-wikiHelp",		 	"Output a wiki-friendly help"),
		Arch(				"-arch",		 		"Define the current architecture for libraries (state variable)", new ArgDef(Type.String, "archName")),
		MacrosOut(			"-macrosOut", 			"Write the preprocessor macros in a file (automatically set when ${Verbose} is used).", new ArgDef(Type.OutputFile, "outFile")),
		NoPrimitiveArrays(	"-noPrimitiveArrays",	"Never output primitive arrays for function arguments (use NIO buffers instead)"),
		File(				null,		 			"Any header (or directory containing headers at any level of hierarchy), shared library, *.bridgesupport file or *.jnaerator file", new ArgDef(Type.OptionalFile, "file")), 
		NoPreprocessing(	"-fpreprocessed",		"Consider source files as being already preprocessed (preprocessor won't be run)"),
		NoCompile(			"(?i)-noComp",				"Do not compile JNAerated headers"),
		NoJAR(				"(?i)-noJar",			"Do not create an output JAR"),
//		EnableCPlusPlus(	"-cppInstanceMethods",	"Enable experimental C++ instance methods wrapping"),
		NoLibBundle(		"(?i)-noLibBundle",		"Do not bundle libraries in output JAR"),
		LibFile(            "-libFile",             "Bundle the provided file with the JNAerated JAR so that it is extracted with the library when it is first used.", new ArgDef(Type.ExistingFile, "resourceFile")),
        MaxConstructedFields(
							"-maxConstrFields",		"Maximum number of fields allowed for structure fields constructors. If a struct has more fields, it will only get a default constructor.", new ArgDef(Type.Int, "fieldCount")),
		GenPrivateMembers(	"-genPrivateMembers", 	"Generate wrappers for private fields and methods (will be protected and deprecated)."),
		CPlusPlusGen(		"-genCPlusPlus",		"[Experimental, Not working at all] Generate C++ classes.");
		
		OptionDef(String clSwitch, String description, ArgDef... args) {
			this.clSwitch = clSwitch;
			this.description = description;
			this.args = args;
			switchPattern = clSwitch == null ? null : Pattern.compile(clSwitch);
			for (int i = 0; i < args.length; i++)
				args[i].position = i;
		}
		public String toString() {
			return super.toString() + ": " + description; 
		}
		public final ArgDef[] args;
		public final Pattern switchPattern;
		public final String clSwitch;
		public final String description;
		
		public enum Type {
			ExistingFile, ExistingDir, File, MessageFormat, String, Int, ExistingFileOrDir, OutputDir, OutputFile, OptionalFile, Enum
		}
		public static class ArgDef {
			public final Type type;
			public final String name;
			public int position;
            public final Class<?> additionalClass;
			public ArgDef(Type type, String name, Class<?> additionalClass) {
				this.type = type;
				this.name = name;
                this.additionalClass = additionalClass;
			}
            public ArgDef(Type type, String name) {
				this(type, name, null);
			}

            Object convertArg(String arg) throws FileNotFoundException {
                switch (type) {
                case OptionalFile:
                    boolean opt = arg.endsWith("?");
                    File f = new File(opt ? arg.substring(0, arg.length() - 1 ) : arg);
                    if (!f.exists()) {
                        if (opt)
                            return null;
                        throw new FileNotFoundException(f.toString());
                    }
                    return f;
                case File:
                    return new File(arg);
                case Int:
                    return Integer.parseInt(arg);
                case MessageFormat:
                    return new MessageFormat(arg);
                case String:
                    return arg;
                case ExistingDir:
                    f = new File(arg);
                    if (!f.isDirectory())
                        throw new FileNotFoundException(f.toString());
                    return f;
                case ExistingFile:
                    f = new File(arg);
                    if (!f.isFile())
                        throw new FileNotFoundException(f.toString());
                    return f;
                case ExistingFileOrDir:
                    f = new File(arg);
                    if (!f.exists())
                        throw new FileNotFoundException(f.toString());
                    return f;
                case Enum:
                    return Enum.valueOf((Class<? extends Enum>)additionalClass, arg);
                case OutputDir:
                    f = new File(arg);
                    if (f.isFile())
                        throw new FileNotFoundException("Expected directory, found file : " + f.toString());
                    f.getAbsoluteFile().getParentFile().mkdirs();
                    return f;
                case OutputFile:
                    f = new File(arg);
                    if (f.isDirectory())
                        throw new FileNotFoundException("Expected file, found directory : " + f.toString());
                    f.getAbsoluteFile().getParentFile().mkdirs();
                    return f;
                }
                throw new UnsupportedOperationException();
            }
		}
		public ArgDef getParam(String name) {
			for (ArgDef ad : args)
				if (ad.name.equals(name))
					return ad;
			throw new NoSuchElementException("Argument parameter '" + name + "' in option " + this);
		}
		
	}

	static void displayHelp(boolean wikiFormat) {
		List<OptionDef> opts = new ArrayList<OptionDef>(Arrays.asList(OptionDef.values()));
		Collections.sort(opts, new Comparator<OptionDef>() {
			@Override
			public int compare(OptionDef o1, OptionDef o2) {
				if (o1.clSwitch == null)
					return o2.clSwitch == null ? 0 : -1;
				if (o2.clSwitch == null)
					return 1;
				return o1.clSwitch.compareTo(o2.clSwitch);
			}
			
		});
		if (wikiFormat) {
			for (OptionDef opt : opts) {
				System.out.print(" * *" + (opt.clSwitch == null ? "" : opt.clSwitch) +"*");
				for (OptionDef.ArgDef ad : opt.args) {
					System.out.print(" <" + ad.name + ": " + ad.type + ">");
				}
				System.out.println();
				System.out.println("  " + opt.description.replaceAll("\\*", "`*`").replaceAll("\n", "\n  "));
			}
		} else {
			System.out.println("Credits:   JNAerator is Copyright (c) 2008-2009 Olivier Chafik");
			System.out.println("           Includes Anarres JCPP (Apache 2.0 license), Copyright (c) 2007-2008, Shevek");
			System.out.println("           Includes Java Native Access (JNA) (LGPL license), Copyright (c) 2006-2009 Todd Fast, Timothy Wall, Wayne Meissner and others");
			System.out.println("           Includes Rococoa (LGPL license), Copyright (c) Copyright Duncan McGregor and others");
			System.out.println("           Includes ANTLR's runtime (BSD license), Copyright (c) 2003-2008, Terence Parr");
			System.out.println("           Licensing & Copyright details : http://code.google.com/p/jnaerator/wiki/CreditsAndLicense");
			
			for (OptionDef opt : opts) {
				System.out.print("\t" + (opt.clSwitch == null ? "" : opt.clSwitch));
				for (OptionDef.ArgDef ad : opt.args) {
					System.out.print(" <" + ad.name + ": " + ad.type + ">");
				}
				System.out.println();
				System.out.println("\t\t" + opt.description);
				System.out.println();
			}
		}
	}
}