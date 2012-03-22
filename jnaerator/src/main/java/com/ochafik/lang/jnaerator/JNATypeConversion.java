/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator;

import com.ochafik.lang.SyntaxUtils;
import com.ochafik.lang.jnaerator.parser.*;

import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.util.listenable.Pair;
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

    @Override
    protected JavaPrim getCppBoolMappingType() {
        return JavaPrim.Byte;
    }
    
    public Pair<Expression, TypeRef> convertExpressionToJava(Expression x, Identifier libraryClassName, boolean promoteNativeLongToLong) throws UnsupportedConversionException {
        Pair<Expression, TypeRef> res = null;
        if (x instanceof Expression.Cast) {
            TypeRef tpe = ((Expression.Cast) x).getType();
            Pair<Expression, TypeRef> casted = convertExpressionToJava(((Expression.Cast) x).getTarget(), libraryClassName, promoteNativeLongToLong);
            TypeRef tr = convertTypeToJNA(tpe, TypeConversionMode.ExpressionType, libraryClassName);
            JavaPrim prim = getPrimitive(tr, libraryClassName);
            if (promoteNativeLongToLong && (prim == JavaPrim.NativeLong || prim == JavaPrim.NativeSize)) {
                prim = JavaPrim.Long;
                tr = typeRef(Long.TYPE);
            }
            Expression val = casted.getFirst();
            if (isString(val)) {
                    val = methodCall(new Expression.New(typeRef(com.ochafik.lang.jnaerator.runtime.StringPointer.class), val), "getPointer");
            } else {
                if (prim == JavaPrim.NativeLong) {
                    val = (Expression) new Expression.New(typeRef(com.sun.jna.NativeLong.class), val);
                } else if (prim == JavaPrim.NativeSize) {
                    val = (Expression) new Expression.New(typeRef(NativeSize.class), val);
                }
            }
            res = typed(val, tr);
        } 
        if (res == null) {
            return super.convertExpressionToJava(x, libraryClassName, promoteNativeLongToLong);
        }
        if (res.getFirst() == null) {
            return null;
        }
        res.getFirst().setParenthesis(x.getParenthesis());
        return (Pair<Expression, TypeRef>) res;
    }
    
}
