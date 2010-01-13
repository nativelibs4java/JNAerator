/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ochafik.lang.jnaerator.parser;

import static com.ochafik.util.string.StringUtils.*;

import com.ochafik.lang.jnaerator.parser.Declarator.ArrayDeclarator;
import com.ochafik.lang.jnaerator.parser.Declarator.DirectDeclarator;
import com.ochafik.lang.jnaerator.parser.Declarator.FunctionDeclarator;
import com.ochafik.lang.jnaerator.parser.Declarator.PointerDeclarator;
import com.ochafik.lang.jnaerator.parser.Enum.EnumItem;
import com.ochafik.lang.jnaerator.parser.Enum.Type;
import com.ochafik.lang.jnaerator.parser.Expression.ArrayAccess;
import com.ochafik.lang.jnaerator.parser.Expression.AssignmentOp;
import com.ochafik.lang.jnaerator.parser.Expression.BinaryOp;
import com.ochafik.lang.jnaerator.parser.Expression.Cast;
import com.ochafik.lang.jnaerator.parser.Expression.ConditionalExpression;
import com.ochafik.lang.jnaerator.parser.Expression.Constant;
import com.ochafik.lang.jnaerator.parser.Expression.Constant.IntForm;
import com.ochafik.lang.jnaerator.parser.Expression.EmptyArraySize;
import com.ochafik.lang.jnaerator.parser.Expression.ExpressionSequence;
import com.ochafik.lang.jnaerator.parser.Expression.FunctionCall;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRef;
import com.ochafik.lang.jnaerator.parser.Expression.MemberRefStyle;
import com.ochafik.lang.jnaerator.parser.Expression.New;
import com.ochafik.lang.jnaerator.parser.Expression.NewArray;
import com.ochafik.lang.jnaerator.parser.Expression.NullExpression;
import com.ochafik.lang.jnaerator.parser.Expression.OpaqueExpression;
import com.ochafik.lang.jnaerator.parser.Expression.TypeRefExpression;
import com.ochafik.lang.jnaerator.parser.Expression.UnaryOp;
import com.ochafik.lang.jnaerator.parser.Expression.VariableRef;
import com.ochafik.lang.jnaerator.parser.Identifier.QualifiedIdentifier;
import com.ochafik.lang.jnaerator.parser.Identifier.SimpleIdentifier;
import com.ochafik.lang.jnaerator.parser.Statement.Block;
import com.ochafik.lang.jnaerator.parser.Statement.Catch;
import com.ochafik.lang.jnaerator.parser.Statement.DeclarationStatement;
import com.ochafik.lang.jnaerator.parser.Statement.ExpressionStatement;
import com.ochafik.lang.jnaerator.parser.Statement.If;
import com.ochafik.lang.jnaerator.parser.Statement.Return;
import com.ochafik.lang.jnaerator.parser.Statement.Throw;
import com.ochafik.lang.jnaerator.parser.Statement.Try;
import com.ochafik.lang.jnaerator.parser.StoredDeclarations.TypeDef;
import com.ochafik.lang.jnaerator.parser.TypeRef.ArrayRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;
import com.ochafik.lang.jnaerator.parser.TypeRef.Pointer;
import com.ochafik.lang.jnaerator.parser.TypeRef.Primitive;
import com.ochafik.lang.jnaerator.parser.TypeRef.SimpleTypeRef;
import com.ochafik.lang.jnaerator.parser.TypeRef.TaggedTypeRef;
import com.ochafik.util.listenable.Adapter;
import com.ochafik.util.listenable.ListenableCollections;
import com.ochafik.util.listenable.Pair;
import com.ochafik.util.string.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author ochafik
 */
public class Printer implements Visitor {

    StringBuilder out = new StringBuilder();
    Stack<String> indentStack = new Stack<String>();
    String indent = "";
    protected static boolean beginEachCommentLineWithStar = true;

    protected void indent(String indent) {
        indentStack.push(this.indent = indent);
    }

    protected void indent() {
        indent(indent == null ? "\t" : indent + "\t");
    }

    protected void deindent() {
        indent = indentStack.pop();
    }

    public Printer() {}
    public Printer(String initialIdent) {
        indent = initialIdent == null ? "" : initialIdent;
    }
    
    @Override
    public String toString() {
        return out.toString();
    }

    protected void expressionPre(Expression x) {
        append(x.getParenthesis() ? "(" : "");
    }
    protected void expressionPost(Expression x) {
        append(x.getParenthesis() ? ")" : "");
    }
    public void visitConstant(Constant e) {
        expressionPre(e);
        Object value = e.getValue();
        if (e.getIntForm() == IntForm.Hex)
            append("0x", Long.toHexString(value instanceof Long ? ((Long)value).longValue() : ((Integer)value).longValue()).toUpperCase());
        else if (e.getIntForm() == IntForm.Octal)
            append(Long.toOctalString(value instanceof Long ? ((Long)value).longValue() : ((Integer)value).longValue()).toUpperCase());
        else if (e.getType() == null)
            append("");
        else {
            switch (e.getType()) {
            case Null:
                append("null");
                break;
            case Byte:
            case Double:
            case Int:
            case Short:
            case UInt:
                append(value);
                break;
            case Float:
                append(value, 'F');
                break;
            case ULong:
            case Long:
                append(value, 'L');
                break;
            case String:
                append('"', StringUtils.javaEscape((String)value), '"');
                break;
            case Char:
                append('\'', StringUtils.javaEscape(((Character)value).toString()), '\'');
                break;
            case IntegerString:
                int intVal = ((Integer)value).intValue();
                append('\'', Constant.intStr(intVal), '\'');
                break;
            case LongString:
                long longVal = ((Long)value).intValue();
                append(
                    '\'',
                    Constant.intStr((int)(longVal & 0xffffffffL)),
                    Constant.intStr((int)((longVal >>> 32) & 0xffffffffL)),
                    '\''
                );
                break;
            case Bool:
                append(((Boolean)value).toString());
                break;
            default:
                throw new UnsupportedOperationException("visitConstant not implemented for constqnt type " + e.getType());
            }
        }
        expressionPost(e);
    }

    public void visitArg(Arg e) {
        implode(e.getAnnotations(), "\n");
        if (e.getValueType() != null) {
            if (e.getName() != null)
                variableDeclarationToString(e.getValueType(), e.getName(), e.isVarArg());
            else
                append(e.getValueType()).append(e.isVarArg() ? "..." : null);
		} else
			append("...");
    }

    public void visitEnum(Enum e) {
        modifiersStringPrefix(e);
        append("enum ", e.getTag());
        if (e.getTag() != null)
            append(" ");
        if (!e.getInterfaces().isEmpty())
            append("implements ").implode(e.getInterfaces(), ", ").append(" ");

        append("{\n");
        indent();
        List<EnumItem> items = e.getItems();
        for (int i = 0, len = items.size(); i < len; i++) {
            EnumItem item = items.get(i);
            append(indent, item, i < len - 1 ? "," : null, "\n");
        }
        deindent();
        append(indent, "}");
    }

    public void visitFunction(Function e) {
        String s = "";
		TypeRef valueType = e.getValueType();
		Identifier name = e.getName();
		List<Modifier> modifiers = e.getModifiers();

		if (e.getType() == null) {
			append("<no function type>");
            return;
        }

		formatComments(e, false, true, true);
		if (!e.getAnnotations().isEmpty())
			implode(e.getAnnotations(), "\n" + indent).append("\n", indent);

		String preMods;
		switch (e.getType()) {
		case StaticInit:
			implode(modifiers, " ");
            space(!modifiers.isEmpty());
            append(e.getBody() == null ? ";" : e.getBody());
            break;
		case CFunction:
		case CppMethod:
		case JavaMethod:
			implode(modifiers, " ");
            space(!modifiers.isEmpty());
			append(valueType);
            space(valueType != null);
            append(name, "(").implode(e.getArgs(), ", ").append(")");

            if (!e.getThrown().isEmpty())
                append(" throws ").implode(e.getThrown(), ", ");

            indent();
            if (e.getBody() == null)
                append(";");
            else
                append(" ", e.getBody());
            break;
		case ObjCMethod:
			append(modifiers.contains(Modifier.Static) ? "+" : "-");
			space();
            if (valueType != null)
                append("(", valueType, ")");
            append(name);
            boolean firstArg = true;
            for (Arg arg : e.getArgs()) {
				if (arg.isVarArg()) {
                    if (!firstArg)
                        append(", ");
					append("...");
				} else {
					if (!firstArg)
						append(' ', arg.getSelector());
					
					append(":(", arg.createMutatedType(), ')', arg.getName());
				}
                firstArg = false;
			}
            append(";");
            break;
		default:
			throw new RuntimeException(e.getType().toString());
		}

        if (e.getAsmName() != null)
            append("__asm(\"", e.getAsmName(), "\") ");

        if (!e.getInitializers().isEmpty())
            append(" : ").implode(e.getInitializers(), ", ");

        if (e.getCommentAfter() != null)
            append(" ", e.getCommentAfter());


    }

    public void visitFunctionPointerDeclaration(FunctionPointerDeclaration e) {
        modifiersStringPrefix(e);
        append(e.getValueType());
        if (e.getDefaultValue() != null)
            append(" = ", e.getDefaultValue());
        append(";");
    }

    public void visitStruct(Struct e) {

		formatComments(e, false, true, true);
        modifiersStringPrefix(e);

		if (e.getType() != null)
		switch (e.getType()) {
			case CPPClass:
				append("class ", e.getTag());
                break;
			case CUnion:
				append("union ", e.getTag() == null ? null : " ", e.getTag());
                break;
			case JavaClass:
            case JavaInterface:
			
				append(e.getType() == Struct.Type.JavaClass ? "class " : "interface ", e.getTag());
                if (!e.getParents().isEmpty())
                    append(" extends ").implode(e.getParents(), ", ");
                if (!e.getProtocols().isEmpty())
                    append(" implements ").implode(e.getProtocols(), ", ");

                break;
			case ObjCClass:
				append(e.isForwardDeclaration() ? "@class " : "@interface ", e.getTag());
                if (e.getCategoryName() != null)
                    append(" (", e.getCategoryName(), ")");
                break;
			case ObjCProtocol:
				append("@protocol ", e.getTag());
                break;
			case CStruct:
			default:
				append("struct ", e.getTag());
                break;
		}

        if (!e.isForwardDeclaration()) {
            space(e.getTag() != null).append("{\n");
            indent();
            implode(e.getDeclarations(), "\n" + indent);
            deindent();
            append("\n", indent, "}");
        }
    }

    public void visitTypeDef(TypeDef e) {
        formatComments(e, false, true, true);
        modifiersStringPrefix(e);
        append("typedef ");
        valueTypeAndStorageSuffix(e);
        append(";");
        if (e.getCommentAfter() != null)
            append(" ", e.getCommentAfter().trim());

    }

    public void visitSimpleTypeRef(SimpleTypeRef e) {
        formatComments(e, true, false, false).modifiersStringPrefix(e).append(e.getName());
    }

    public void visitFunctionSignature(FunctionSignature e) {
        if (e.getFunction() == null)
				return;

        assert e.getFunction().getBody() == null;
        modifiersStringPrefix(e);
        append(e.getFunction().getValueType()).space(e.getFunction().getValueType() != null);
        append("(");
        modifiersStringPrefix(e.getFunction());
        switch (e.getType()) {
            case CFunction:
                append("*");
                break;
            case ObjCBlock:
                append("^");
                break;
        }
        append(e.getFunction().getName());
        append(")(");
        implode(e.getFunction().getArgs(), ", ");
        append(")");
        append(e.getModifiers().isEmpty() ? "" : " ");
        implode(e.getModifiers(), " ");
    }

    public void visitPrimitive(Primitive e) {
        modifiersStringPrefix(e, e.getName() != null);
        append(e.getName());
    }

    public void visitPointer(Pointer e) {
        modifiersStringPrefix(e);
		append(e.getTarget());
        append(e.getPointerStyle());
    }

    public void visitArray(ArrayRef e) {
        modifiersStringPrefix(e);
        append(e.getTarget());
        bracketsToString(e);
    }

    public void visitSourceFile(SourceFile e) {
        implode(e.getDeclarations(), "\n" + indent);
    }



    public void visitEnumItem(EnumItem e) {
        formatComments(e, false, true, true);
        append(e.getName());
        if (!e.getArguments().isEmpty()) {
            if (e.getType() == Type.C)
                append(" = ", e.getArguments().get(0));
            else {
                append("(");
                implode(e.getArguments(), ", ");
                append(")");
            }
        }
        if (e.getBody() != null) {
            append(" {\n\t", indent);
            indent();
            implode(e.getBody().getDeclarations(), "\n" + indent);
            deindent();
            append("\n", indent, "}");
        }
        space(e.getCommentAfter() != null).append(e.getCommentAfter());
    }

    public void visitUnaryOp(UnaryOp e) {
        expressionPre(e);
        append(UnaryOp.unOpsRev.get(e.getOperator())).append(e.getOperand());
        expressionPost(e);
    }

    public void visitVariableRef(VariableRef e) {
        expressionPre(e);
        append(e.getName());
        expressionPost(e);
    }

    public void visitBinaryOp(BinaryOp e) {
        expressionPre(e);
        append(e.getFirstOperand()).space().append(BinaryOp.binOpsRev.get(e.getOperator())).space().append(e.getSecondOperand());
        expressionPost(e);
    }

    protected Printer targetPrefix(MemberRef e) {
        if (e.getTarget() == null || e.getMemberRefStyle() == null)
            return this;

        String sep;
        switch (e.getMemberRefStyle()) {
        case Arrow:
            sep = "->";
            break;
        case Dot:
            sep = ".";
            break;
        case Colons:
            sep = "::";
            break;
        default:
            assert false;
            sep = null;
        }
        if (sep != null) {
            append(e.getTarget());
            append(sep);
        }
        return this;
    }
    public void visitFunctionCall(FunctionCall e) {
        expressionPre(e);
        if (e.getMemberRefStyle() == MemberRefStyle.SquareBrackets) {
            /// Objective-C method call
            append('[');
            append(e.getTarget());
            if (e.getFunction() != null) {
                append(' ');
                append(e.getFunction());
            }
            List<Pair<String, Expression>> args = e.getArguments();
            for (int i = 0, len = args.size(); i < len; i++) {
                Pair<String, Expression> arg = args.get(i);
                if (i > 0) {
                    append(' ');
                    append(arg.getFirst());
                }
                append(':');
                append(arg.getSecond());
            }
            append(']');
        } else {
            targetPrefix(e);
            if (e.getFunction() != null)
                append(e.getFunction());
            append("(");
            append(StringUtils.implode(ListenableCollections.adapt(e.getArguments(), new Adapter<Pair<String, Expression>, Expression>() {

                public Expression adapt(Pair<String, Expression> value) {
                    return value.getValue();
                }

            }), ", "), ")");
        }
        expressionPost(e);
    }

    public void visitCast(Cast e) {
        expressionPre(e);
        append("(").append(e.getType()).append(")").append(e.getTarget());
        expressionPost(e);
    }

    public void visitDeclarator(Declarator e) {
        if (e.isParenthesized())
			append('(');

        implode(e.getModifiers(), " ").space(!e.getModifiers().isEmpty());
        
		if (e instanceof DirectDeclarator)
            append(((DirectDeclarator)e).getName());
        else if (e instanceof PointerDeclarator) {
            PointerDeclarator d = (PointerDeclarator)e;
            append(d.getPointerStyle(), d.getTarget());
        } else if (e instanceof FunctionDeclarator) {
            FunctionDeclarator d = (FunctionDeclarator)e;
            append(d.getTarget(), '(').implode(d.getArgs(), ", ").append(")");
        } else if (e instanceof ArrayDeclarator) {
            ArrayDeclarator d = (ArrayDeclarator)e;
            append(d.getTarget(), '[').implode(d.getDimensions(), "][").append("]");
        }
        
		if (e.isParenthesized())
			append(')');
		if (e.getBits() >= 0)
			append(":", e.getBits());
		if (e.getDefaultValue() != null)
			append(" = ", e.getDefaultValue());
	}

    public void visitVariablesDeclaration(VariablesDeclaration e) {
        formatComments(e, false, true, true);
		if (!e.getAnnotations().isEmpty())
            implode(e.getAnnotations(), "\n" + indent).append("\n", indent);

        modifiersStringPrefix(e);
        valueTypeAndStorageSuffix(e);
        append(";");
        if (e.getCommentAfter() != null)
            space().append(e.getCommentAfter());
    }

    public void visitTaggedTypeRefDeclaration(TaggedTypeRefDeclaration e) {
        if (e.getTaggedTypeRef() == null)
			return;

		TaggedTypeRef tr = e.getTaggedTypeRef();
		formatComments(e, false, true, true);
        append(tr, tr.isForwardDeclaration() ? ";" : null, e.getCommentAfter());
    }

    public void visitTaggedTypeRef(TaggedTypeRef e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visitEmptyArraySize(EmptyArraySize e) {
        expressionPre(e);
        expressionPost(e);
    }

    public void visitDefine(Define e) {
        append(indent, "#define ", e.getName());
        space(e.getValue() != null).append(e.getValue());
    }

    public void visitTypeRefExpression(TypeRefExpression e) {
        expressionPre(e);
        append(e.getType());
        expressionPost(e);
    }

    public void visitNew(New e) {
        expressionPre(e);
        append("new ").append(e.getType());
        if (e.getConstruction() == null)
            append("()");
        else
            append(e.getConstruction());
        expressionPost(e);
    }

    public void visitAnnotation(Annotation e) {
        append(indent, "@", e.getAnnotationClass());
        if (e.getArgument() != null)
            append(e.getArgument());
        else if (!e.getArguments().isEmpty())
            append("(").implode(e.getArguments(), ", ").append(")");
    }

    public void visitEmptyDeclaration(EmptyDeclaration e) {
        formatComments(e, true, true, false);
    }

    public void visitNewArray(NewArray e) {
        expressionPre(e);
        append("new ").append(e.getType()).append("[").implode(e.getDimensions(), "][").append("]");
        expressionPost(e);
    }

    public void visitPointerDeclarator(PointerDeclarator e) {
        visitDeclarator(e);
    }

    public void visitArrayDeclarator(ArrayDeclarator e) {
        visitDeclarator(e);
    }

    public void visitDirectDeclarator(DirectDeclarator e) {
        visitDeclarator(e);
    }

    public void visitFunctionDeclarator(FunctionDeclarator e) {
        visitDeclarator(e);
    }

    public void visitModifiableElement(ModifiableElement e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visitBlock(Block e) {
        append('{');
        if (!e.getStatements().isEmpty()) {
            if (e.isCompact()) {
                append(' ');
                implode(e.getStatements(), ", ");
                append(' ');
            } else {
                indent();
                append(indent);
                implode(e.getStatements(), "\n" + indent);
                deindent();
                append('\n', indent);
            }
        }
        append('}');
    }

    public void visitExpressionStatement(ExpressionStatement e) {
        append(e.getExpression(), ";");
    }

    public void visitIf(If e) {
        append("if (", e.getCondition(), ") ");
        if (e.getThenBranch() == null)
            append("<null>");
        else {
            if (e.getThenBranch() instanceof Block) {
                append(e.getThenBranch());
                if (e.getElseBranch() != null)
                    append(" ");
            } else {
                indent();
                append("\n", indent, e.getThenBranch());
                deindent();
                if (e.getElseBranch() != null)
                    append("\n", indent);
            }
        }

        if (e.getElseBranch() != null) {
            append("else ");
            if (e.getElseBranch() instanceof Block) {
                append(e.getElseBranch());
            } else {
                indent();
                append("\n", indent, e.getElseBranch());
                deindent();
            }
        }
    }

    public void visitNullExpression(NullExpression e) {
        expressionPre(e);
        append("null");
        expressionPost(e);
    }

    public void visitReturn(Return e) {
        append("return ", e.getValue(), ";");
    }

    public void visitExternDeclarations(ExternDeclarations e) {
        append("extern \"", e.getLanguage(), "\" {");
        indent();
        implode(e.getDeclarations(), "\n" + indent);
        deindent();
        append("\n", indent, "}");
    }

    public void visitOpaqueExpression(OpaqueExpression e) {
        expressionPre(e);
        append(e.getOpaqueString());
        expressionPost(e);
    }

    public void visitArrayAccess(ArrayAccess e) {
        expressionPre(e);
        append(e.getTarget());
        append("[");
        append(e.getIndex());
        append("]");
        expressionPost(e);
    }

    public void visitMemberRef(MemberRef e) {
        expressionPre(e);
        targetPrefix(e);
        append(e.getName());
        expressionPost(e);
    }

    public void visitAssignmentOp(AssignmentOp e) {
        expressionPre(e);
        append(e.getTarget()).space().append(AssignmentOp.assignOpsRev.get(e.getOperator())).space().append(e.getValue());
        expressionPost(e);
    }

    public void visitConditionalExpression(ConditionalExpression e) {
        expressionPre(e);
        append(e.getTest()).append(" ? ").append(e.getThenValue()).append(" : ").append(e.getElseValue());
        expressionPost(e);
    }

    public void visitExpressionSequence(ExpressionSequence e) {
        expressionPre(e);
        implode(e.getSequence(), ", ");
        expressionPost(e);
    }

    public void visitSimpleIdentifier(SimpleIdentifier e) {
        append(e.getName());

        if (!e.getTemplateArguments().isEmpty()) {
            append("<");
            implode(e.getTemplateArguments(), ", ");
            append(" >");
        }
    }

    public void visitQualifiedIdentifier(QualifiedIdentifier e) {
        implode(e.getIdentifiers(), String.valueOf(e.getSeparator()));
    }

    public void visitDeclarationStatement(DeclarationStatement e) {
        append(e.getDeclaration());
    }

    public void visitThrow(Throw e) {
        append("throw ", e.getExpression(), ";");
    }

    public void visitProperty(Property e) {
        append("@property");
        if (!e.getModifiers().isEmpty())
            append("(").implode(e.getModifiers(), " ").append(")");
		append(" ", e.getDeclaration());
    }

    public void visitFriendDeclaration(FriendDeclaration e) {
        append("friend ", e.getFriend());
    }

    public void visitTry(Try e) {
        append("try {\n");
        indent();
        append(indent, e.getTryStatement());
        deindent();
        append("\n", indent, "}");
        implode(e.getCatches(), " ");
        if (e.getFinallyStatement() != null) {
            append(" finally {\n");
            indent();
            append(indent);
            append(e.getFinallyStatement());
            deindent();
            append("\n", indent, "}");
        }
    }

    public void visitCatch(Catch e) {
        append("catch (", e.getDeclaration(), ") {\n\t", indent);
        if (e.getBody() != null) {
            indent();
            append(e.getBody());
            deindent();
        }
        append("\n", indent, "}");
    }

    protected Printer space() {
        return space(true);
    }
    protected Printer space(boolean doIt) {
        if (doIt)
            append(" ");
        return this;
    }
    public Printer append(Object... os) {
        for (Object e : os) {
            if (e instanceof Element)
                ((Element)e).accept(this);
            else if (e != null)
                out.append(e);
        }
        return this;
    }

    protected final Printer implode(Iterable<?> elements, Object separator) {
        if (elements != null) {
            String sepStr = separator.toString();
            boolean first = true;
            for (Object s : elements) {
                if (s == null)
                    continue;

                if (first)
                    first = false;
                else
                    append(sepStr);

                append(s);
            }
        }
        return this;
	}

    public static String formatComments(CharSequence indent, String commentBefore, String commentAfter, boolean mergeCommentsAfter, boolean allowLineComments, boolean skipLineAfter, String... otherComments) {
        if (indent == null)
            indent = "";
		List<String> nakedComments = new ArrayList<String>();
		List<String> src = new ArrayList<String>();
		if (commentBefore != null)
			src.add(commentBefore);
		if (mergeCommentsAfter && commentAfter != null)
			src.add(commentAfter);
		src.addAll(Arrays.asList(otherComments));

		if (src.isEmpty())
			return null;

		for (String c : src) {
			if (c == null)
				continue;

			c = Element.cleanComment(c).trim();
			nakedComments.add(c);
		}

		String uniqueLine = null;
		if (nakedComments.size() == 1 && !nakedComments.get(0).contains("\n"))
			uniqueLine = nakedComments.get(0);

		String suffix = skipLineAfter ? "\n" + indent : "";
		if (uniqueLine != null && allowLineComments)
			return "/// " + uniqueLine.replace("\\u", "\\\\u") + suffix;


		String content = beginEachCommentLineWithStar ?
			" * " + StringUtils.implode(nakedComments, "\n").replaceAll("\n", "<br>\n" + indent + " * ") + "\n" + indent :
			"\t" + StringUtils.implode(nakedComments, "\n").replaceAll("\n", "<br>" + LINE_SEPARATOR + indent + "\t");

		return "/**" + LINE_SEPARATOR + indent + content.replace("\\u", "\\\\u") + " */" + suffix;
	}

    public Printer formatComments(Element e, boolean mergeCommentsAfter, boolean allowLineComments, boolean skipLineAfter, String... otherComments) {
		append(formatComments(indent, e.getCommentBefore(), e.getCommentAfter(), mergeCommentsAfter, allowLineComments, skipLineAfter, otherComments));
        return this;
	}

	protected Printer modifiersStringPrefix(ModifiableElement e) {
        return modifiersStringPrefix(e, true);
    }
    protected Printer modifiersStringPrefix(ModifiableElement e, boolean addSpace) {
        List<Modifier> modifiers = e.getModifiers();
        if (modifiers != null && !modifiers.isEmpty()) {
            implode(modifiers, " ");
            if (addSpace)
                space();
        }
        return this;
	}

    protected void variableDeclarationToString(TypeRef e, String varName, boolean isVarArg) {
        if (e instanceof FunctionSignature) {
            FunctionSignature fs = (FunctionSignature)e;
            if (!isVarArg && fs.getFunction() != null && fs.getFunction().getName() != null) {
				append(indent);
                return;
            }
        } else if (e instanceof ArrayRef) {
            ArrayRef ar = (ArrayRef)e;
            append(ar.getTarget(), isVarArg ? "... " : " ", varName);
            bracketsToString(ar);
            return;
        }
        append(e).append(isVarArg ? "... " : " ").append(varName);
	}

    protected void bracketsToString(ArrayRef e) {
        append("[").implode(e.getDimensions(), "][").append("]");
    }

    protected void valueTypeAndStorageSuffix(StoredDeclarations e) {
		if (e.getValueType() instanceof FunctionSignature) {
			FunctionSignature sig = (FunctionSignature) e.getValueType();
			if (sig.getFunction() != null) {
				Identifier name = sig.getFunction().getName();
				if (name != null && e.declarators.size() == 1) {
					String stoName = e.declarators.get(0).resolveName();
					if (name.equals(stoName) || stoName == null) {
						append(sig);
                        return;
                    }
				}
			}
		}
		append(e.getValueType()).space(!e.getDeclarators().isEmpty()).implode(e.getDeclarators(), ", ");
	}

}
