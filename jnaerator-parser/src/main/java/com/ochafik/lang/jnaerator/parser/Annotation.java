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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Annotation extends Element {

    TypeRef annotationClass;
    final Map<String, Expression> namedArguments = new LinkedHashMap<String, Expression>();
    Expression defaultArgument;
	
	public Annotation() {
	}
	public Annotation(TypeRef annotationClass) {
        this(annotationClass, null);
    }
	public Annotation(TypeRef annotationClass, Expression defaultArgument) {
        this(annotationClass, defaultArgument, Collections.<String, Expression>emptyMap());
    }
	public Annotation(TypeRef annotationClass, Expression defaultArgument, Map<String, Expression> namedArguments) {
		setAnnotationClass(annotationClass);
        setDefaultArgument(defaultArgument);
		setNamedArguments(namedArguments);
	}

    public void setDefaultArgument(Expression defaultArgument) {
        this.defaultArgument = changeValue(this, this.defaultArgument, defaultArgument);
    }
    
    public Expression getDefaultArgument() {
        return defaultArgument;
    }
    
    
	public void setNamedArguments(Map<String, Expression> namedArguments) {
		changeValue(this, this.namedArguments, namedArguments);
	}
	public Map<String, Expression> getNamedArguments() {
		return Collections.unmodifiableMap(namedArguments);
	}
	
	public TypeRef getAnnotationClass() {
		return annotationClass;
	}
	public void setAnnotationClass(TypeRef annotationClass) {
		this.annotationClass = changeValue(this, this.annotationClass, annotationClass);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitAnnotation(this);
	}
	
	@Override
	public Element getNextChild(Element child) {
        return getNextSibling(namedArguments.values(), child);
	}

	@Override
	public Element getPreviousChild(Element child) {
		return getPreviousSibling(namedArguments.values(), child);
	}

	@Override
	public boolean replaceChild(Element child, Element by) {
        if (child == getAnnotationClass()) {
            setAnnotationClass((TypeRef)by);
            return true;
        }
		if (child == getDefaultArgument()) {
            setDefaultArgument((Expression) by);
            return true;
        }
		return replaceChild(namedArguments, Expression.class, this, child, by) ||
            super.replaceChild(child, by);
	}

}
