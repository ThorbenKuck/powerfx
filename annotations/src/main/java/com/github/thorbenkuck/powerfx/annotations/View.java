package com.github.thorbenkuck.powerfx.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface View {

	Class<?>[] identifiedBy() default {};

}
