/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.VariablesDeclaration;

/**
 *
 * @author ochafik
 */
public class DeclarationsMigrator extends DeclarationsScanner {

    Result result;

    public DeclarationsMigrator(Result result) {
        this.result = result;
    }
    boolean insideTypeRef;

    @Override
    public void visitVariablesDeclaration(VariablesDeclaration v) {
        super.visitVariablesDeclaration(v);
        //TODO
    }
}
