package babeloff;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;

import org.gradle.api.tasks.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ochafik.lang.jnaerator.JNAerator;
import com.ochafik.lang.jnaerator.JNAeratorConfig;

public class JNAeratorTask extends DefaultTask {

    /**
     * Path to JNAerator configuration file.
     */
    private File config;
    @InputFile
    public File getConfig() { return this.config; }
    public void setConfig(File config) { this.config = config; }

    /**
     * Output directory for JNAerated Java sources.
     */
    private File output;
    @OutputDirectory
    public File getOutput() { return this.output; }
    public void setOutput(File dir) { this.output = dir; }

    /**
     *
     * @return cononical directory path
     */
    static File canonizeDir(final File f) throws IOException {
        if (!f.exists()) {  f.mkdirs(); }
        return f.getCanonicalFile();
    }

    @TaskAction
    void generate() throws TaskExecutionException {
        if (!config.exists()) {
            this.getLogger().info("No JNAerator config file '" + config + "' found");
            return;
        }
        try {
            final List<String> args = new ArrayList<>();
            args.add(config.getAbsolutePath());
            final String modeName =
                    JNAeratorConfig.OutputMode.Directory.name();
            final String javaDirStr =
                    canonizeDir(this.output).toString();
            final String configStr =
                    canonizeDir(this.config).toString();
            // Override settings from config file :
            args.add("-mode"); args.add(modeName);
            args.add("-f");
            args.add("@"); args.add(configStr);
            args.add("-o"); args.add(javaDirStr);

            JNAerator.main(args.toArray(new String[0]));
        }
        catch (Exception ex) {
            final Exception newex = new Exception("Error running JNAerator on " + this.config, ex);
            throw new TaskExecutionException( this, newex );
        } finally {
            System.out.printf("%s, %s!\n",
                    this.config.toString(),
                    this.output.toString());

        }
    }
}