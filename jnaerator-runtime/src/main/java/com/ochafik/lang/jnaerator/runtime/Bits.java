package com.ochafik.lang.jnaerator.runtime;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD} )
public @interface Bits {
	int value();
}
