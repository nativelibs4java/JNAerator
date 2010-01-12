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

public class FriendDeclaration extends Declaration {
	Declaration friend;
	public FriendDeclaration() {
		super();
	}
	public void setFriend(Declaration friend) {
		this.friend = changeValue(this, this.friend, friend);
	}
	public Declaration getFriend() {
		return friend;
	}
	@Override
	public boolean replaceChild(Element child, Element by) {
		if (child == getFriend()) {
			setFriend((Declaration) by);
			return true;
		}
		return super.replaceChild(child, by);
	}
	public FriendDeclaration(Declaration friend) {
		super();
		setFriend(friend);
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visitFriendDeclaration(this);
	}
	@Override
	public String toString(CharSequence indent) {
		return "friend "  + (getFriend() == null ? "null" : getFriend().toString(indent));
	}

}
