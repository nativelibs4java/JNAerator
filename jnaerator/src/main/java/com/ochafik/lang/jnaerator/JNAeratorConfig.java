/*
 Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
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

import com.ochafik.io.ReadText;
import com.ochafik.lang.jnaerator.JNAeratorCommandLineArgs.OptionDef;
import org.bridj.cpp.com.IID;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridj.ann.Array;
import com.ochafik.lang.jnaerator.JNAeratorConfigUtils.FileExtensionFilter;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.ElementsHelper;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.util.CompoundCollection;
import com.ochafik.util.listenable.Adapter;
import com.ochafik.util.listenable.Filter;
import com.ochafik.util.listenable.Pair;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import java.util.logging.Level;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import java.io.*;
import java.util.Collections;
import java.util.regex.Pattern;

public class JNAeratorConfig {

    public enum Compiler {

        GCC4, MSVC9
    }

    public enum Architecture {

        x86_64, i386, PowerPC
    }

    public enum Platform {

        Windows, Linux, MacOSX
    }

    public enum Runtime {

        JNA(false, true, false,
        com.sun.jna.Callback.class,
        com.sun.jna.Pointer.class,
        com.sun.jna.Memory.class,
        com.sun.jna.Structure.class,
        com.sun.jna.Union.class,
        null,
        null,
        com.sun.jna.Library.class,
        null,
        "jna-runtime.jar.files") {
            public DeclarationsConverter createDeclarationsConverter(Result result) {
                return new JNADeclarationsConverter(result);
            }

            public GlobalsGenerator createGlobalsGenerator(Result result) {
                return new JNAGlobalsGenerator(result);
            }

            ;
            public TypeConversion createTypeConversion(Result result) {
                return new JNATypeConversion(result);
            }
        },
        JNAerator(false, true, false,
        com.sun.jna.Callback.class,
        com.sun.jna.Pointer.class,
        com.sun.jna.Memory.class,
        com.ochafik.lang.jnaerator.runtime.Structure.class,
        com.ochafik.lang.jnaerator.runtime.Union.class,
        null,
        null,
        com.sun.jna.Library.class,
        null,
        "jnaerator-runtime.jar.files") {
            public DeclarationsConverter createDeclarationsConverter(Result result) {
                return new JNADeclarationsConverter(result);
            }

            public GlobalsGenerator createGlobalsGenerator(Result result) {
                return new JNAGlobalsGenerator(result);
            }

            ;
            public TypeConversion createTypeConversion(Result result) {
                return new JNATypeConversion(result);
            }

            @Override
            public String toString() {
                return "JNAerator (based on JNA)";
            }
        },
        BridJ(true, false, true,
        org.bridj.Callback.class,
        org.bridj.Pointer.class,
        null, //org.bridj.Memory.class,
        null,//org.bridj.Struct.class,
        null,//org.bridj.Struct.class,
        null, //org.bridj.StructIO.class,
        org.bridj.Pointer.class,// "array" class
        null, //org.bridj.NativeLib.class,
        org.bridj.ann.Bits.class,
        "bridj.jar.files") {
            public DeclarationsConverter createDeclarationsConverter(Result result) {
                return new BridJDeclarationsConverter(result);
            }

            public GlobalsGenerator createGlobalsGenerator(Result result) {
                return new BridJGlobalsGenerator(result);
            }

            ;
            public TypeConversion createTypeConversion(Result result) {
                return new BridJTypeConversion(result);
            }

            @Override
            public String toString() {
                return "BridJ (faster runtime that supports C++)";
            }
        },
        NodeJS(false, true, false,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null) {
            public DeclarationsConverter createDeclarationsConverter(Result result) {
                return new NodeJSDeclarationsConverter(result);
            }

            public GlobalsGenerator createGlobalsGenerator(Result result) {
                return new NodeJSGlobalsGenerator(result);
            }

            ;
            public TypeConversion createTypeConversion(Result result) {
                return new NodeJSTypeConversion(result);
            }

            @Override
            public String toString() {
                return "NodeJS (experimental native library bindings for node.js)";
            }

            @Override
            public String generateMavenProjectModel(String groupId, String artifactId, String version) throws IOException {
                return "TODO"; // use antrun, or don't even bother?
            }

            @Override
            public boolean renameFunctionSignatures() {
                return false;
            }
        };
        public static final Runtime DEFAULT = BridJ;//JNAerator;

        public boolean renameFunctionSignatures() {
            return true;
        }

        public enum Ann {

            Bits,
            FastCall,
            StdCall,
            ObjCBlock,
            This,
            ThisCall,
            Length,
            ByValue,
            Field,
            Symbol,
            Name,
            Union,
            Virtual,
            Constructor,
            IID
        }

        Runtime(boolean hasFastStructs,
                boolean hasJNA,
                boolean hasBitFields,
                Class<?> callbackClass,
                Class<?> pointerClass,
                Class<?> memoryClass,
                Class<?> structClass,
                Class<?> unionClass,
                Class<?> structIOClass,
                Class<?> arrayClass,
                Class<?> libraryClass,
                Class<? extends Annotation> someAnnotationClass,
                String runtimeFilesListFileName) {
            this.hasFastStructs = hasFastStructs;
            this.hasBitFields = hasBitFields;
            this.hasJNA = hasJNA;
            this.callbackClass = callbackClass;
            this.pointerClass = pointerClass;
            this.memoryClass = memoryClass;
            this.structClass = structClass;
            this.libraryClass = libraryClass;
            this.unionClass = unionClass;
            this.structIOClass = structIOClass;
            this.arrayClass = arrayClass;
            this.runtimeFilesListFileName = runtimeFilesListFileName;
            annotationPackage = someAnnotationClass == null ? null : someAnnotationClass.getPackage().getName();
        }

        public abstract DeclarationsConverter createDeclarationsConverter(Result result);

        public abstract GlobalsGenerator createGlobalsGenerator(Result result);

        public abstract TypeConversion createTypeConversion(Result result);
        public final String runtimeFilesListFileName;
        private String annotationPackage;

        public SimpleTypeRef typeRef(Ann ann) {
            if (annotationPackage == null) {
                return null;
            }
            String n = ann.toString();
            if (this == BridJ) {
                if (ann == Ann.Length) {
                    n = Array.class.getSimpleName();
                } else if (ann == Ann.IID) {
                    return ElementsHelper.typeRef(ident(IID.class));
                }
            }
            List<String> elts = new ArrayList<String>();
            elts.addAll(Arrays.asList(annotationPackage.split("\\.")));
            elts.add(n);
            return annotationPackage == null ? null : ElementsHelper.typeRef(ident(elts.toArray(new String[elts.size()])));
        }
        public final Class callbackClass, pointerClass, memoryClass, structClass, unionClass, structIOClass, arrayClass, libraryClass;
        public final boolean hasFastStructs;
        public final boolean hasJNA;
        public final boolean hasBitFields;

        public String generateMavenProjectModel(String groupId, String artifactId, String version) throws IOException {
            String res = "com/ochafik/lang/jnaerator/" + name() + "-pom.xml";
            String pom = ReadText.readText(getClass().getClassLoader().getResource(res));
            if (pom == null) {
                throw new FileNotFoundException(res);
            }
            pom = pom.
                    replaceAll("%groupId%", groupId).
                    replaceAll("%artifactId%", artifactId).
                    replaceAll("%version%", version);

            return pom;
        }
    }

    public enum GenFeatures {

        Compile,
        FileComments,
        UsageComments,
        EnumTypeLocationComments,
        LibrariesAutoExtraction,
        CPlusPlusMangling,
        StructConstructors,
        TypedPointersForForwardDeclarations,
        OriginalFunctionSignatures,
        FunctionArgsJavaDoc
    }

    public enum OutputMode {

        Jar("JAR with bindings only"),
        StandaloneJar("JAR with bindings and runtime dependencies"),
        Directory("Bindings sources in simple file hierarchy"),
        Maven("Bindings sources in Maven project ready to build"),
        AutoGeneratedMaven("Maven project that automatically regenerates its bindings");
        private final String desc;

        OutputMode(String desc) {
            this.desc = desc;
        }

        public String getDescription() {
            return desc;
        }

        @Override
        public String toString() {
            return "'" + name() + "' : " + desc;
        }

        boolean isJar() {
            return this == Jar || this == StandaloneJar;
        }

        boolean isDirectory() {
            return this == Directory || isMaven();
        }

        boolean isMaven() {
            return this == Maven || this == AutoGeneratedMaven;
        }

        boolean generatesSources() {
            return this != AutoGeneratedMaven;
        }
    }
    public static final String DEFAULT_HEADER_EXTENSIONS = "h:hpp:hxx";
    public static final String DEFAULT_IMPLEMS_EXTENSIONS = "cpp:c:cxx:m:mm";
    public final EnumSet<GenFeatures> features = EnumSet.allOf(GenFeatures.class);
    public Runtime runtime = Runtime.DEFAULT;

    public static class PreprocessorConfig {

        public boolean WORKAROUND_PP_BUGS = true;
        public final List<String> explicitIncludes = new ArrayList<String>();
        public final List<String> implicitIncludes = new ArrayList<String>();
        public final Map<String, String> implicitMacros = new LinkedHashMap<String, String>();
        public final Map<String, String> explicitMacros = new LinkedHashMap<String, String>();
        public final Map<String, String> forcedTypeDefs = new LinkedHashMap<String, String>();
        public final List<String> frameworksPath = new ArrayList<String>();
        public List<String> includeStrings = new ArrayList<String>();
        public boolean preprocess = true;

        public List<String> getAllIncludes() {
            List<String> list = new ArrayList<String>(explicitIncludes);
            list.addAll(implicitIncludes);
            return Collections.unmodifiableList(list);
        }

        public Map<String, String> getAllMacros() {
            Map<String, String> macros = new LinkedHashMap<String, String>();
            macros.putAll(implicitMacros);
            macros.putAll(explicitMacros);
            return macros;
        }
    }
    public long fullParsingTimeout = 5000, sliceParsingTimeout = 1000;
    public final JNAeratorConfig.PreprocessorConfig preprocessorConfig = new JNAeratorConfig.PreprocessorConfig();
    public boolean followIncludes;
    boolean microsoftCOM;
    public File preprocessingOutFile, macrosOutFile;
    public File choicesOutFile, choicesInputFile;
    public boolean useJNADirectCalls;
    public boolean limitComments, noComments;
    public boolean putTopStructsInSeparateFiles = true;
    //public boolean bundleRuntime = true;
    public boolean genRawBindings = true;
    public boolean beautifyNames;
    public boolean treatEmptyStructsAsForwardDecls;
    public String[] libraryNamingPrefixes;
    public boolean extractLibSymbols;
    public final List<Pattern> skippedStructNames = new ArrayList<Pattern>();
    public final List<Pattern> skippedFunctionNames = new ArrayList<Pattern>();
    public final List<Pattern> skippedEnumNames = new ArrayList<Pattern>();
    //public boolean fastStructs;
    public List<Pair<MessageFormat, MessageFormat>> onlineDocumentationURLFormats = new ArrayList<Pair<MessageFormat, MessageFormat>>();
    public String entryName;
    public boolean noStaticInit;
    public int maxConstructedFields = 10;
    public boolean beanStructs;
    public boolean reification;
    public boolean convertBodies;
    public boolean removeInlineAsm;
    public Map<String, String> extraJavaSourceFilesContents = new LinkedHashMap<String, String>();
    public Set<String> frameworks = new LinkedHashSet<String>();
    boolean skipIncludedFrameworks;
    public FileFilter fileFilter = new FileExtensionFilter(DEFAULT_HEADER_EXTENSIONS.split("[:;]"));
    public Map<NativePlatform, List<File>> libraryFilesByArch = new LinkedHashMap<NativePlatform, List<File>>();
    public List<File> libraryFiles = new ArrayList<File>();
    public Map<String, List<File>> sourceFilesByLibrary = new HashMap<String, List<File>>();
    public final Map<File, String> libraryByDirectory = new HashMap<File, String>();
    public Map<File, String> libraryByFile = new LinkedHashMap<File, String>();

    public void addFramework(String framework) throws IOException {
        File file = JNAeratorConfigUtils.getFrameworkDirectory(framework, preprocessorConfig.frameworksPath);
        frameworks.add(framework);

        File headers = new File(file, "Headers");
        if (headers.exists()) {
            preprocessorConfig.implicitIncludes.add(headers.getAbsolutePath());
            File mainHeader = new File(headers, framework + ".h");
            if (mainHeader.exists()) {
                addSourceFile(mainHeader, framework, true, true, false);
            } else {
                addSourceFile(headers, framework, true, true, false);
            }
        } else {
            new IOException("No Headers subdirectory in framework '" + framework + "' found here : " + file).printStackTrace();
        }

        File naturalDir = new File(file, "Resources/BridgeSupport");
        File f;
        f = new File(naturalDir, framework + ".bridgesupport");
        if (!f.exists()) {
            f = new File(naturalDir, framework + "Full.bridgesupport");
        }

        if (f.exists()) {
            bridgeSupportFiles.add(f);
        }
    }

    public void addLibraryFile(File file, NativePlatform arch) {

        List<File> others = libraryFilesByArch.get(arch);
        if (others == null) {
            libraryFilesByArch.put(arch, others = new ArrayList<File>());
        }

        String fn = file.getName();
        int i = fn.lastIndexOf('.');
        if (i > 0) {
            fn = fn.substring(0, i);
        }

        others.add(file);
        libraryByFile.put(file, fn);
        libraryFiles.add(file);
    }

    public void addSourceFile(File file, String library, boolean applyFilters, boolean retainAsTarget, boolean indexSourceFilesByLibrary) throws IOException {
        if (file.isFile()) {
            if (fileFilter == null || !applyFilters || fileFilter.accept(file)) {
                file = file.getCanonicalFile();
                if (library == null && fileToLibrary != null) {
                    library = fileToLibrary.adapt(file);
                }
                sourceFiles.add(file);
                if (retainAsTarget) {
                    libraryByFile.put(file, library);

                    File directory = file.getParentFile().getAbsoluteFile();
                    String oldLib = libraryByDirectory.put(directory, library);
                    if (oldLib != null && !oldLib.equals(library)) {
                        JNAerator.logger.log(Level.WARNING, "Directory " + directory + " contains files from different libraries, so there won't be any default library for its files (symbols defined in files from that library that were included but not explicitly listed will not be JNAerated).");
                        libraryByDirectory.put(directory, "");
                    }
                }
                if (library != null && indexSourceFilesByLibrary) {
                    List<File> files = sourceFilesByLibrary.get(library);
                    if (files == null) {
                        sourceFilesByLibrary.put(library, files = new ArrayList<File>());
                    }
                    files.add(file);
                }
            }
        } else {
            File[] fs = file.listFiles();
            if (fs != null) {
                for (File f : fs) {
                    addSourceFile(f, library, true, retainAsTarget, indexSourceFilesByLibrary);
                }
            }
        }
    }

    public JNAeratorConfig() {
//		if (System.getenv("POINTER_CLASSES") == null)
//			features.remove(GenFeatures.TypedPointersForForwardDeclarations);
    }
    public boolean verbose;
    boolean parseInChunks;
    public File outputDir, sourcesOutputDir, resourcesOutputDir;
    public List<String> rootDirectoriesPrefixesForSourceComments = new ArrayList<String>();
    public Adapter<Function, Boolean> functionsAccepter;
    public String packageName = null, rootPackageName = null;
    public String defaultLibrary;
    public Map<String, File> libraryProjectSources = new LinkedHashMap<String, File>();
    public Map<String, String> extractedLibraries = new HashMap<String, String>();
    public Adapter<File, String> fileToLibrary = new Adapter<File, String>() {
        public String adapt(File file) {
            String libraryName = null;
            try {
                //String canoFile = value.getCanonicalPath();
                //libraryFile = libraryByFile.get(canoFile);
                file = file.getCanonicalFile();
                libraryName = libraryByFile.get(file);

                if (libraryName == null) {
                    libraryName = libraryByDirectory.get(file.getParentFile());
                }
                //if (value.toString().startsWith("\""))
                //	new Exception("Double quotes in file !").printStackTrace();
//				if (!canoFile.contains("Program Files")) {
//					System.out.println("libraryByFile = " + libraryByFile);
//					System.out.println("libraryByFile(" + canoFile + ") = " + libraryFile);
//					System.out.println("    value = " + value);
//					System.out.println("can value = " + value.getCanonicalFile());
//					System.out.println("abs value = " + value.getAbsoluteFile());
//				}

            } catch (IOException e) {
                e.printStackTrace();
            }
            return libraryName == null ? defaultLibrary : libraryName;
        }
    };

    public void addRootDir(File dir) throws IOException {
        if (!dir.exists()) {
            return;
        }
        String str = dir.getCanonicalPath();
        if (!str.endsWith(File.separator)) {
            str += File.separator;
        }
        if (!rootDirectoriesPrefixesForSourceComments.contains(str)) {
            rootDirectoriesPrefixesForSourceComments.add(str);
        }
    }
    public Filter<Element> symbolsAccepter = new Filter<Element>() {
        public boolean accept(Element value) {
            String s = Element.getFileOfAscendency(value);
            if (s == null) {
                return false;
            }

            File f = new File(s);
            try {
                f = f.getCanonicalFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return libraryByFile.containsKey(f);
        }
    };

    public Set<String> getLibraries() {
        Set<String> ret = new HashSet<String>();
        for (Map.Entry<File, String> e : libraryByFile.entrySet()) {
            if (e.getValue() != null) {
                ret.add(e.getValue());
            }
        }
        return ret;
    }
    public String libraryForElementsInNullFile;
    public String cPlusPlusNameSpaceSeparator = "_";
    public boolean preferJavac;
    public Set<File> bridgeSupportFiles = new LinkedHashSet<File>();
    public File outputJar;
    public File cacheDir;
    public boolean autoConf = true;
    public boolean forceOverwrite;
    public Set<String> undefines = new HashSet<String>();
    public boolean gccLong, sizeAsLong;
    public OutputMode outputMode;// = OutputMode.Maven;
    @Deprecated
    public boolean legacyNoJar;
    @Deprecated
    public boolean legacyNoCompile;
    public boolean noAutoImports;
    public boolean bundleSources = true;
    public boolean noCPlusPlus;
    public String mavenGroupId = "com.mycompany", mavenArtifactId = "my-native-bindings", mavenVersion = "1.0-SNAPSHOT";

    public String getLibrary(String elementFile) {
        if (elementFile == null) {
            return libraryForElementsInNullFile;
        }

        return fileToLibrary == null
                ? defaultLibrary
                : fileToLibrary.adapt(new File(elementFile));
    }
    Set<File> sourceFiles = new LinkedHashSet<File>();
    public boolean bundleLibraries = true;
    public boolean wcharAsShort;
    public boolean charPtrAsString;
    public boolean genCPlusPlus;
    public File extractedSymbolsOut;
    public boolean stringifyConstCStringReturnValues = true;
    public File bridgesupportOutFile;
    public boolean scalaStructSetters;
    public boolean noPrimitiveArrays;
    public boolean synchronizedMethods;
    public File scalaOut;
    public boolean skipPrivateMembers = true;
    public boolean castConstants = true;
    public File rawParsedSourcesOutFile, normalizedParsedSourcesOutFile;
    public List<Pair<OptionDef, List<String>>> parsedArgs;
    public boolean skipLibraryInstanceDeclarations;
    public String callbackInvokeMethodName = "apply";

    public Collection<File> getFiles() {
        /*return new AdaptedCollection<String, File>(libraryByFile.keySet(), new Adapter<String, File>() {
         @Override
         public File adapt(String value) {
         return new File(value);
         }
         });*/
        return sourceFiles;//libraryByFile.keySet();
    }

    public String relativizeFileForSourceComments(String path) {
        if (path == null) {
            return null;
        }

        for (String pref : rootDirectoriesPrefixesForSourceComments) {
            if (path.startsWith(pref)) {
                path = path.substring(pref.length());
                break;
            }
        }
        return path;
    }

    @SuppressWarnings("unchecked")
    public Collection<File> getInputFiles() {
        return new CompoundCollection<File>(sourceFiles, bridgeSupportFiles, libraryFiles);
    }
}