package babeloff.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class JNAeratorPluginTest {
    @Test
    void pluginIsApplied() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'babeloff.jnaerator'

        def task = project.tasks.findByName('generate')
        assert task instanceof babeloff.JNAeratorTask
        //task.config = project.file("$project.output/generated-src")
        //assert task.config == project.file("src/test/resource/test_config.jnae")
        //assert task.output == project.file("$project.output/generated-src")
    }
}