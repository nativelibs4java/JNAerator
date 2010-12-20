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
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import com.ochafik.lang.jnaerator.parser.Statement.ExpressionStatement;
import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.StructObject;
import org.bridj.ValuedEnum;
import org.bridj.cpp.CPPObject;

import static com.ochafik.lang.SyntaxUtils.as;
//import org.bridj.structs.StructIO;
//import org.bridj.structs.Array;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.ochafik.lang.jnaerator.JNAeratorConfig.GenFeatures;
import com.ochafik.lang.jnaerator.TypeConversion.NL4JConversion;
import com.ochafik.lang.jnaerator.cplusplus.CPlusPlusMangler;
import com.ochafik.lang.jnaerator.parser.*;
import static com.ochafik.lang.jnaerator.parser.Statement.*;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;
import com.ochafik.lang.jnaerator.parser.Struct.MemberVisibility;
import com.ochafik.lang.jnaerator.parser.TypeRef.*;
import com.ochafik.lang.jnaerator.parser.Expression.*;
import com.ochafik.lang.jnaerator.parser.Function.Type;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder.ListWrapper;
import com.ochafik.lang.jnaerator.parser.Declarator.*;
import com.ochafik.lang.jnaerator.runtime.VirtualTablePointer;
import com.ochafik.util.CompoundCollection;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import static com.ochafik.lang.jnaerator.TypeConversion.*;

public class Symbols {
		public final Map<Integer, Element> resolvedVariables = new HashMap<Integer, Element>();
	public final Map<Integer, Element> resolvedTypes = new HashMap<Integer, Element>();
	public final Map<Element, SymbolTable> elementTables = new HashMap<Element, SymbolTable>();
	public Element getType(Identifier ident) {
		return resolvedTypes.get(ident.getId());	
	}
	public Element getVariable(Identifier ident) {
		return resolvedVariables.get(ident.getId());	
	}
	public SymbolTable getEnclosingSymbolTable(Element element) {
		while (element != null) {
			SymbolTable st = elementTables.get(element);
			if (st != null)
				return st;
			element = element.getParentElement();
		}
		return null;
	}
	public boolean isClassType(TypeRef tr) {
		if (tr instanceof SimpleTypeRef)
			return getType(((SimpleTypeRef)tr).getName()) instanceof Struct;
		return false;
	}
	public boolean isEnumType(TypeRef tr) {
		if (tr instanceof SimpleTypeRef)
			return getType(((SimpleTypeRef)tr).getName()) instanceof com.ochafik.lang.jnaerator.parser.Enum;
		return false;
	}
	public boolean isEnumItem(Identifier ident) {
		Element v = getVariable(ident);
		if (v instanceof Declarator)
			v = ((Declarator)v).getParentElement();
		if (v instanceof Declaration)
			v = ((Declaration)v).getValueType();
		
		Element type;
		if (v instanceof SimpleTypeRef)
			return getType(((SimpleTypeRef)v).getName()) instanceof com.ochafik.lang.jnaerator.parser.Enum;
		
		return false; // TODO ?
	}
    public static class SymbolTable {
    		public final SymbolTable parent;
    		public final Symbols symbols;
    		public final Map<Identifier, Element> variableDefinitions = new HashMap<Identifier, Element>();
		public final Map<Identifier, Element> typeDefinitions = new HashMap<Identifier, Element>();
		public final Identifier subNamespace;
		public final Element owner;
		
		public SymbolTable(Symbols symbols) {
			this.parent = null;
			this.symbols = symbols;
			this.owner = null;
			this.subNamespace = null;
		}
		public SymbolTable(SymbolTable parent, Identifier subNamespace, Element owner) {
			this.parent = parent;
			this.symbols = parent.symbols;
			this.subNamespace = subNamespace;
			this.owner = owner;
			if (owner != null)
				symbols.elementTables.put(owner, this);
		}
		public void defineVariable(String name, Element element) {
			defineVariable(ident(name), element);
		}
		public void defineVariable(Identifier name, Element element) {
			variableDefinitions.put(name, element);
		}
		public void defineType(Identifier name, Element element) {
			typeDefinitions.put(name, element);
		}
		public Element resolveVariable(Identifier ident) {
			return resolve(ident, true);
		}
		public Element resolveType(Identifier ident) {
			return resolve(ident, false);
		}
		private Element resolve(Identifier ident, boolean varOrType) {
			if (ident == null)
				return null;
			// TODO handle new namespace
			Identifier lastIdent = ident.resolveLastSimpleIdentifier();
			Element resolved = (varOrType ? variableDefinitions : typeDefinitions).get(lastIdent);
			if (resolved == null) {
				if (parent != null)
					return parent.resolve(ident, varOrType);
                //if (ident.getParentElement() instanceof TypeRef && ident.getParentElement().getParentElement() instanceof VariablesDeclaration)
                //    System.out.println("hehehe");
				return null;
			}
			//System.out.println("Resolved " + ident + " as " + resolved);
			//System.err.println("Resolved " + ident + " as " + resolved);
            (varOrType ? symbols.resolvedVariables : symbols.resolvedTypes).put(ident.getId(), resolved);
			return resolved;
		}
    }
    static Symbols resolveSymbols(Element root) {
    		Symbols symbols = new Symbols();
		final SymbolTable rootTable = new SymbolTable(symbols);
		root.accept(new Scanner() {
			SymbolTable currentTable = rootTable;
            
            /*public void visitTypeRef(TypeRef element) {
                super.visitTypeRef(element);
            }*/
			public void visitSimpleTypeRef(SimpleTypeRef element) {
                super.visitSimpleTypeRef(element);
				currentTable.resolveType(element.getName());
			}
			public void visitVariableRef(VariableRef element) {
                super.visitVariableRef(element);
				currentTable.resolveVariable(element.getName());
			}
			
			@Override
            public void visitArg(final Arg element) {
            		super.visitArg(element);
            		currentTable.defineVariable(element.getName(), element);
            }
			@Override
            public void visitDirectDeclarator(final DirectDeclarator element) {
            		super.visitDirectDeclarator(element);
            		currentTable.defineVariable(element.getName(), element);
            }
			@Override
            public void visitBlock(final Block element) {
            		currentTable = new SymbolTable(currentTable, null, element);
            		try {
					super.visitBlock(element);
				} finally {
					currentTable = currentTable.parent;
				}
            }
			@Override
            public void visitFunction(final Function element) {
            		currentTable.defineVariable(element.getName(), element);
            		currentTable = new SymbolTable(currentTable, null, element);
            		try {
            			super.visitFunction(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
            }
			@Override
            public void visitStruct(final Struct element) {
            		currentTable.defineType(element.getTag(), element);
            		currentTable = new SymbolTable(currentTable, element.getTag(), element);
            		try {
            			super.visitStruct(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
            }
			@Override
            public void visitNamespace(final Namespace element) {
            		currentTable = new SymbolTable(currentTable, element.getName(), element);
            		try {
            			super.visitNamespace(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
            }
			@Override
            public void visitFor(final For element) {
            		currentTable = new SymbolTable(currentTable, null, element);
            		try {
            			super.visitFor(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
            }
            @Override
            public void visitIf(final If element) {
            		currentTable = new SymbolTable(currentTable, null, element);
            		try {
            			super.visitIf(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
			}
			@Override
            public void visitTry(final Try element) {
            		currentTable = new SymbolTable(currentTable, null, element);
            		try {
            			super.visitTry(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
			}
			@Override
            public void visitCatch(final Catch element) {
            		currentTable = new SymbolTable(currentTable, null, element);
            		try {
            			super.visitCatch(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
			}
			@Override
			public void visitWhile(final While element) {
            		currentTable = new SymbolTable(currentTable, null, element);
            		try {
            			super.visitWhile(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
			}
			@Override
			public void visitDoWhile(final DoWhile element) {
            		currentTable = new SymbolTable(currentTable, null, element);
            		try {
            			super.visitDoWhile(element);
            		
				} finally {
					currentTable = currentTable.parent;
				}
			}
        });
        
        return symbols;
	}
}
