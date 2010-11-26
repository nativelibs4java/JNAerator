/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
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
package com.ochafik.lang.jnaerator.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ochafik
 */
public class Declarations extends Declaration implements DeclarationsHolder {
    final List<Declaration> declarations = new ArrayList<Declaration>();
    
    public Declarations() {}
    public Declarations(List<Declaration> declarations) { 
        setDeclarations(declarations);
    }
    
	@Override
	public void accept(Visitor visitor) {
		visitor.visitDeclarations(this);
	}
    
    public void addDeclaration(Declaration declaration) {
        if (declaration != null) {
            declaration.setParentElement(this);
            declarations.add(declaration);
        }
    }
    
	public void addDeclarations(List<Declaration> ds) {
		if (ds == null)
			return;
		for (Declaration d : ds)
			d.setParentElement(this);
		declarations.addAll(ds);
	}
    public void setDeclarations(List<Declaration> declarations) {
        changeValue(this, this.declarations, declarations);
    }
    public void setDeclarations(Declaration... declarations) {
        setDeclarations(Arrays.asList(declarations));
    }
    public List<Declaration> getDeclarations() {
        return unmodifiableList(declarations);
    }
    
	@Override
	public Element getNextChild(Element child) {
		Element e = getNextSibling(declarations, child);
		if (e != null)
			return e;
		return super.getNextChild(child);
	}
	@Override
	public Element getPreviousChild(Element child) {
		Element e = getPreviousSibling(declarations, child);
		if (e != null)
			return e;
		return super.getPreviousChild(child);
	}

    
	@Override
	public boolean replaceChild(Element child, Element by) {
		if (replaceChild(declarations, Declaration.class, this, child, by))
			return true;
		return super.replaceChild(child, by);
	}
}
