package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.annotations.processors.MethodCreator;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class DefineMethodCreator implements MethodCreator {

	private final TypeElement annotatedClass;

	public DefineMethodCreator(TypeElement annotatedClass) {
		this.annotatedClass = annotatedClass;
	}

	@Override
	public MethodSpec create() {
		return MethodSpec.methodBuilder("define")
				.returns(TypeName.get(annotatedClass.asType()))
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addCode(CodeBlock.builder().addStatement("return presenter").build())
				.addAnnotation(Override.class)
				.build();
	}

	@Override
	public boolean willCreate() {
		return true;
	}
}
