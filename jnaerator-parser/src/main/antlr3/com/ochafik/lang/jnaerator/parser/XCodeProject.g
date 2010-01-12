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

/**
	This grammar is by no mean complete.
	It is able to parse preprocessed C & Objective-C files and can tolerate some amount of C++. 
	It lacks serious expression support, which is being worked on.
	Complex variable declarations may not be supported, such as complex signatures of functions that return function pointers...
*/

grammar XCodeProject;
options {
  backtrack=true;
  output=AST;
  k=3;
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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
}

@members {

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

xCodeProject
	:	map
	;

map returns [Map<String, Object> map]
	:	{ $map = new HashMap<String, Object>(); }
		'{'
		(
			IDENTIFIER '=' value ';' {
				$map.put($IDENTIFIER.text, $value.value); 
			}
		)*
		'}'
	;

value returns [Object value]
	:	constant { $value = $constant.constant; } |
		map  { $value = $map.map; } |
		IDENTIFIER {
			try {
				$value = Integer.parseInt($IDENTIFIER.text); 
			} catch (Exception ex) {
				$value = $IDENTIFIER.text; 
			}
		} |
		list { $value = $list.list; }
	;
	
list returns [List<Object> list]
	:	{ $list = new ArrayList<Object>(); }
		'('
		(
			head=value { $list.add($head.value); }
			(
				',' 
				v=value { $list.add($v.value); }
			)*
		)?
		')'
	;
			
constant returns [Object constant]
	:	//DECIMAL_NUMBER { $constant = Integer.parseInt($DECIMAL_NUMBER.text); } |
		//HEXADECIMAL_NUMBER { $constant = Integer.parseInt($HEXADECIMAL_NUMBER.text, 16); } |
		//OCTAL_NUMBER { $constant = Integer.parseInt($OCTAL_NUMBER.text, 8); } |
		//FLOAT_NUMBER { $constant = Float.parseFloat($FLOAT_NUMBER.text); } |
		CHARACTER { $constant = $CHARACTER.text.substring(1, $CHARACTER.text.length() - 2); } |
		STRING { $constant = $STRING.text.substring(1, $STRING.text.length() - 2); }
	;
	
	
fragment
Letter
	:	'$' |
		'_' |
		'A'..'Z' |
		'a'..'z'
	;

IDENTIFIER
	:	(
			Letter |
			'0'..'9'
		)*
	;

fragment
FloatingPointExponentSuffix
	:	('e' | 'E')
		('+' | '-')?
		('0'..'9')+
	;

fragment
FloatingPointConstantSuffix
	:	'f' |
		'F' |
		'd' |
		'D'
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

fragment
UnicodeEscape
	:	'\\'
		'u'
		HexDigit HexDigit HexDigit HexDigit
	;


CHARACTER
	:	'\'' 
		(
			CharEscape |
			~(
				'\\' |
				'\''
			)
		)+
		'\''
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


fragment
HexDigit
	:	'0'..'9' |
		'a'..'f' |
		'A'..'F'
	;

fragment
IntegerConstantSuffix
	:	('u' | 'U') |
		(
			('l' | 'L')
			('l' | 'L')?
		)
	;

WS
	:	(
			' ' |
			'\r' |
			'\t' |
			'\u000C' |
			'\n'
		) {
			$channel=HIDDEN;
		}
    ;

COMMENT
	:	(
			'/*' 
			( options {greedy=false;} : . )* 
			'*/'
		)  { 
			$channel=HIDDEN; 
		}
	;

LINE_COMMENT
	:	(
			'//'
			~('\n'|'\r')*
			'\r'?
			'\n'
		) { 
			$channel=HIDDEN;
		}
    ;

