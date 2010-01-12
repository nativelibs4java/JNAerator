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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.Declarator.PointerStyle;
import com.ochafik.util.string.StringUtils;

public class RococoaUtils {
	static TypeRef ROCOCOA_ID_TYPEREF = new TypeRef.SimpleTypeRef("id");

	//static Pattern methodNameMatchesStaticConstructorPattern = Pattern.compile("^NS.*?([A-Z][a-z]+)$");
	static Pattern shortNamesPattern = Pattern.compile("([A-Z]+?[a-z]*)(?:[A-Z][a-z]|$)");
	
	public static List<String> getShortNames(Struct type) {
		String name = type.getTag().toString();
		List<String> shortNames = new ArrayList<String>();
		String base = name.startsWith("NS") ? name.substring(2) : name;
	
		Matcher matcher = shortNamesPattern.matcher(base);
		int i = 0;
		while (matcher.find(i)) {
			String n = matcher.group(1);
			shortNames.add(base.substring(matcher.start()));
			i = matcher.start() + n.length();
		}
		return shortNames;
	}
	public static String getMethodName(Function function) {
		StringBuilder nb = new StringBuilder(); 
		nb.append(function.getName());
		boolean firstArg = true;
		for (Arg arg : function.getArgs()) {
			if (arg.isVarArg())
				continue;
			
			if (firstArg)
				firstArg = false;
			else {
				nb.append("_");
				nb.append(arg.getSelector());
			}
		}
		return nb.toString();
	}
	public static boolean methodNameMatchesObjcStaticConstructor(Struct type, Identifier identifier) {
		String name = identifier.toString();
		for (String shortName : getShortNames(type)) {
			if (name.matches(StringUtils.uncapitalize(shortName) + "(With.*)?"))//"([A-Z]\\w*)?"))
				return true;
			
			if (name.matches("[a-z]+" + StringUtils.capitalize(shortName) + "(With.*)?"))//"([A-Z]\\w*)?"))
				return true;
		}
		return false;
	}
	public static TypeRef fixReturnType(Function function) {
		TypeRef returnType = function.getValueType();
		
		if (returnType == null)
			returnType = RococoaUtils.ROCOCOA_ID_TYPEREF;
		
		Struct declaringClass = function.findParentOfType(Struct.class);
		if (returnType.toString().equals("id")) {
			String pointedClassName;
			if (function.getName().toString().matches("^(alloc|(init|copy|mutableCopy)([A-Z].*)?)$") || RococoaUtils.methodNameMatchesObjcStaticConstructor(declaringClass, function.getName()))
				pointedClassName = declaringClass.getTag().toString();
			else
				/// Lets subclasses redefine method return type when parent method return type is ID
				pointedClassName = "NSObject";
				
			returnType = new TypeRef.Pointer(new TypeRef.SimpleTypeRef(pointedClassName), PointerStyle.Pointer);
		}
		return returnType;
	}
	
}
