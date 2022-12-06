package com.jnaerator;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.ochafik.lang.jnaerator.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Launch JNAerator to wrap native libraries in Java for use with JNA.
 * @goal generate
 * @phase generate-sources
 * @description Launches JNAerator with the command-line arguments contained in src/main/jnaerator/config.jnaerator. To launch from command line, use "mvn jnaerator:generate"
 */
public class JNAeratorMojo
    extends AbstractMojo
{
    /**
     * Path to JNAerator configuration file.
     * @parameter property=${project.basedir}/src/main/jnaerator/config.jnaerator
     * @required
     */
    private File config;


    /**
     * Whether JNAerator should output helper Scala classes (experimental)
     * @parameter property=false
     * @optional
     */
    private boolean generateScala;

    /**
     * Output directory for JNAerated Java sources.
     * @parameter property=${project.build.directory}/generated-sources/java
     * @optional
     */
    private File javaOutputDirectory;

    /**
     * Output directory for JNAerated Scala sources.
     * @parameter property=${project.build.directory}/generated-sources/scala
     * @optional
     */
    private File scalaOutputDirectory;

    /**
     * @parameter property=${project}
     * @required
     * @readonly
     * @since 1.0
     */
    private MavenProject project;

	static File canonizeDir(File f) throws IOException {
        if (!f.exists())
            f.mkdirs();
        return f.getCanonicalFile();
    }
    public void execute()
        throws MojoExecutionException
    {
    	if (!config.exists()) {
    		getLog().info("No JNAerator config file '" + config + "' found");
    		return;
    	}
        try
        {
            List<String> args = new ArrayList<String>();

            args.add(config.getAbsolutePath());
            
            // Override settings from config file :
            args.add("-mode");
            args.add(JNAeratorConfig.OutputMode.Directory.name());
            args.add("-f");
            args.add("-o");
            
            File javaDir = canonizeDir(javaOutputDirectory);
            args.add(javaDir.toString());
            
            project.addCompileSourceRoot(javaDir.toString());

            if (generateScala) {
                args.add("-scalaOut");
                args.add(canonizeDir(scalaOutputDirectory).toString());
            }

            com.ochafik.lang.jnaerator.JNAerator.main(args.toArray(new String[0]));
        }
        catch (Exception e )
        {
            throw new MojoExecutionException( "Error running JNAerator on " + config, e );
        }
    }
}
