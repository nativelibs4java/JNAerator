/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author ochafik
 */
public class JNAeratorConfigTest {
    @Test
    public void testPoms() throws Throwable {
        try {
            for (JNAeratorConfig.Runtime runtime : JNAeratorConfig.Runtime.values()) {
                String pom = runtime.generateMavenProjectModel("com.mycompany", "myartifact", "1.0-SNAPSHOT");
                assertNotNull(pom);
            }
        } catch (Throwable th) {
            th.printStackTrace();
            throw th;
        }
    }
}
