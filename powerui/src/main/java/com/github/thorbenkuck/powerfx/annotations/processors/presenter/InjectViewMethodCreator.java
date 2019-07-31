package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.annotations.processors.MethodCreator;
import com.squareup.javapoet.*;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public class InjectViewMethodCreator implements MethodCreator {

	private VariableElement variableElement;
	private ExecutableElement injectView;

	@Override
	public MethodSpec create() {
		return MethodSpec.methodBuilder("injectView")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addParameter(ParameterSpec.builder(TypeName.OBJECT, "view").build())
				.addCode(CodeBlock.builder()
						.beginControlFlow("if(!($L instanceof $T))", "view", ClassName.get(variableElement.asType()))
						.addStatement("throw new $T($S + $L.getClass() + $S)", IllegalArgumentException.class, "The type of the Object(", "view", ") is not related to " + ClassName.get(variableElement.asType()))
						.endControlFlow()
						.addStatement("presenter.$L(($T) view)", injectView.getSimpleName(), ClassName.get(variableElement.asType()))
						.build())
				.build();
	}

	@Override
	public boolean willCreate() {
		return variableElement != null && injectView != null;
	}

	public void setVariableElement(VariableElement variableElement) {
		this.variableElement = variableElement;
	}

	public void setInjectView(ExecutableElement injectView) {
		this.injectView = injectView;
	}
}
