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
import java.util.Collection;
import java.util.List;

import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import com.ochafik.util.string.StringUtils;

public class Struct extends TypeRef.TaggedTypeRef implements DeclarationsHolder {//extends StoredDeclarations  {
	Type type;
	MemberVisibility nextMemberVisibility = MemberVisibility.Public;
	final List<Identifier> parents = new ArrayList<Identifier>();
	final List<Identifier> protocols = new ArrayList<Identifier>();
	String categoryName;
	final List<Declaration> declarations = new ArrayList<Declaration>();
	
	public enum MemberVisibility {
		Public, Private, Protected
	}
	public enum Type {
		CStruct,
        CPPClass,
        ObjCClass,
        ObjCProtocol,
        CUnion,
        JavaClass,
        JavaInterface
	}

	@Override
	public Struct addModifiers(Modifier... mds) {
		return (Struct)super.addModifiers(mds);
	}
	
	@Override
	public Element getNextChild(Element child) {
		Element e = super.getNextChild(child);
		if (e != null)
			return e;
		e = getNextSibling(protocols, child);
		if (e != null)
			return e;
		return getNextSibling(declarations, child);
	}

	@Override
	public Element getPreviousChild(Element child) {
		Element e = super.getPreviousChild(child);
		if (e != null)
			return e;
		e = getPreviousSibling(protocols, child);
		if (e != null)
			return e;
		return getPreviousSibling(declarations, child);
	}

	@Override
	public boolean replaceChild(Element child, Element by) {
		if (super.replaceChild(child, by))
			return true;
		
		if (replaceChild(protocols, Identifier.class, this, child, by))
			return true;

		if (replaceChild(parents, Identifier.class, this, child, by))
			return true;

		return replaceChild(declarations, Declaration.class, this, child, by);
	}

	
	public static Struct forwardDecl(SimpleIdentifier name, Type type) {
		Struct s = new Struct();
		s.setForwardDeclaration(true);
		s.setTag(name);
		s.setType(type);
		return s;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	
	public List<Identifier> getParents() {
		return unmodifiableList(parents);
	}
	public void addParent(Identifier parent) {
		if (parent == null)
			return;
		parent.setParentElement(this);
		parents.add(parent);
	}
	public void setParents(List<Identifier> parents) {
		changeValue(this, this.parents, parents);
	}
	public List<Identifier> getProtocols() {
		return unmodifiableList(protocols);
	}
	public void addProtocol(Identifier protocol) {
		if (protocol == null)
			return;
		protocol.setParentElement(this);
		protocols.add(protocol);
	}
	public void addProtocols(Identifier... protocols) {
		List<Identifier> l = new ArrayList<Identifier>(getProtocols());
		l.addAll(Arrays.asList(protocols));
		setProtocols(l);
	}
	public void setProtocols(List<Identifier> protocols) {
		changeValue(this, this.protocols, protocols);
	}
	public Struct setType(Type type) {
		this.type = type;
		switch (type) {
		case ObjCClass:
		case ObjCProtocol:
			break;
		case CPPClass:
			setNextMemberVisibility(MemberVisibility.Private);
			break;
		case CStruct:
		case JavaClass:
		case JavaInterface:
			setNextMemberVisibility(MemberVisibility.Public);
			break;
		default:
			break;
		}
		return this;
	}
	public Type getType() {
		return type;
	}
	public List<Declaration> getDeclarations() {
		return unmodifiableList(declarations);
	}
	public void addDeclaration(Declaration d) {
		if (d == null)
			return;
		
		d.setVisibility(getNextMemberVisibility());
		d.setParentElement(this);
		declarations.add(d);
	}
	public void addDeclarations(Collection<? extends Declaration> ds) {
		if (ds == null)
			return;
		
		for (Declaration d : ds)
			addDeclaration(d);
	}
	public void setDeclarations(List<Declaration> declarations) {
		changeValue(this, this.declarations, declarations);
	}
	
	public void setNextMemberVisibility(MemberVisibility nextVisibility) {
		this.nextMemberVisibility = nextVisibility;
	}
	public MemberVisibility getNextMemberVisibility() {
		return nextMemberVisibility;
	}

    public String bodyToString(CharSequence indent) {
        String lnind = "\n" + indent + "\t";
		return isForwardDeclaration() ? "" :
			(declarations.isEmpty() ?
				"" :
				lnind + implode(declarations, lnind, indent + "\t") + "\n" + indent
			)
		;
    }
	public String toString(CharSequence indent) {
		String body = isForwardDeclaration() ? "" : " {" + bodyToString(indent) + "}";
		
		String pre = formatComments(indent, false, true, true) + 
			getModifiersStringPrefix();
			//commentBefore + "\n" + indent;//(indent, mergeCommentsBeforeAndAfter);
		
		String nameStr = (getTag() == null ? "" : " " + getTag().toString(indent));
//		String javaPublicity = getModifiers().contains(Modifier.Public) ? "public " :
//			getModifiers().contains(Modifier.Protected) ? "protected " :
//			"";
//		if (getModifiers().contains(Modifier.Static))
//			javaPublicity += "static ";
//		if (getModifiers().contains(Modifier.Final))
//			javaPublicity += "final ";
		
		String javaExtension = getParents().isEmpty() ? "" : " extends " + StringUtils.implode(getParents(), ", ");
		String javaImplements = getProtocols().isEmpty() ? "" : " implements " + StringUtils.implode(getProtocols(), ", ");
		
		if (getType() != null)
		switch (getType()) {
			case CPPClass:
				return pre + "class" + nameStr + body;
			case CUnion:
				return pre + "union" + nameStr + body;
			case JavaClass:
				return pre + "class" + nameStr + javaExtension + javaImplements + body;
			case JavaInterface:
				return pre + "interface" + nameStr + javaExtension + javaImplements + body;
			case ObjCClass:
				return pre + (isForwardDeclaration() ? "@class" : "@interface") + nameStr + (getCategoryName() == null ? "" : " (" + getCategoryName() + ")") + body;
			case ObjCProtocol:
				return pre + "@protocol" + nameStr + body; // TODO check this ???
			case CStruct:
			default:
				return pre + "struct" + nameStr + body;
		}
		return null;
	}
	public void accept(Visitor visitor) {
		visitor.visitStruct(this);
	}

	public void setParents(Identifier... ns) {
		setParents(Arrays.asList(ns));
	}
}
