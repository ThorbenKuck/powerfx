package com.github.thorbenkuck.powerfx.annotations.processors;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

public class MethodCallDelegate {

	public static MethodSpec delegateMethodCall(ExecutableElement realMethod, String delegateName, String instanceName) {
		return MethodSpec.methodBuilder(delegateName)
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addCode(CodeBlock.builder()
						.addStatement("$L.$L()", instanceName, realMethod.getSimpleName())
						.build())
				.build();
	}

}
