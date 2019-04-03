package babeloff.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.plugins.PluginManager;
import org.junit.Test;
import org.junit.Assert;

public class JNAeratorPluginTest {
    @Test
    public void pluginTaskExists() {
        final String pluginId = "babeloff.jnaerator";
        final Project project = ProjectBuilder.builder().build();
        final PluginManager pm = project.getPluginManager();

        pm.apply(pluginId);
        Assert.assertTrue(pm.hasPlugin(pluginId));
        Assert.assertNotNull(project.getTasks().getByName("generate"));

        // assert task instanceof babeloff.JNAeratorTask
        //task.config = project.file("$project.output/generated-src")
        //assert task.config == project.file("src/test/resource/test_config.jnae")
        //assert task.output == project.file("$project.output/generated-src")
    }
}