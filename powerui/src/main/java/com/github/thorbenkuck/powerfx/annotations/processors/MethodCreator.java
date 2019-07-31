package com.github.thorbenkuck.powerfx.annotations.processors;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public interface MethodCreator {

	default void apply(TypeSpec.Builder typeSpecBuilder) {
		if (willCreate()) {
			typeSpecBuilder.addMethod(create());
		}
	}

	MethodSpec create();

	boolean willCreate();
}
