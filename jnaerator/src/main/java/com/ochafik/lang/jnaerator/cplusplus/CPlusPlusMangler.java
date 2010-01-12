package com.ochafik.lang.jnaerator.cplusplus;

import com.ochafik.lang.jnaerator.Result;
import com.ochafik.lang.jnaerator.parser.Function;

public interface CPlusPlusMangler {
	String mangle(Function method, Result context);
}
