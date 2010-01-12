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

import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;

public class TaggedTypeRefDeclaration extends Declaration {
	public TaggedTypeRefDeclaration(TaggedTypeRef namedTypeRef) {
		setTaggedTypeRef(namedTypeRef);
	}
	public TaggedTypeRefDeclaration() {}

	TaggedTypeRef namedTypeRef;
	
	@Override
	public TaggedTypeRefDeclaration addModifiers(Modifier... mds) {
		return (TaggedTypeRefDeclaration) super.addModifiers(mds);
	}
	//public String resove
	/*public String getName() {
		return getTaggedTypeRef() != null ? namedTypeRef.getTag() : null;
	}
	public void setName(String name) {
		if (getTaggedTypeRef() != null)
			getTaggedTypeRef().setTag(name);
	}*/
	public void setTaggedTypeRef(TaggedTypeRef namedTypeRef) {
		this.namedTypeRef = changeValue(this, this.namedTypeRef, namedTypeRef);
	}
	public TaggedTypeRef getTaggedTypeRef() {
		return namedTypeRef;
	}
	
	@Override
	public boolean replaceChild(Element child, Element by) {
		if (child.equals(getTaggedTypeRef())) {
			setTaggedTypeRef((TaggedTypeRef)by);
			return true;
		}
		return super.replaceChild(child, by);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitTaggedTypeRefDeclaration(this);
	}

	@Override
	public String toString(CharSequence indent) {
		if (getTaggedTypeRef() == null)
			return null;
		
		TaggedTypeRef tr = getTaggedTypeRef();
		return formatComments(indent, false, true, true) + 
			tr.toString(indent) + (tr.isForwardDeclaration() ? ";" : "") +
			(getCommentAfter() == null ? "" : getCommentAfter());
	}

}
