/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
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
package com.ochafik.lang.jnaerator.parser;

import com.ochafik.lang.jnaerator.parser.Struct.MemberVisibility;
import java.util.List;

public abstract class Declaration extends Statement {//ModifiableElement {
    
	protected TypeRef valueType;
	protected MemberVisibility visibility;
	/*protected int bits = -1;
	
	public void setBits(int bits) {
		this.bits = bits;
	}
	public int getBits() {
		return bits;
	}*/
	
	@Override
	public Declaration clone() {
		return (Declaration) super.clone();
	}
	
	public TypeRef getValueType() {
		return valueType;
	}
	public void setValueType(TypeRef valueType) {
		if (valueType == null)
			valueType = null;
		this.valueType = changeValue(this, this.valueType, valueType);
	}
	
	@Override
	public List<Modifier> harvestModifiers() {
		List<Modifier> mods = super.harvestModifiers();
		if (getValueType() != null)
			mods.addAll(getValueType().harvestModifiers());
		return mods;
	}
	
	@Override
	public boolean replaceChild(Element child, Element by) {
		if (child == getValueType()) {
			setValueType((TypeRef) by);
			return true;
		}
		return super.replaceChild(child, by);
	}
		
	public void setVisibility(MemberVisibility visibility) {
		this.visibility = visibility;
	}
	public MemberVisibility getVisibility() {
		return visibility;
	}
	
}
