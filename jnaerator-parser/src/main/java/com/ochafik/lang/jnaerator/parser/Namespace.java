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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ochafik
 */
public class Namespace extends Declarations {
    Identifier name;
    
    public Namespace() {}
    public Namespace(Identifier name, Declaration... declarations) {
        setName(name);
        setDeclarations(Arrays.asList(declarations));
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visitNamespace(this);
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = changeValue(this, this.name, name);
    }

    @Override
    public boolean replaceChild(Element child, Element by) {
        if (child == getName()) {
			setName((Identifier) by);
			return true;
		}
		return super.replaceChild(child, by);
    }
}
