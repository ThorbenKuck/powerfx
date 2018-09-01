package com.github.thorbenkuck.powerfx.annotations;

import com.github.thorbenkuck.powerfx.View;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface PresenterImplementation {

	Class<? extends View> value();

}
