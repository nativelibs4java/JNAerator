/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.*;
import java.util.Stack;

/**
 *
 * @author ochafik
 */
public class DeclarationsScanner extends Scanner {

    Stack<DeclarationsHolder> declarationsHolders = new Stack<DeclarationsHolder>();

    @Override
    public void visitStruct(Struct struct) {
        declarationsHolders.push(struct);
        try {
            super.visitStruct(struct);
        } finally {
            declarationsHolders.pop();
        }
    }

    @Override
    public void visitSourceFile(SourceFile header) {
        declarationsHolders.push(header);
        try {
            super.visitSourceFile(header);
        } finally {
            declarationsHolders.pop();
        }
    }
}
