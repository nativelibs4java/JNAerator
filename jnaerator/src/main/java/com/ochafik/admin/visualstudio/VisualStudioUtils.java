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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.ochafik.util.listenable.Adapter;
import com.ochafik.util.string.RegexUtils;

public class VisualStudioUtils {
	private static Logger logger = Logger.getLogger(VisualStudioUtils.class.getName());

	public static List<String> getMicrosoftIncludes() {
		return Arrays.asList(getProp("VISUAL_STUDIO_INCLUDES", 
			getMicrosoftVisualStudioHome() + "\\VC\\include" + ";" + getMicrosoftWindowsSDKHome() + "\\Include",
			true
		).split(";"));
	}
	public static String getMicrosoftWindowsSDKHome() {
		return getProp("WINDOWS_SDK_HOME", "C:\\Program Files\\Microsoft SDKs\\Windows\\v6.0A", true);
	}
	private static String getMicrosoftVisualStudioHome() {
		return getProp("VISUAL_STUDIO_HOME", "C:\\Program Files\\Microsoft Visual Studio 9.0", true);
	}
	static String getProp(String name, String defVal) {
		return getProp(name, defVal, false);
	}
	
	static String getProp(String name, String defVal, boolean remember) {
		String v = System.getenv(name);
		v = v == null ? System.getProperty(name, defVal) : v;
		logger.log(Level.INFO, "[environment] " + name + "=" + v);
		return v;
	}

	static final Pattern variablePattern = Pattern.compile("$\\(([^)]+)\\)");
	enum VarType {
		SolutionName, 
		SolutionDir,
		ProjectName,
		ConfigurationName,
		TargetDir,
		TargetPath
	}
	public static String resolveVariables(String string, final Solution solution, final Project project, final Configuration configuration) {
		String res = RegexUtils.regexReplace(variablePattern, string, new Adapter<String[], String>() {
			public String adapt(String[] value) {
				String match = value[0];
				String variableName = value[1];
				VarType type = VarType.valueOf(variableName);
				String name;
				if (type != null) {
					switch (type) {
					case ProjectName:
						return project.name == null ? match : project.name;
					case TargetPath:
						return configuration.outputFile == null ? match : configuration.outputFile;
					case SolutionName:
						name = solution.getName();
						return name == null ? match : name;
					case SolutionDir:
						return solution.solutionFile == null ? match : solution.solutionFile.getParent();
					case ConfigurationName:
						return configuration.name;
					case TargetDir:
						if (configuration.outputFile == null)
							return match;
						
						return new File(configuration.outputFile).getParent();
					default:
						return match;
					}
				}
				String v = System.getenv().get(variableName);
				return v == null ? match : v;
			}
		});
		//System.err.println("resolved \"" + string + "\" as :\n\t\"" + res + "\"");
		return res;
	}
}
