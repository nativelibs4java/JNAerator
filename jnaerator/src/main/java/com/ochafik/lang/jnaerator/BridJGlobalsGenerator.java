/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.DeclarationsHolder;
import com.ochafik.lang.jnaerator.parser.Expression;
import com.ochafik.lang.jnaerator.parser.Identifier;
import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;

public class BridJGlobalsGenerator extends GlobalsGenerator {

    public BridJGlobalsGenerator(Result result) {
        super(result);
    }

    @Override
    protected void convertGlobals(VariablesDeclaration globals, Signatures signatures, DeclarationsHolder out, Expression nativeLibFieldExpr, Identifier callerLibraryName, String callerLibrary) {
        int[] iChild = new int[1];
        result.declarationsConverter.convertVariablesDeclaration(globals, signatures, out, iChild, true, callerLibraryName, callerLibraryName, callerLibrary);//globals, out, null, true, null, null, callerLibraryName, callerLibrary);
    }
}
