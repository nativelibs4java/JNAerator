/*
	Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (https://github.com/nativelibs4java/JNAerator).
	
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
