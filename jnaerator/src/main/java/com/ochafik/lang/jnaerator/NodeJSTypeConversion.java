/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.parser.Enum.EnumItem;
import com.ochafik.lang.jnaerator.parser.Expression;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.parser.TypeRef;

/**
 *
 * @author ochafik
 */
public class NodeJSTypeConversion extends TypeConversion {

    public NodeJSTypeConversion(Result result) {
        super(result);
    }

    @Override
    protected JavaPrim getCppBoolMappingType() {
        return JavaPrim.Boolean;
    }

    @Override
    public Expression getEnumItemValue(EnumItem enumItem, boolean forceConstants) {
        return varRef(enumItem.getName());
    }
}
