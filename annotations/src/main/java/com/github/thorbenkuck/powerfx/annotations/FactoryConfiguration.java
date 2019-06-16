package com.github.thorbenkuck.powerfx.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface FactoryConfiguration {

	boolean DEFAULT_AUTO_LOAD = true;

	boolean DEFAULT_LAZY = true;

	String DEFAULT_NAME = "null";

	String name() default DEFAULT_NAME;

	boolean autoLoad() default DEFAULT_AUTO_LOAD;

	boolean lazy() default DEFAULT_LAZY;

}
