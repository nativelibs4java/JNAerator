/*
 Copyright (c) 2009-2013 Olivier Chafik, All Rights Reserved
	
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
package com.ochafik.lang.jnaerator;

import java.util.HashSet;
import java.util.Set;

import com.ochafik.lang.jnaerator.parser.Identifier;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Function.SignatureType;

public class Signatures {

    private final Set<Identifier> classSignatures = new HashSet<Identifier>();
    private final Set<String> variablesSignatures = new HashSet<String>(),
            methodsSignatures = new HashSet<String>();

    public boolean addClass(Identifier sig) {
        return classSignatures.add(sig);
    }

    public boolean addMethod(Function fun) {
        return addMethod(fun.computeSignature(SignatureType.JavaStyle));
    }

    public boolean addMethod(String sig) {
        return methodsSignatures.add(sig);
    }

    public boolean addVariable(String sig) {
        return variablesSignatures.add(sig);
    }

    /**
     * TODO: CLEAN THIS UGLY HACK Rewrites function name until its signature
     * doesn't collide anymore with existing signatures
     */
    public Identifier findNextMethodName(String originalSignature, Identifier originalName) {
        String signature = originalSignature;
        Identifier name = originalName;
        int n = 1;
        while (!methodsSignatures.add(signature)) {
            String suffix = "$" + (++n);
            name = ident(originalName + suffix);
            int i = originalSignature.indexOf("(");
            signature = originalSignature.substring(0, i) + suffix + originalSignature.substring(i);
        }
        return name;
    }
}
