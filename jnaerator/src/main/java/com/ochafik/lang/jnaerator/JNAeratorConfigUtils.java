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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.ochafik.admin.visualstudio.Configuration;
import com.ochafik.admin.visualstudio.Project;
import com.ochafik.admin.visualstudio.Solution;
import com.ochafik.admin.visualstudio.VisualStudioUtils;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Modifier;
import com.ochafik.lang.jnaerator.parser.ModifierType;
import com.ochafik.util.SystemUtils;
import com.ochafik.util.listenable.Adapter;
import com.ochafik.util.string.RegexUtils;
import com.ochafik.util.string.StringUtils;

public class JNAeratorConfigUtils {

    public static Logger logger = Logger.getLogger(JNAeratorConfigUtils.class.getName());

    public static final class MSC_VER {

        public final int VC4 = 1000,
                VC5 = 1100,
                VC6 = 1200,
                VC70 = 1300,
                VC71 = 1310;
    }

    static String getProp(String name, String defVal, boolean verbose) {
        String v = System.getenv(name);
        v = v == null ? System.getProperty(name, defVal) : v;
        if (verbose) {
            logger.log(Level.INFO, "[environment] " + name + "=" + v);
        }
        return v;
    }
    //this.frameworkspath = new ArrayList<String>(Arrays.asList(new String[] {"/System/Library/Frameworks/", "/Library/Frameworks/", "/Local/Library/Frameworks/"})); 
    static List<String> DEFAULT_FRAMEWORKS_PATH = Arrays.asList(
            "/System/Library/Frameworks/CoreServices.framework/Versions/Current/Frameworks",
            "/System/Library/Frameworks/ApplicationServices.framework/Versions/Current/Frameworks",
            "/System/Library/Frameworks",
            "/Library/Frameworks",
            "/Local/Library/Frameworks/",
            System.getProperty("user.home") + "/Library/Frameworks");
    static List<String> DEFAULT_INCLUDE_PATH;

    static {
        if (SystemUtils.isMacOSX()) {
            DEFAULT_INCLUDE_PATH = new ArrayList<String>();
            for (String s : new String[]{
                        "/Developer/SDKs/MacOSX10.5.sdk/usr/include",
                        "/Developer/SDKs/MacOSX10.4u.sdk/usr/include"
                    }) {
                if (new File(s).exists()) {
                    DEFAULT_INCLUDE_PATH.add(s);
                    break;
                }
            }

            DEFAULT_INCLUDE_PATH.add(".");
        } else if (SystemUtils.isWindows()) {
            ArrayList<String> list = new ArrayList<String>(VisualStudioUtils.getMicrosoftIncludes());
            list.add(".");
            DEFAULT_INCLUDE_PATH = list;
        } else {
            DEFAULT_INCLUDE_PATH = new ArrayList<String>(Arrays.asList("."));
        }
        if (SystemUtils.isUnix()) {
            DEFAULT_INCLUDE_PATH.add("/usr/include");
            DEFAULT_INCLUDE_PATH.add("/usr/local/include");

            /*
             * /usr/include/c++ is likely to contain directories with versions such as 4.0.0, 4.4.0...
             * We try to take the greatest version (in lexicographic order of matching dir names) and check that it contains "new" and "map" files
             */
            File cppi = new File("/usr/include/c++");
            TreeSet<String> versions = new TreeSet<String>();
            if (cppi.isDirectory()) {
                for (File f : cppi.listFiles()) {
                    if (!f.isDirectory()) {
                        continue;
                    }
                    String n = f.getName();
                    if (!n.matches("[\\d+](\\.[\\d+])*")) {
                        continue;
                    }
                    if (!(new File(f, "new").exists() && new File(f, "map").exists())) {
                        continue;
                    }
                    versions.add(n);
                }
            }
            if (!versions.isEmpty()) {
                File d = new File(cppi, versions.last());
                DEFAULT_INCLUDE_PATH.add(d.toString());
                DEFAULT_INCLUDE_PATH.add(new File(d, "tr1").toString());
            }
        }
        DEFAULT_INCLUDE_PATH = Collections.unmodifiableList(DEFAULT_INCLUDE_PATH);
    }

    public static class FileExtensionFilter implements FileFilter {

        final Collection<String> allowedExtensions;

        public FileExtensionFilter(Collection<String> allowedExtensions) {
            this.allowedExtensions = allowedExtensions;
        }

        public FileExtensionFilter(String[] split) {
            this(Arrays.asList(split));
        }

        public boolean accept(File file) {
            String name = file.getName().toLowerCase();
            int i = name.lastIndexOf('.');
            return accept(file, i > 0 ? name.substring(i + 1) : "");
        }

        public boolean accept(File file, String extension) {
            return allowedExtensions.contains(extension);
        }
    }

    public static void addCPlusPlus(JNAeratorConfig.PreprocessorConfig config) {
        config.implicitMacros.put("__cplusplus", null);
    }

    public static void addGCCPredefinedMacros(JNAeratorConfig.PreprocessorConfig config) {
        //gcc -dM -E - < /dev/null
        config.implicitMacros.put("__DBL_MIN_EXP__", "(-1021)");
        config.implicitMacros.put("__FLT_MIN__", "1.17549435e-38F");
        config.implicitMacros.put("__CHAR_BIT__", "8");
        config.implicitMacros.put("__WCHAR_MAX__", "2147483647");
        config.implicitMacros.put("__DBL_DENORM_MIN__", "4.9406564584124654e-324");
        config.implicitMacros.put("__FLT_EVAL_METHOD__", "0");
        config.implicitMacros.put("__DBL_MIN_10_EXP__", "(-307)");
        config.implicitMacros.put("__FINITE_MATH_ONLY__", "0");
        config.implicitMacros.put("__SHRT_MAX__", "32767");
        config.implicitMacros.put("__LDBL_MAX__", "1.18973149535723176502e+4932L");
        if (com.sun.jna.Platform.isMac()) {
            config.implicitMacros.put("__APPLE_CC__", "5484");
            config.implicitMacros.put("__MACH__", "1");
            config.implicitMacros.put("__APPLE__", "1");
        }
        config.implicitMacros.put("__UINTMAX_TYPE__", "long long unsigned int");
        config.implicitMacros.put("__SCHAR_MAX__", "127");
        config.implicitMacros.put("__USER_LABEL_PREFIX__", "_");
        config.implicitMacros.put("__STDC_HOSTED__", "1");
        config.implicitMacros.put("__DBL_DIG__", "15");
        config.implicitMacros.put("__FLT_EPSILON__", "1.19209290e-7F");
        config.implicitMacros.put("__LDBL_MIN__", "3.36210314311209350626e-4932L");
        config.implicitMacros.put("__strong", "");
        config.implicitMacros.put("__DECIMAL_DIG__", "21");
        config.implicitMacros.put("__LDBL_HAS_QUIET_NAN__", "1");
        config.implicitMacros.put("__DYNAMIC__", "1");
//		config.implicitMacros.put("__GNUC__", "4");
//		config.implicitMacros.put("__MMX__", "1");
        config.implicitMacros.put("__DBL_MAX__", "1.7976931348623157e+308");
        config.implicitMacros.put("__DBL_HAS_INFINITY__", "1");
        config.implicitMacros.put("OBJC_NEW_PROPERTIES", "1");
        config.implicitMacros.put("__weak", "");
        config.implicitMacros.put("__DBL_MAX_EXP__", "1024");
//		config.implicitMacros.put("__SSE2_MATH__", "1");
        config.implicitMacros.put("__LONG_LONG_MAX__", "9223372036854775807LL");
        config.implicitMacros.put("__GXX_ABI_VERSION", "1002");
        config.implicitMacros.put("__FLT_MIN_EXP__", "(-125)");
        config.implicitMacros.put("__DBL_MIN__", "2.2250738585072014e-308");
        config.implicitMacros.put("__DBL_HAS_QUIET_NAN__", "1");
        config.implicitMacros.put("__REGISTER_PREFIX__", "");
        config.implicitMacros.put("__NO_INLINE__", "1");
//		config.implicitMacros.put("__i386", "1");
        config.implicitMacros.put("__FLT_MANT_DIG__", "24");
        config.implicitMacros.put("__VERSION__", "\"4.0.1 (Apple Inc. build 5484)\"");
//		config.implicitMacros.put("i386", "1");
//		config.implicitMacros.put("__i386__", "1");
        config.implicitMacros.put("__ENVIRONMENT_MAC_OS_X_VERSION_MIN_REQUIRED__", "1055");
        config.implicitMacros.put("__SIZE_TYPE__", "long unsigned int");
        config.implicitMacros.put("__FLT_RADIX__", "2");
        config.implicitMacros.put("__LDBL_EPSILON__", "1.08420217248550443401e-19L");
//		config.implicitMacros.put("__SSE_MATH__", "1");
        config.implicitMacros.put("__FLT_HAS_QUIET_NAN__", "1");
        config.implicitMacros.put("__FLT_MAX_10_EXP__", "38");
        config.implicitMacros.put("__LONG_MAX__", "2147483647L");
        config.implicitMacros.put("__FLT_HAS_INFINITY__", "1");

        config.implicitMacros.put("__LDBL_MANT_DIG__", "64");
        config.implicitMacros.put("__CONSTANT_CFSTRINGS__", "1");
        config.implicitMacros.put("__WCHAR_TYPE__", "int");
        config.implicitMacros.put("__FLT_DIG__", "6");
        config.implicitMacros.put("__INT_MAX__", "2147483647");
        config.implicitMacros.put("__FLT_MAX_EXP__", "128");
        config.implicitMacros.put("__DBL_MANT_DIG__", "53");
        config.implicitMacros.put("__WINT_TYPE__", "int");
//		config.implicitMacros.put("__SSE__", "1");
        config.implicitMacros.put("__LDBL_MIN_EXP__", "(-16381)");
        config.implicitMacros.put("__LDBL_MAX_EXP__", "16384");
        config.implicitMacros.put("__LDBL_MAX_10_EXP__", "4932");
        config.implicitMacros.put("__DBL_EPSILON__", "2.2204460492503131e-16");
        config.implicitMacros.put("__LDBL_HAS_INFINITY__", "1");
        config.implicitMacros.put("__INTMAX_MAX__", "9223372036854775807LL");
        config.implicitMacros.put("__FLT_DENORM_MIN__", "1.40129846e-45F");
        config.implicitMacros.put("__PIC__", "1");
        config.implicitMacros.put("__FLT_MAX__", "3.40282347e+38F");
//		config.implicitMacros.put("__SSE2__", "1");
        config.implicitMacros.put("__FLT_MIN_10_EXP__", "(-37)");
        config.implicitMacros.put("__INTMAX_TYPE__", "long long int");
        config.implicitMacros.put("__GNUC_MINOR__", "0");
        config.implicitMacros.put("__DBL_MAX_10_EXP__", "308");
        config.implicitMacros.put("__LDBL_DENORM_MIN__", "3.64519953188247460253e-4951L");
//		config.implicitMacros.put("__PTRDIFF_TYPE__", "int");
        config.implicitMacros.put("__LDBL_MIN_10_EXP__", "(-4931)");
        config.implicitMacros.put("__LDBL_DIG__", "18");
//		config.implicitMacros.put("__GNUC_GNU_INLINE__", "1");
    }

    static void defaultMacro(JNAeratorConfig config, String name, String value) {
        if (!config.preprocessorConfig.implicitMacros.containsKey(name)) {
            config.preprocessorConfig.implicitMacros.put(name, value);
        }
    }

    /**
     * TODO move this to a .h resource file
     */
    public static void autoConfigure(final JNAeratorConfig config) {
        if (config.runtime == JNAeratorConfig.Runtime.BridJ) {
            config.genCPlusPlus = true;
        }

        if (!config.noCPlusPlus) {
            addCPlusPlus(config.preprocessorConfig);
        }

        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            defaultMacro(config, "__BIG_ENDIAN__", "1");
            defaultMacro(config, "G_BYTE_ORDER", "4321"); //glibc: #define G_BIG_ENDIAN    4321
        } else {
            defaultMacro(config, "__LITTLE_ENDIAN__", "1");
            defaultMacro(config, "G_BYTE_ORDER", "1234"); //glibc: #define G_LITTLE_ENDIAN 1234
        }
        //prevent a jcpp bug to happen when expanding assert(...) :
        config.preprocessorConfig.implicitMacros.put("NDEBUG", null);

        config.preprocessorConfig.implicitMacros.put("__STDC__", null);
        config.preprocessorConfig.implicitMacros.put("HAVE_PROTOTYPES", null);
        config.preprocessorConfig.implicitMacros.put("STDC_HEADERS", null);

        config.preprocessorConfig.implicitIncludes.addAll(getDefaultIncludePath(config.verbose));//JNAeratorConfigUtils.DEFAULT_INCLUDE_PATH);
        config.preprocessorConfig.frameworksPath.addAll(getDefaultFrameworkPath(config.verbose));//JNAeratorConfigUtils.DEFAULT_FRAMEWORKS_PATH);
        if (SystemUtils.isWindows()) {
            //http://msdn.microsoft.com/en-us/library/b0084kay(VS.80).aspx

            //config.preprocessorConfig.includeStrings.add("#define __declspec(x)\n");

            //http://support.microsoft.com/kb/65472
            config.preprocessorConfig.implicitMacros.put("_CHAR_UNSIGNED", null);

            config.preprocessorConfig.implicitMacros.put("WIN32_LEAN_AND_MEAN", null);

            // http://msdn.microsoft.com/en-us/library/dh8che7s(VS.80).aspx
            config.preprocessorConfig.implicitMacros.put("_WCHAR_T_DEFINED", null);
            config.preprocessorConfig.implicitMacros.put("_NATIVE_WCHAR_T_DEFINED", null);

            defaultMacro(config, "_MSC_VER", "1100");
            defaultMacro(config, "WINAPI", "__stdcall");

            config.functionsAccepter = new Adapter<Function, Boolean>() {
                public Boolean adapt(Function value) {
                    return true;
                    /*
                     Set<Modifier> mods = value.getModifiers();
	
                     return mods.contains(ModifierType.DllExport) ||
                     mods.contains(ModifierType.DllImport) ||
                     mods.contains(ModifierType.Extern);*/
                }
            };

            //config.preprocessorConfig.implicitMacros.put("")
            //_CPPRTTI
            //_DLL
            //_M_IX86
            //_MT
            //_CPPUNWIND

        } else {
            config.preprocessorConfig.implicitMacros.put("__GNUC__", null);

            if (SystemUtils.isMacOSX()) {
                config.preprocessorConfig.implicitMacros.put("TARGET_API_MAC_OSX", null);
                config.preprocessorConfig.implicitMacros.put("__APPLE_CPP__", null);
                config.preprocessorConfig.implicitMacros.put("__APPLE_CC__", null);
//				config.preprocessorConfig.implicitMacros.put("FUNCTION_PASCAL", "0");
//				config.preprocessorConfig.implicitMacros.put("FUNCTION_DECLSPEC", "1");
//				config.preprocessorConfig.implicitMacros.put("FUNCTION_WIN32CC", "1");

            }

            config.functionsAccepter = new Adapter<Function, Boolean>() {
                public Boolean adapt(Function value) {
                    return config.convertBodies || !value.hasModifier(ModifierType.Inline);
                }
            };
        }
        config.preprocessorConfig.includeStrings.add(0, "#define __attribute__(x)\n");

        JNAeratorConfigUtils.autoConfigureArchitecture(config);
    }

    private static Collection<? extends String> getDefaultFrameworkPath(boolean verbose) {
        return Arrays.asList(getProp("JNAERATOR_FRAMEWORKS_PATH", StringUtils.implode(DEFAULT_FRAMEWORKS_PATH, File.pathSeparator), verbose).split(File.pathSeparator));
    }

    private static Collection<? extends String> getDefaultIncludePath(boolean verbose) {
        return Arrays.asList(getProp("JNAERATOR_INCLUDE_PATH", StringUtils.implode(DEFAULT_INCLUDE_PATH, File.pathSeparator), verbose).split(File.pathSeparator));

    }

    /**
     * TODO move this to a .h resource file <ul> <li> endianness <li>
     * TARGET_CPU_* : see
     * /System/Library/Frameworks/CoreServices.framework/Versions/Current/Frameworks/CarbonCore.framework/Headers/fp.h
     * </ul>
     *
     * @param config
     */
    static void autoConfigureArchitecture(JNAeratorConfig config) {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (config.verbose) {
            System.out.println("os.arch = " + arch);
        }

        //protect us from inline assembly in VC++:
        //config.preprocessorConfig.implicitMacros.put("_M_CEE_PURE", null);

        if (arch.equals("x86_64") || arch.equals("amd64")) {
            config.preprocessorConfig.implicitMacros.put("TARGET_CPU_X86_64", null);
            config.preprocessorConfig.implicitMacros.put("__i386__", null);
            config.preprocessorConfig.implicitMacros.put("__x86_64__", null);
            config.preprocessorConfig.implicitMacros.put("__amd64__", null);
            config.preprocessorConfig.implicitMacros.put("__LITTLE_ENDIAN__", null);
            config.preprocessorConfig.implicitMacros.put("M_I86", "1");
            config.preprocessorConfig.implicitMacros.put("_M_I86", "1");
            config.preprocessorConfig.implicitMacros.put("_WIN32", "1");
//			config.preprocessorConfig.implicitMacros.put("M_X64", "1");
//			config.preprocessorConfig.implicitMacros.put("_M_X64", "1");
//			config.preprocessorConfig.implicitMacros.put("_WIN64", "1");
        } else if (arch.equals("i386") || arch.equals("x86")) {
            config.preprocessorConfig.implicitMacros.put("TARGET_CPU_X86", null);
            config.preprocessorConfig.implicitMacros.put("__i386__", null);
            config.preprocessorConfig.implicitMacros.put("__LITTLE_ENDIAN__", null);
            config.preprocessorConfig.implicitMacros.put("M_I86", "1");
            config.preprocessorConfig.implicitMacros.put("_M_I86", "1");
            config.preprocessorConfig.implicitMacros.put("_WIN32", "1");
        } else if (arch.equals("ppc")) {
            config.preprocessorConfig.implicitMacros.put("TARGET_CPU_PPC", null);
            config.preprocessorConfig.implicitMacros.put("__PPC__", null);
            config.preprocessorConfig.implicitMacros.put("__powerpc__", null);
            config.preprocessorConfig.implicitMacros.put("__BIG_ENDIAN__", null);
        } else if (arch.equals("ppc64")) {
            config.preprocessorConfig.implicitMacros.put("TARGET_CPU_PPC64", null);
            config.preprocessorConfig.implicitMacros.put("__PPC_64__", null);
//			config.preprocessorConfig.implicitMacros.put("__powerpc64__", null);
            config.preprocessorConfig.implicitMacros.put("__BIG_ENDIAN__", null);
        }

    }

    public static void readProjectConfig(File projectFile, String configName, final JNAeratorConfig config) throws Exception {
        String projectFileName = projectFile.getName();
        config.rootDirectoriesPrefixesForSourceComments.add(projectFile.getCanonicalFile().getParent() + File.separator);

        if (projectFileName.endsWith(".sln")) {
            if (configName == null) {
                configName = "Release|Win32";
            }

            for (String include : VisualStudioUtils.getMicrosoftIncludes()) {
                include = new File(include).getCanonicalPath();
                config.preprocessorConfig.implicitIncludes.add(include);
                if (!include.endsWith(File.separator)) {
                    include = include + File.separator;
                }
                config.rootDirectoriesPrefixesForSourceComments.add(include);
            }

            Solution solution = new Solution(projectFile);
            solution.parseProjects(config.fileFilter);

            //final Map<String, FileConfiguration> configsByFile = new HashMap<String, FileConfiguration>();
            //final Map<File, String> libraryDLLByFile = new HashMap<File, String>();

            for (Project project : solution.getProjects()) {
                String projectConfigName = project.activeConfigurationNameBySolutionConfigurationName.get(configName);
                if (projectConfigName == null) {
                    projectConfigName = configName;
                }

                //	throw new IOException("Solution configuration with name '" + configName + "' does correspond to any configuration in project '" + project.name + "' (available configs : " + 
                //			StringUtils.implode(project.activeConfigurationNameBySolutionConfigurationName.keySet(), ", ") + ")");

                Configuration configuration = project.configurations.get(projectConfigName);
                String libraryFile = configuration.outputFile == null ? project.name : RegexUtils.findFirst(configuration.outputFile, Pattern.compile("^(.*?)(\\.[^.]*)?$"), 1);
                config.libraryProjectSources.put(libraryFile, project.projectFile.getCanonicalFile());

                System.out.println("project " + project.name + ": library = " + libraryFile);
                if (configuration != null) {
                    System.out.println("preprocessorDefinitions : " + configuration.preprocessorDefinitions);
                    for (String def : configuration.preprocessorDefinitions) {
                        config.preprocessorConfig.implicitMacros.put(def, "");
                    }
                }
                for (File file : project.files) {
                    try {
                        file = file.getCanonicalFile();
                        System.out.println(file + "\n\t-> " + libraryFile);
                        config.addSourceFile(file, libraryFile, false, true, true);

                        //config.preprocessorConfig
                        //config.libraryByFile.put(file, libraryFile)
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            if (config.preprocessorConfig.implicitMacros.containsKey("WIN32") || config.preprocessorConfig.implicitMacros.containsKey("_WIN32")) {
                config.preprocessorConfig.implicitMacros.put("_M_IX86", "");
            } else if (config.preprocessorConfig.implicitMacros.containsKey("WIN64") || config.preprocessorConfig.implicitMacros.containsKey("_WIN64")) {
                config.preprocessorConfig.implicitMacros.put("_M_AMD64", "");
            }

        }
    }
    
    public static File getFrameworkHeaderDirectory(String framework, List<String> frameworksPath) throws IOException {
        return new File(getFrameworkDirectory(framework, frameworksPath), "Headers");
    }
    public static File getFrameworkDirectory(String framework, List<String> frameworksPath) throws IOException {
        File file = new File(framework);
        if (!file.getName().matches("\\.framework$") || !file.exists()) {
            for (String pathEl : frameworksPath) {
                File f = new File(pathEl, framework + ".framework");
                if (f.exists() && f.isDirectory()) {
                    file = f;
                    break;
                }
            }
        }
        if (!file.exists()) {
            throw new IOException("Could not find framework '" + framework + "' in path " + frameworksPath);
        }
        return file;
    }
}
