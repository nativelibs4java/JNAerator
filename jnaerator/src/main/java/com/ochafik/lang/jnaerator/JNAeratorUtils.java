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

import java.util.ArrayList;
import java.util.List;

import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import com.ochafik.lang.jnaerator.parser.Declarator;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations;
import com.ochafik.lang.jnaerator.parser.Declarator.DirectDeclarator;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.TypeDef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;
public class JNAeratorUtils {
	static String getExactTypeDefName(Element e) {
		Element parent = e.getParentElement();
		if (parent == null || !(parent instanceof TypeDef))
			return null;
		TypeDef td = (TypeDef) parent;
		return findBestPlainStorageName(td);
	}
	static List<String> guessOwnerName(Element e) {
		Element parent = e.getParentElement();
		if (parent == null)
			return null;
		
//		StoredDeclarations td = as(parent, StoredDeclarations.class);
//		String bestName = JNAeratorUtils.findBestPlainStorageName(td);
//		if (bestName != null)
//			return Arrays.asList(bestName);
		
		List<String> ns = new ArrayList<String>();
		
		while (parent != null && !(parent instanceof DeclarationsHolder)) {
			if (parent instanceof Arg) {
				Arg arg = (Arg)parent;
				if (arg.getName() != null)
					ns.add(arg.getName());
			} else if (parent instanceof Function) {
				Function f = (Function)parent;
				if (f.getName() != null)
					ns.add(0, f.getName().toString());
				return ns;
			} else if (parent instanceof StoredDeclarations) {
				StoredDeclarations sd = (StoredDeclarations)parent;
				String bestName = findBestPlainStorageName(sd);
				if (bestName != null) {
					ns.add(0, bestName);
					
				}
			} else if (parent instanceof Expression) {
				if (!ns.contains("expression"))
					ns.add("expression");
			}
			parent = parent.getParentElement();
		}
		return ns;
		
	}
	
	public static String findBestPlainStorageName(StoredDeclarations sd) {
		if (sd == null)
			return null;
		
		TaggedTypeRef ttr = sd.getValueType() instanceof TaggedTypeRef  ? (TaggedTypeRef)sd.getValueType() : null;
		String idealName = null;
		if (ttr != null && ttr.getTag() != null) {
			String tn = ttr.getTag().toString();
			if (tn.startsWith("_"))
				idealName = tn.substring(1);
		}
		DirectDeclarator plainDecl = null, idealDecl = null;
		boolean plainHasNiceName = false, hasMoreThanOnePlainDecl = false;
		for (Declarator st : sd.getDeclarators()) {
			if (st instanceof DirectDeclarator) {
				DirectDeclarator decl = (DirectDeclarator)st;
				String name = decl.getName();
				if (name.equals(idealName)) {
					idealDecl = decl;
					break;
				}
				boolean hasNiceName = !decl.getName().startsWith("_");
				if (hasMoreThanOnePlainDecl || plainDecl != null && !(!plainHasNiceName && hasNiceName)) {
					hasMoreThanOnePlainDecl = true;
					continue;
				}
				plainDecl = decl;
				plainHasNiceName = hasNiceName;
			}
			// TODO play with other names ("pointed_by", "returned_by"...)
		}
		return 
			idealDecl != null ? idealDecl.resolveName() :
			plainDecl != null && !hasMoreThanOnePlainDecl ? plainDecl.resolveName() :
			null
		;
	}
}
