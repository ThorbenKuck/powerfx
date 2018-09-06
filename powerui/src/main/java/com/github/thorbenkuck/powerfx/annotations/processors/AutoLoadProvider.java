package com.github.thorbenkuck.powerfx.annotations.processors;

import com.github.thorbenkuck.powerfx.SuperControllerMapping;
import com.github.thorbenkuck.powerfx.annotations.AutoLoad;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class AutoLoadProvider {

	public static void applyAutoLoad(TypeSpec.Builder builder, TypeName viewValue, String factoryName) {
		builder.addAnnotation(AutoLoad.class)
				.addStaticBlock(CodeBlock.builder().addStatement("$T.register($T.class, new $L())", SuperControllerMapping.class, viewValue, factoryName).build());
	}

}
