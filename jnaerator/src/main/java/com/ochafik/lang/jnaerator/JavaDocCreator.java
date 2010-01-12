package com.ochafik.lang.jnaerator;

import com.ochafik.lang.jnaerator.JNAeratorConfig.GenFeatures;
import com.ochafik.lang.jnaerator.parser.Arg;
import com.ochafik.lang.jnaerator.parser.Element;
import com.ochafik.lang.jnaerator.parser.Function;
import com.ochafik.lang.jnaerator.parser.Scanner;
import com.ochafik.lang.jnaerator.parser.TypeRef.FunctionSignature;
import com.ochafik.util.string.StringUtils;

public class JavaDocCreator extends Scanner {
	
	Result result;
	public JavaDocCreator(Result result) {
		this.result = result;
	}
	
	@Override
	public void visitFunction(Function function) {
		if (!result.config.noComments)
			if (!(function.getParentElement() instanceof FunctionSignature) && result.config.features.contains(GenFeatures.OriginalFunctionSignatures)) {
				function.moveAllCommentsBefore();
				function.addToCommentBefore("Original signature : <code>" + function.computeSignature(true) + "</code>");
			}
//		function.addToCommentBefore("File : " + Element.getFileOfAscendency(function));
		super.visitFunction(function);
//		if (function.getValueType() != null && !function.getValueType().toString().equals("void"))
//			function.addToCommentBefore("@return " + Element.cleanComment(function.getValueType().toString()));
	}
	
	String cleanCom(String com) {
		if (com == null)
			return null;
		com = Element.cleanComment(com).trim().replaceAll("\n", "<br>");
		if (com.trim().length() == 0)
			return null;
		return com;
	}
	@Override
	public void visitArg(Arg arg) {
		super.visitArg(arg);
		if (arg.getName() == null)
			return;
		if (!result.config.features.contains(GenFeatures.FunctionArgsJavaDoc))
			return;
			
		String ca = arg.getCommentAfter(), cb = arg.getCommentBefore();
		if (ca == null && cb == null)
			return;
		Function f = arg.findParentOfType(Function.class);
		if (f == null)
			return;
		
		ca = cleanCom(ca);
		cb = cleanCom(cb);
		
		if (!result.config.noComments)
			f.addToCommentBefore("@param " + arg.getName() + " " + StringUtils.implode(new String[] { ca, cb }, "<br>"));
		arg.setCommentAfter(null);
		arg.setCommentBefore(null);
	}
}
