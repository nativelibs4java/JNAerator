package babeloff.jnaerator;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class JNAeratorPlugin implements Plugin<Project> {

    private static final String GENERATE_TASK = "generate";

    public void apply(Project project) {
        final Task task = project.getTasks().create(GENERATE_TASK, JNAeratorTask.class);
        //        (task) -> {
        //            task.setConfig("test_config.jnae");
        //            task.setOutputDirectory("World");
        //        });
        //project.configurations { jnaerator }
        //project.dependencies {
        //    jnaerator "com.nativelibs4java:jnaerator:0.13-SNAPSHOT"
        //}
        //project.task("jnaerator", type:JNAerateTask)
    }
}
