/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved

	This file is part of JNAerator (https://jnaerator.googlecode.com/).

	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.ochafik.lang.jnaerator.parser;

import com.ochafik.lang.jnaerator.parser.Expression.Constant;
import java.util.Collection;

public class ValuedModifier implements Modifier {
    Modifier modifier;
    Constant value;
    public ValuedModifier() {}
    public ValuedModifier(Modifier modifier, Constant value) {
        this.modifier = modifier;
        this.value = value;
    }

    public Collection<ModifierKind> getKinds() {
        return modifier.getKinds();
    }
    
    

    public Modifier getModifier() {
        return modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public Constant getValue() {
        return value;
    }

    public void setValue(Constant value) {
        this.value = value;
    }
    
    public Modifier resolveAlias() {

        Modifier modifier = getModifier();
        if (modifier == null)
            return this;
        return new ValuedModifier(modifier.resolveAlias(), getValue());
    }

    public boolean isA(ModifierKind kind) {
        Modifier modifier = getModifier();
        return modifier != null && modifier.isA(kind);
    }

    public boolean isAnyOf(ModifierKind... kinds) {
        Modifier modifier = getModifier();
        return modifier != null && modifier.isAnyOf(kinds);
    }

    public boolean isAllOf(ModifierKind... kinds) {
        Modifier modifier = getModifier();
        return modifier != null && modifier.isAllOf(kinds);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ValuedModifier))
            return false;

        ValuedModifier m = (ValuedModifier)o;
        if ((getModifier() == null) != (m.getModifier() == null))
            return false;
        if (getModifier() != null && !getModifier().equals(m.getModifier()))
            return false;
        if ((getValue() == null) != (m.getValue() == null))
            return false;
        if (getValue() != null && !getValue().equals(m.getValue()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hc = 0;
        if (getModifier() != null)
            hc = getModifier().hashCode();
        if (getValue() != null)
            hc ^= getValue().hashCode();
        return hc;
    }

    @Override
    public String toString() {
        
        String s = getModifier() + "(" + getValue() + ")";
        Collection<ModifierKind> kinds = modifier.getKinds();
        if (modifier.getKinds().contains(ModifierKind.Declspec))
			return "__declspec(" + s + ")";
        
        if (kinds.contains(ModifierKind.Attribute))
			return "__attribute__((" + s + "))";
        return s;
    }


}
