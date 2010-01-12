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

import com.ochafik.lang.jnaerator.parser.Declaration;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Enum;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.TaggedTypeRefDeclaration;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.Declarator;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;
import com.ochafik.lang.jnaerator.parser.Declarator.DirectDeclarator;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.TypeDef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;

/**
 * <ul>
 * <li>Cocoa enums usually don't have names, but they are followed by a typedef that maps an enum-like name to integer. This scanner removes this fake typedef and imports back the name of the enum.
 * </li>
 * </ul> 
 * @author ochafik
 */
public class ObjectiveCToJavaPreScanner extends Scanner {
//	@Override
//	public void visitStruct(Struct struct) {
//		if (struct.isForwardDeclaration())
//			struct.replaceBy(null);
//		else
//			super.visitStruct(struct);
//	}
	@Override
	public void visitEnum(Enum e) {
		Element parent = e.getParentElement();
		if (e.getTag() == null || !(parent instanceof TypeDef)) {
			// Hack to infer the enum name from the next typedef NSUInteger NSSomethingThatLooksLikeTheEnumsIdentifiers
			Element base = parent instanceof Declaration ? e.getParentElement() : e;
			Element next = base.getNextSibling();
			if (!handleAppleEnumTypeDef(next, e)) {
				Element previous = base.getPreviousSibling();
				Element beforePrevious = previous == null ? null : previous.getPreviousSibling();
				if (previous != null && !(beforePrevious instanceof TaggedTypeRefDeclaration && ((TaggedTypeRefDeclaration)beforePrevious).getTaggedTypeRef() instanceof Enum)) {
					handleAppleEnumTypeDef(previous, e);
				}
			}
		}
		String comment = e.getCommentBefore();
		if (comment != null) {
			// TODO parse cocoa comments here
		}
		super.visitEnum(e);
	}
	@Override
	public void visitVariablesDeclaration(VariablesDeclaration v) {
		if (v.getDeclarators().isEmpty() && v.getValueType() instanceof TaggedTypeRef) {
			TaggedTypeRefDeclaration d = new TaggedTypeRefDeclaration((TaggedTypeRef)v.getValueType());
			d.importDetails(v, false);
			v.replaceBy(d);
			d.accept(this);
		} else
			super.visitVariablesDeclaration(v);
	}
	private boolean handleAppleEnumTypeDef(Element nextDeclaration, Enum e) {
		if (!(nextDeclaration instanceof TypeDef))
			return false;
		
		TypeDef typeDef = (TypeDef) nextDeclaration;
		if (typeDef.getDeclarators().size() != 1)
			return false;
		
		Declarator decl = typeDef.getDeclarators().get(0);
		if (!(decl instanceof DirectDeclarator))
			return false;
		
		TypeRef type = typeDef.getValueType();
		if (!(type instanceof TypeRef.SimpleTypeRef))
			return false;
		
		Identifier simpleType = ((TypeRef.SimpleTypeRef)type).getName();
		if (simpleType == null ||
		!(
			simpleType.equals("NSUInteger") || 
			simpleType.equals("NSInteger") ||
			simpleType.equals("int32_t") ||
			simpleType.equals("uint32_t") ||
			simpleType.equals("CFIndex")
		))
			return false;
		
		String name = decl.resolveName();
		if (e.getTag() != null && !e.getTag().equals("_" + name))
			return false;
		
		Element ep = e.getParentElement();
		ep.addToCommentBefore(typeDef.getCommentBefore());
		ep.addToCommentBefore(typeDef.getCommentAfter());
		typeDef.importDetails(ep, true);
		ep.replaceBy(null);
		typeDef.setValueType(e);
		
		
		System.err.println("Inferred enum name : " + name);
		return false;
	}
}
