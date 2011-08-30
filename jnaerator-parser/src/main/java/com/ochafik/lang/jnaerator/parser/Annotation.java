/*
	Copyright (c) 2009-2011 Olivier Chafik, All Rights Reserved
	
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
import java.util.List;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;

public class Annotation extends Element {

    TypeRef annotationClass;
	//Class<? extends java.lang.annotation.Annotation> annotationClass;
	final List<Expression> arguments = new ArrayList<Expression>();
	String argument;
	
	public Annotation() {
		
	}
	
	public Annotation(Class<? extends java.lang.annotation.Annotation> annotationClass, Expression... arguments) {
		this(typeRef(annotationClass), arguments);
	}
	public Annotation(TypeRef annotationClass, Expression... arguments) {
		setAnnotationClass(annotationClass);
		setArguments(Arrays.asList(arguments));
	}
	public Annotation(TypeRef annotationClass, String argument) {
		setAnnotationClass(annotationClass);
		setArgument(argument);
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}
	public String getArgument() {
		return argument;
	}
	public void setArguments(List<Expression> arguments) {
		changeValue(this, this.arguments, arguments);
	}
	public List<Expression> getArguments() {
		return unmodifiableList(arguments);
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
		return getNextSibling(arguments, child);
	}

	@Override
	public Element getPreviousChild(Element child) {
		return getPreviousSibling(arguments, child);
	}

	@Override
	public boolean replaceChild(Element child, Element by) {
        if (child == getAnnotationClass()) {
            setAnnotationClass((TypeRef)by);
            return true;
        }
		return replaceChild(arguments, Expression.class, this, child, by);
	}

}
