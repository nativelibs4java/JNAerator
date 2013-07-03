/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;

/**
 *
 * @author ochafik
 */
public class NodeJSGlobalsGenerator extends GlobalsGenerator {

    public NodeJSGlobalsGenerator(Result result) {
        super(result);
    }

    @Override
    protected void convertGlobals(VariablesDeclaration globals, Signatures signatures, DeclarationsHolder out, Expression nativeLibFieldExpr, Identifier callerLibraryName, String callerLibrary) throws UnsupportedConversionException {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
