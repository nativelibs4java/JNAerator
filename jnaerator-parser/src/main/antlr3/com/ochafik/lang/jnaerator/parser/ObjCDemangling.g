/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public Lticense for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/


/**
	This grammar is by no mean complete.
	It is able to parse preprocessed C & Objective-C files and can tolerate some amount of C++. 
	It lacks serious expression support, which is being worked on.
	Complex variable declarations may not be supported, such as complex signatures of functions that return function pointers...
*/

grammar ObjCDemangling;
options {
	backtrack = false;
	//output = AST;
	//memoize = true;
	//k = 3;
}

@header { 
/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.ochafik.lang.jnaerator.parser;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;


import static com.ochafik.lang.jnaerator.parser.ElementsHelper.*;
import static com.ochafik.lang.jnaerator.parser.TypeRef.*;
import static com.ochafik.lang.jnaerator.parser.Expression.*;
import static com.ochafik.lang.jnaerator.parser.Declaration.*;
import static com.ochafik.lang.jnaerator.parser.Identifier.*;
import static com.ochafik.lang.jnaerator.parser.Statement.*;
import static com.ochafik.lang.jnaerator.parser.Declarator.*;
import static com.ochafik.lang.jnaerator.parser.StoredDeclarations.*;

}

@members {
	protected String next() {
		return input.LT(1).getText();
	}
	
	static HashMap<String, TypeRef> predefRefs = new HashMap<String, TypeRef>();
	static {
		predefRefs.put("c", typeRef("char"));
		
		predefRefs.put("i", typeRef("int"));
		predefRefs.put("s", typeRef("short"));
		predefRefs.put("l", typeRef("long"));
		predefRefs.put("q", typeRef("long").addModifiers(Modifier.Long));
		predefRefs.put("C", typeRef("char").addModifiers(Modifier.Unsigned));
		predefRefs.put("I", typeRef("int").addModifiers(Modifier.Unsigned));
		predefRefs.put("S", typeRef("short").addModifiers(Modifier.Unsigned));
		predefRefs.put("L", typeRef("long").addModifiers(Modifier.Unsigned));
		predefRefs.put("Q", typeRef("long").addModifiers(Modifier.Unsigned, Modifier.Long));
		predefRefs.put("f", typeRef("float"));
		predefRefs.put("d", typeRef("double"));
		predefRefs.put("B", typeRef("BOOL"));
		predefRefs.put("v", typeRef("void"));
	}
	boolean isPredefRef(String s) {
		return predefRefs.containsKey(s);
	}
	public TypeRef getPredefRef(String s) {
		return predefRefs.get(s).clone();
	}
}

@lexer::header { 
/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.ochafik.lang.jnaerator.parser;
}

mangledTypeEOF returns [TypeRef type] : mangledType { $type = $mangledType.type; } EOF;
mangledType returns [TypeRef type]
	:	structType { $type = $structType.type; } |
		unionType { $type = $unionType.type; } |
		arrayType { $type = $arrayType.type; } |
		primitiveType { $type = $primitiveType.type; } |
		'^' (
			//'?' { $type = new TypeRef.Pointer(typeRef("void"), PointerStyle.Pointer); } |
			pointed=mangledType { 
				$type = new TypeRef.Pointer($pointed.type, Declarator.PointerStyle.Pointer); 
			}
		)
	;
	
primitiveType returns [TypeRef type]
	:
		{ isPredefRef(next()) }? IDENTIFIER {
			$type = getPredefRef($IDENTIFIER.text);
		} |
		'*' { $type = new TypeRef.Pointer(typeRef("char"), PointerStyle.Pointer); } |
		'@' { $type = typeRef("id"); } |
		'#' { $type = typeRef("Class"); } |
		':' { $type = typeRef("SEL"); } |
		'?' { $type = typeRef("__opaque_unknown_type"); }
	;
	
structType returns [Struct type]
	:	{ 
			$type = new Struct();
			$type.setType(Struct.Type.CStruct);
			$type.setForwardDeclaration(true);
		} '{' 
		(
			tagName=IDENTIFIER '=' {
				$type.setTag(ident($tagName.text));
			} 
		)
		(
			f=structField {
				$type.addDeclaration($f.field);
				$type.setForwardDeclaration(false);
			}
		)*
		'}'

	;

arrayType returns [TypeRef type]
	:	 
		'[' 
			size=DECIMAL_NUMBER?
			ct=mangledType { 
				$type = new TypeRef.ArrayRef($ct.type, $size.text == null ? null : expr(Constant.Type.Int, Integer.parseInt($size.text))); 
				
			}
		']' 
	; 
	
methodType returns [Function method]
	:
		rt=mangledType {
			$method = new Function(Function.Type.ObjCMethod, null, $rt.type);
		}
		DECIMAL_NUMBER 
		'@' DECIMAL_NUMBER ':' DECIMAL_NUMBER 
		(
			at=mangledType DECIMAL_NUMBER {
				$method.addArg(new Arg(null, $at.type));
			}
		)*
		EOF
	;
unionType returns [Struct type]
	:	{ 
			$type = new Struct();
			$type.setType(Struct.Type.CUnion);
		} '(' 
		(
			tagName=IDENTIFIER '=' {
				$type.setTag(ident($tagName.text));
			} 
		)?
		( 
			f=structField { $type.addDeclaration($f.field); } 
		)+
		')'

	;
	
structField returns [VariablesDeclaration field]
@init {
	DirectDeclarator declarator = null;
}
	:
		{ 
			$field = new VariablesDeclaration(); 
			$field.addDeclarator(declarator = new DirectDeclarator());
		}
		(
			fieldName=STRING {
				declarator.setName(String.valueOf(Constant.parseString($fieldName.text).getValue()));
			}
		)?
		(
			fieldType=mangledType {
				$field.setValueType($fieldType.type);
			} |
			'b' bits=DECIMAL_NUMBER {
				declarator.setBits(Integer.parseInt($bits.text));
				$field.setValueType(typeRef("int"));
			}
		)		
	;
	
fragment
Letter
	:	'$' |
		'_' |
		'A'..'Z' |
		'a'..'z'
	;

IDENTIFIER
	:	
		Letter+
	;

DECIMAL_NUMBER
	:	//('-' | '+')?
		('0' | '1'..'9' '0'..'9'*)
	;

OCTAL_NUMBER
	: 	'0'
		('0'..'7')+
	;

fragment
CharEscape
	:	'\\'
		(
			'b' | 
			't' | 
			'n' | 
			'f' | 
			'r' | 
			'\"' | 
			'\'' | 
			'\\' 
		) |
		OctalEscape 
	;


fragment
OctalEscape
	:	'\\' (
			('0'..'3') ('0'..'7') ('0'..'7') |
			('0'..'7') ('0'..'7') |
			('0'..'7')
		)
	;

STRING
	:	'"'
		(
			CharEscape |
			~(
				'\\' |
				'"'
			)
		)*
		'"'
	;
