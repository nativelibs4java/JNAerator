/*	
 Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
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

import java.util.List;

import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;

public abstract class GlobalsGenerator {

    public GlobalsGenerator(Result result) {
        this.result = result;
    }
    final Result result;

    @SuppressWarnings("unchecked")
    protected abstract void convertGlobals(VariablesDeclaration globals, Signatures signatures, DeclarationsHolder out, Expression nativeLibFieldExpr, Identifier callerLibraryName, String callerLibrary) throws UnsupportedConversionException;

    public void convertGlobals(List<VariablesDeclaration> list, Signatures signatures, DeclarationsHolder out, Expression nativeLibFieldExpr, Identifier libraryNameExpression, String library) {
        if (list == null) {
            return;
        }
        if (result.config.skipLibraryInstanceDeclarations) {
            return;
        }
        for (VariablesDeclaration v : list) {
            try {
                convertGlobals(v, signatures, out, nativeLibFieldExpr, libraryNameExpression, library);
            } catch (UnsupportedConversionException ex) {
                out.addDeclaration(result.declarationsConverter.skipDeclaration(v, ex.toString()));
            }
        }
    }
}
