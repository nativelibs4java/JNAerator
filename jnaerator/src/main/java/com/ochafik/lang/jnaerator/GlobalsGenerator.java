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
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.Declaration;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import com.ochafik.lang.jnaerator.TypeConversion.JavaPrim;
import com.ochafik.lang.jnaerator.TypeConversion.TypeConversionMode;
import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import com.ochafik.lang.jnaerator.parser.Declarator;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.Modifier;
import com.ochafik.lang.jnaerator.parser.ModifierType;
import com.ochafik.lang.jnaerator.parser.Statement;
import com.ochafik.lang.jnaerator.parser.Struct;
import com.ochafik.lang.jnaerator.parser.TypeRef;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;
import com.ochafik.lang.jnaerator.parser.Declarator.PointerStyle;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRefStyle;
import com.ochafik.lang.jnaerator.parser.Expression.VariableRef;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalCallback;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalPointer;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalPointerType;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalStruct;
import com.sun.jna.ptr.ByReference;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
public abstract class GlobalsGenerator {
	public GlobalsGenerator(Result result) {
		this.result = result;
	}

	final Result result;
	
	@SuppressWarnings("unchecked")
	protected abstract void convertGlobals(VariablesDeclaration globals, Signatures signatures, DeclarationsHolder out, Expression nativeLibFieldExpr, Identifier callerLibraryName, String callerLibrary) throws UnsupportedConversionException;
    
	public void convertGlobals(List<VariablesDeclaration> list, Signatures signatures, DeclarationsHolder out, Expression nativeLibFieldExpr, Identifier libraryNameExpression, String library) {		
		if (list == null)
			return;
		if (result.config.skipLibraryInstanceDeclarations)
			return;
		for (VariablesDeclaration v : list) {
			try {
				convertGlobals(v, signatures, out, nativeLibFieldExpr, libraryNameExpression, library);
			} catch (UnsupportedConversionException ex) {
				out.addDeclaration(result.declarationsConverter.skipDeclaration(v, ex.toString()));
			}
		}
	}

}
