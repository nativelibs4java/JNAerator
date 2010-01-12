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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import static com.ochafik.admin.visualstudio.VisualStudioUtils.*;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ochafik.xml.XMLUtils;
import com.ochafik.xml.XPathUtils;

public class Project implements Comparable<Project> {
	public final Solution solution;
	public File projectFile;
	public String name, id;
	public List<String> depsIds = new ArrayList<String>();
	public Map<String, String> activeConfigurationNameBySolutionConfigurationName = new HashMap<String, String>();
	
	public Map<String, Configuration> configurations = new HashMap<String, Configuration>();
	public Map<File, FileConfiguration> fileConfigurations = new HashMap<File, FileConfiguration>();
	public List<File> files = new ArrayList<File>();
	
	public Project(Solution solution, File projectFile, String name, String id) {
		this.solution = solution;
		this.projectFile = projectFile;
		this.name = name;
		this.id = id;
	}
	public int compareTo(Project o) {
		return name.compareTo(o.name);
	}
	
	String resolve(String s, Configuration configuration) {
		String res = resolveVariables(s, solution, this, configuration);
		return res == null || res.length() == 0 ? null : res;
	}
	
	public String resolveStringByXPath(String xPathString, Object source, Configuration configuration) throws XPathExpressionException {
		return resolve(XPathUtils.findStringByXPath(xPathString, source), configuration);
	}
	@SuppressWarnings("unchecked")
	public List<String> resolveListByXPath(String xPathString, Object source, String separatorPattern, Configuration configuration) throws XPathExpressionException {
		String s = resolve(XPathUtils.findStringByXPath(xPathString, source), configuration);
		return s == null ? Collections.EMPTY_LIST : Arrays.asList(s.split(separatorPattern));
	}
	public TreeSet<String> resolveSetByXPath(String xPathString, Object source, String separatorPattern, Configuration configuration) throws XPathExpressionException {
		return new TreeSet<String>(resolveListByXPath(xPathString, source, separatorPattern, configuration));
	}
	
	Configuration parseConfiguration(Node configNode) throws XPathExpressionException {
		Configuration config = new Configuration();
		config.name = resolve(XPathUtils.findStringByXPath("@Name", configNode), config);
		if (config.name == null) 
			return null;
		
		config.outputDirectory = resolve(XPathUtils.findStringByXPath("@OutputDirectory", configNode), config);
		
		Node compilerTool = XPathUtils.findNodeByXPath("Tool[@Name='VCCLCompilerTool']", configNode);
		if (compilerTool != null) {
			config.includes = resolveListByXPath("@AdditionalIncludeDirectories", compilerTool, ";", config);
			config.preprocessorDefinitions = resolveSetByXPath("@PreprocessorDefinitions", compilerTool, ";", config);
		}
		config.outputFile = resolve(XPathUtils.findStringByXPath("Tool[@Name='VCLinkerTool']/@OutputFile", configNode), config);
		/*if (config.outputFile == null)
			config.outputFile = name + ".dll";*/
		
		return config;
	}
	
	public void parse(FileFilter fileFilter, boolean parseFileConfigurations, Solution solution) throws Exception {
		if (!projectFile.exists() || !projectFile.isFile())
			throw new IOException("Cannot find project file " + projectFile);
		
		Document document = XMLUtils.readXML(projectFile);
		File projectPath = projectFile.getParentFile();
		
		for (Node configurationNode : XPathUtils.findNodesIterableByXPath("VisualStudioProject/Configurations/Configuration", document)) {
			Configuration config = parseConfiguration(configurationNode);
			if (config != null)		
				configurations.put(config.name, config);
		}
		for (Node fileNode : XPathUtils.findNodesIterableByXPath("//Files//File", document)) {
			String relativePath = resolveStringByXPath("@RelativePath", fileNode, null);
			if (relativePath == null)
				continue;
			
			File file = relFile(projectPath, relativePath);
			if (fileFilter != null && !fileFilter.accept(file))
				continue;
			
			files.add(file);
			
			if (parseFileConfigurations) {
				FileConfiguration fileConfiguration = new FileConfiguration();
				fileConfiguration.file = file;
				fileConfigurations.put(file, fileConfiguration);
				for (Node fileConfigurationNode : XPathUtils.findNodesIterableByXPath("FileConfiguration", fileNode)) {
					Configuration config = parseConfiguration(fileConfigurationNode);
					if (config != null)
						fileConfiguration.configurations.put(config.name, config);
				}
			}
		}
	}
	
	static File relFile(File base, String relPath) {
		relPath = relPath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
		return new File(base, relPath);
	}
}