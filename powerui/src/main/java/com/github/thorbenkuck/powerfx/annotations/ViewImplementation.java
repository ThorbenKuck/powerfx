package com.github.thorbenkuck.powerfx.annotations;

import com.github.thorbenkuck.powerfx.Presenter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface ViewImplementation {

	Class<? extends Presenter> value();

}
