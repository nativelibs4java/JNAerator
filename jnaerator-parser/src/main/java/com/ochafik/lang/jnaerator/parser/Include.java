/*
 Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
 This file is part of JNAerator (https://jnaerator.googlecode.com/).
	
 JNAerator is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
	
 JNAerator is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
	
 You should have received a copy of the GNU Lesser General Public License
 along with JNAerator.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ochafik.lang.jnaerator.parser;

public class Include extends Declaration {

    public enum Type {
        CInclude,
        ObjCImport,
        JavaImport,
        JavaStaticImport
    }
    
    Type type;
    String path;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Include(Type type, String path) {
        this.type = type;
        this.path = path;
    }

    public Include() {
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitInclude(this);
    }
}
