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

import com.ochafik.lang.jnaerator.parser.Arg;
import static com.ochafik.lang.jnaerator.parser.Identifier.*;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.parser.Declaration;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Modifier;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.TaggedTypeRefDeclaration;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.Declarator;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;
import com.ochafik.lang.jnaerator.parser.Declarator.DirectDeclarator;
import com.ochafik.lang.jnaerator.parser.Declarator.MutableByDeclarator;
import com.ochafik.lang.jnaerator.parser.ModifierKind;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.TypeDef;
import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;

public class CToJavaPreScanner extends Scanner {
	public CToJavaPreScanner() {
	}
	
	@Override
	public void visitStruct(Struct struct) {
		super.visitStruct(struct);
		if (struct.isForwardDeclaration() && struct.getTag() != null) {
			Element parent = struct.getParentElement();
			if (!(parent instanceof TaggedTypeRefDeclaration)) {
				TypeRef tr = new TypeRef.SimpleTypeRef(struct.getTag());
				for (Modifier mod : struct.getModifiers()) {
					if (mod.isA(ModifierKind.StorageClassSpecifier))
						tr.addModifiers(mod);
				}
				struct.replaceBy(tr);
				tr.accept(this);
			}
		}
	}
	@Override
	public void visitFunctionSignature(FunctionSignature functionSignature) {
		// TODO Auto-generated method stub
		super.visitFunctionSignature(functionSignature);
	}
	
	@Override
	public void visitTaggedTypeRefDeclaration(TaggedTypeRefDeclaration taggedTypeRefDeclaration) {
		super.visitTaggedTypeRefDeclaration(taggedTypeRefDeclaration);
		TaggedTypeRef tr = taggedTypeRefDeclaration.getTaggedTypeRef();
		if (tr != null) {
			String before = tr.getCommentBefore();
			tr.setCommentBefore(taggedTypeRefDeclaration.getCommentBefore());
			tr.addToCommentBefore(before);
			taggedTypeRefDeclaration.setCommentBefore(null);
		}
	}
	
	@Override
	public void visitTypeDef(TypeDef v) {
		super.visitTypeDef(v);
		
		Element toAddAfter = v;
		
		TypeRef valueType = v.getValueType();
		String bestName = null, origName = null;
		TaggedTypeRef ttr = null;
		if (valueType instanceof TaggedTypeRef) {
			ttr = (TaggedTypeRef) valueType;
			bestName = JNAeratorUtils.findBestPlainStorageName(v);
			if (bestName != null)
				origName = ttr.getTag() != null ? ttr.getTag().toString() : null;
		}
		
		/// Explode comma-separated variables declarations
		for (Declarator vs : v.getDeclarators()) {
			if (vs == null)// || vs instanceof DirectDeclarator && v.getDeclarators().size() == 1)
				continue;
			
			String name = vs.resolveName();
			if (vs instanceof DirectDeclarator && name.equals(bestName) && ttr != null && origName != null) {
				DirectDeclarator rep = new DirectDeclarator(origName);
				vs.replaceBy(rep);
				ttr.setTag(ident(bestName));
				vs = rep;
				name = origName;
			}
			Declaration decl = null;
		
			Declarator.MutableByDeclarator type = vs.mutateType(v.getValueType());
			if (type instanceof TypeRef) {
				TypeRef tr = (TypeRef)type;
				decl = new StoredDeclarations.TypeDef(tr, new DirectDeclarator(name, vs.getBits()));
				decl.importDetails(v, false);
				decl.importDetails(vs, false);
				decl.importDetails(tr, true);
			} else {
				continue;
			}
			
			toAddAfter.insertSibling(decl, false);
			toAddAfter = decl;

			//decl.accept(this);//super.visitVariablesDeclaration(decl);
		}
		if (toAddAfter != v)
			v.replaceBy(null);
		
		/*if (v.getDeclarators().size() == 1 && v.getDeclarators().get(0) instanceof DirectDeclarator)
			return;
		
		EmptyDeclaration empty = new EmptyDeclaration();
		v.replaceBy(empty);
		
		Element toAddAfter = empty;
		
		/// Explode comma-separated variables declarations
		for (Declarator vs : v.getDeclarators()) {
			if (vs == null)
				continue;
			
			Declarator.MutableByDeclarator type = vs.mutateType(v.getValueType());
			if (!(type instanceof TypeRef))
				continue;
			
			TypeRef tr = (TypeRef)type;
			StoredDeclarations.TypeDef decl = new StoredDeclarations.TypeDef(tr, new DirectDeclarator(vs.resolveName()));
			decl.importDetails(v, false);
			decl.importDetails(vs, false);
			decl.importDetails(tr, true);
			
			toAddAfter.insertSibling(decl, false);
			toAddAfter = decl;

			decl.accept(this);
		}
		*/
	}
	
	@Override
	public void visitArg(Arg arg) {
		Declarator d = arg.getDeclarator();
		if (d != null && !(d instanceof DirectDeclarator)) {
			MutableByDeclarator type = d.mutateType(arg.getValueType());
			if (type instanceof TypeRef) {
				arg.setValueType((TypeRef)type);
				arg.setDeclarator(new DirectDeclarator(d.resolveName(), d.getBits()));
			} else {
				type = null;
			}
		}
		super.visitArg(arg);
	}
	@Override
	public void visitVariablesDeclaration(VariablesDeclaration v) {
		super.visitVariablesDeclaration(v);
		
		Element toAddAfter = v;
		
		/// Explode comma-separated variables declarations
		int nDecl = v.getDeclarators().size();
		for (Declarator vs : v.getDeclarators()) {
			if (vs == null || vs instanceof DirectDeclarator && nDecl == 1)
				continue;
			
			Declaration decl = null;
		
			Declarator.MutableByDeclarator type = vs.mutateType(v.getValueType());
			if (type instanceof TypeRef) {
				TypeRef tr = (TypeRef)type;
				decl = new VariablesDeclaration(tr, new DirectDeclarator(vs.resolveName(), vs.getBits()));
				decl.importDetails(v, false);
				decl.importDetails(vs, false);
				decl.importDetails(tr, true);
			} else if (type instanceof Function) {
				Function f = (Function)type;
				f.setName(new SimpleIdentifier(vs.resolveName()));
				decl = (Function)type;
				decl.importDetails(v, false);
				decl.importDetails(vs, false);
			} else if (type instanceof Declaration) {
				decl = (Declaration)type;
			} else {
				continue;
			}
			
			toAddAfter.insertSibling(decl, false);
			toAddAfter = decl;

			decl.accept(this);//super.visitVariablesDeclaration(decl);
		}
		if (toAddAfter != v)
			v.replaceBy(null);
	}
	
}
