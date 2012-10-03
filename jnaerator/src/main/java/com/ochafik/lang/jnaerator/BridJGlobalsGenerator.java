/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
