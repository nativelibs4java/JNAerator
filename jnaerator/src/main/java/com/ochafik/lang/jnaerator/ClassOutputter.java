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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class ClassOutputter {

    public PrintWriter getClassSourceWriter(String className) throws IOException {
        String path = className.replace('.', '/') + ".java";
        return getSourceWriter(path);
    }

    public abstract PrintWriter getSourceWriter(String path) throws IOException;
}
