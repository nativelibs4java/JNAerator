package com.ochafik.lang.jnaerator;

import java.util.*;
//import org.bridj.structs.StructIO;
//import org.bridj.structs.Array;

import com.ochafik.lang.jnaerator.parser.*;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;
import com.ochafik.lang.jnaerator.parser.TypeRef.*;
import com.ochafik.lang.jnaerator.parser.Expression.*;
import com.ochafik.lang.jnaerator.parser.Declarator.*;
import com.ochafik.util.listenable.Pair;
import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;

public class MatchingUtils {
    public static TypeRef recognizeSizeOf(Expression e) {
        if (!(e instanceof FunctionCall))
            return null;

        FunctionCall fc = (FunctionCall)e;
        List<Pair<String, Expression>> args = fc.getArguments();
        if (args.size() != 1)
            return null;

        Pair<String, Expression> arg = args.get(0);
        if (arg == null)
            return null;

        Expression f = fc.getFunction();
        if (!(f instanceof VariableRef))
            return null;
        VariableRef vr = (VariableRef)f;
        if (!"sizeof".equals(String.valueOf(vr.getName())))
            return null;

        Expression a = arg.getSecond();
        if (!(a instanceof Expression.TypeRefExpression))
            return null;

        Expression.TypeRefExpression tr = (Expression.TypeRefExpression)a;
        return tr.getType();
    }
    public static Pair<TypeRef, Expression> recognizeSizeOfMult(Expression e) {
        List<Expression> mult = recognizeMultiply(e);
        if (mult == null)
            return null;

        TypeRef typeRef = null;
        Expression f = null;
        for (Iterator<Expression> it = mult.iterator(); it.hasNext();) {
            Expression x = it.next();
            TypeRef tr = recognizeSizeOf(x);
            if (tr != null && typeRef == null) { // only take first sizeof
                typeRef = tr;
            } else {
                if (f == null)
                    f = x;
                else
                    f = expr(f, BinaryOperator.Multiply, x);
            }
        }
        if (typeRef == null)
            return null;
        if (f == null)
            f = expr(1);
        return new Pair<TypeRef, Expression>(typeRef, f);
    }

    public static List<Expression> recognizeMultiply(Expression e) {
        List<Expression> ret = new ArrayList<Expression>();
        if (!recognizeMultiply(e, ret))
            return null;

        return ret;
    }
    public static boolean recognizeMultiply(Expression e, List<Expression> out) {
        if (!(e instanceof BinaryOp))
            return false;

        BinaryOp op = (BinaryOp)e;
        if (op.getOperator() != BinaryOperator.Multiply)
            return false;

        for (Expression sub : new Expression[] { op.getFirstOperand(), op.getSecondOperand() })
            if (!recognizeMultiply(sub, out))
                out.add(sub);

        return true;
    }
}
