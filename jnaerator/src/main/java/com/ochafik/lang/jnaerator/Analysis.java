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

import java.util.Iterator;
import java.util.Collections;
import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import java.util.List;
import java.util.ArrayList;
import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;
import com.ochafik.lang.jnaerator.parser.TypeRef.*;
import com.ochafik.lang.jnaerator.parser.Expression.*;
import com.ochafik.lang.jnaerator.parser.Declarator.*;

/*
 mvn -o compile exec:java -Dexec.mainClass=com.ochafik.lang.jnaerator.JNAerator

 */
public class Analysis {

    Result result;

    public Analysis(Result result) {
        this.result = result;
    }

    public static enum Attribute {

        Type, Declaration, ContainedDeclarations
    }

    public static class Resolver extends Scanner {
        /*public class SymbolTable {
            
         SymbolTable parent;
            
         LinkedHashMap<String, Declaration> declarations = new LinkedHashMap<String, Declaration>();
            
         }
         Stack<SymbolTable> symbolTables = new Stack<SymbolTable>();
         */
    }

    List<String> getNames(Declaration d) {
        if (d instanceof StoredDeclarations) {
            List<String> ret = new ArrayList<String>();
            for (Declarator declarator : ((StoredDeclarations) d).getDeclarators()) {
                ret.add(declarator.resolveName());
            }
            return ret;
        } else if (d instanceof TaggedTypeRefDeclaration) {
            TaggedTypeRef tr = ((TaggedTypeRefDeclaration) d).getTaggedTypeRef();
            Identifier id = tr.getTag();
            if (id instanceof SimpleIdentifier) {
                return Collections.singletonList(((SimpleIdentifier) id).getName());
            }
        }
        return Collections.EMPTY_LIST;
    }

    public Declaration resolveDown(DeclarationsHolder dh, Iterator<SimpleIdentifier> simpleIds) {
        String nextId = simpleIds.next().getName();
        for (Declaration d : dh.getDeclarations()) {
            List<String> names = getNames(d);
            if (names.contains(nextId)) {
                if (!simpleIds.hasNext()) {
                    return d;
                } else {
                    DeclarationsHolder sub = null;
                    if (d instanceof TaggedTypeRefDeclaration) {
                        TaggedTypeRef ttr = ((TaggedTypeRefDeclaration) d).getTaggedTypeRef();
                        if (ttr instanceof DeclarationsHolder) {
                            sub = (DeclarationsHolder) ttr;
                        }
                    }
                    if (sub == null) {
                        return null;
                    }

                    return resolveDown(sub, simpleIds);
                }
            }
        }
        return null;
    }

    public Declaration resolveDown(DeclarationsHolder dh, Identifier id) {
        return resolveDown(dh, id.resolveSimpleIdentifiers().iterator());
    }

    public Declaration resolveUp(Identifier i) {
        Element p = i;// e.getParentElement();
        for (; p != null;) {
            do {//while (!(p instanceof DeclarationsHolder)) {
                p = p.getParentElement();
            } while (!(p instanceof DeclarationsHolder));


        }
        return null;
    }

    public Declaration resolve(Identifier e) {
//        if (e instanceof Declaration)
//            return (Declaration)e;
        Declaration decl = (Declaration) e.getAttribute(Attribute.Declaration);
        if (decl == null) {
            e.setAttribute(Attribute.Declaration, decl = resolveUp(e));
        }
        return decl;
    }
}
