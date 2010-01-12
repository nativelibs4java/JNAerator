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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ochafik.io.ReadText;


public class Solution {
	private static final boolean verbose = true;
	
	public File solutionFile;
	public Map<String, Project> idToProject;
	public Set<File> allFiles = new HashSet<File>(10000);

	static Pattern solutionIdPattern = Pattern.compile("Project\\(\"\\{[^}]+\\}\"\\) = \"([^\"]+)\", \"([^\"]+)\", [^{]+\\{([^}]+)\\}\"" +
		".*?ProjectSection\\(ProjectDependencies\\)(.*?)EndProjectSection", Pattern.MULTILINE
	);

	static Pattern projectDependencyPattern = Pattern.compile("\\{[^}]+\\} = \\{([^}]+)\\}");

	static Pattern projectConfigsForSolutionConfigPattern = Pattern.compile("^\\s*\\{([^}]+)\\}\\.([^\\.]+)\\.ActiveCfg\\s*=\\s*(.*)\\s*$");
	public Solution(File solutionFile) {
		this.solutionFile = solutionFile;
	}
	public void parse() throws Exception {
		if (idToProject == null) {
			idToProject = new HashMap<String, Project>();
			
			String solutionContent = ReadText.readText(solutionFile);
			File solutionPath = solutionFile.getParentFile();
			
			for (Matcher solutionIdMatcher = solutionIdPattern.matcher(solutionContent.replace('\n', ' ')); solutionIdMatcher.find();) {
				Project p = new Project(this, relFile(solutionPath, solutionIdMatcher.group(2)), solutionIdMatcher.group(1), solutionIdMatcher.group(3));
				for (Matcher depM = projectDependencyPattern.matcher(solutionIdMatcher.group(4)); depM.find();)
					p.depsIds.add(depM.group(1));
				idToProject.put(p.id, p);
			}
			
			for (Matcher m = projectConfigsForSolutionConfigPattern.matcher(solutionContent); m.find();) {
				String id = m.group(1), slnConfigName = m.group(2), projConfigName = m.group(3);
				Project p = idToProject.get(id);
				if (p == null)
					continue;
				
				p.activeConfigurationNameBySolutionConfigurationName.put(slnConfigName, projConfigName);
			}
		}
	}
	
	public String getName() {
		return solutionFile == null ? null : solutionFile.getName().replaceAll("\\.[^.]+$", "");
	}
	
	public void parseProjects(final FileFilter fileFilter) throws Exception {
		parse();
		int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		if (verbose)
			System.out.println("Parsing Solution: " + solutionFile.getName() + " (" + nThreads + " threads)");
		
		final Semaphore semaphore = new Semaphore(0);
		final LinkedList<Project> projects = new LinkedList<Project>(idToProject.values());
		class Worker extends Thread { public void run() {
			for (;;) {
				try {
					Project project = null;
					synchronized (projects) {
						if (projects.isEmpty())
							break;
						
						project = projects.removeLast();
					}
					if (verbose) {
						synchronized (System.out) {
							System.out.println("Parsing Project: " + project.name);
						}
					}
				
					project.parse(fileFilter, false, Solution.this);
					synchronized (allFiles) {
						allFiles.addAll(project.files);
					}
				} catch (Exception ex) {
					synchronized (System.err) {
						ex.printStackTrace(System.err);
					}
					
				}
			}
			semaphore.release();
		}};
		for (int i = nThreads; i-- != 0;) 
			new Worker().start();
		
		semaphore.acquire(nThreads);
	}
	
	static File relFile(File base, String relPath) {
		relPath = relPath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
		return new File(base, relPath);
	}
	public Collection<Project> getProjects() {
		return idToProject.values();
	}
}