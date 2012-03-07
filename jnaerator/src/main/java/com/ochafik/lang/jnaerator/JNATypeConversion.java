/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.Expression;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
/**
 *
 * @author ochafik
 */
public class JNATypeConversion extends TypeConversion {

    public JNATypeConversion(Result result) {
        super(result);
    }
 
    public Expression getEnumItemValue(com.ochafik.lang.jnaerator.parser.Enum.EnumItem enumItem) { 
        return cast(typeRef(int.class), findEnumItem(enumItem));
    }
    
}
