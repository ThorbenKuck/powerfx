package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.di.ReflectionsHelper;
import com.github.thorbenkuck.di.WiredTypes;
import com.github.thorbenkuck.powerfx.annotations.processors.MethodCreator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.List;

public class InjectMethodCreator implements MethodCreator {

	private final List<Element> toInjectInto = new ArrayList<>();
	private int count;

	private String nextName() {
		return "t" + count++;
	}

	private CodeBlock generateMethodInjection(String instanceName, ExecutableElement executableElement) {
		CodeBlock.Builder builder = CodeBlock.builder();
		List<String> variableNames = new ArrayList<>();

		for (VariableElement parameter : executableElement.getParameters()) {
			String name = nextName();
			variableNames.add(name);
			builder.addStatement("$T $L = wiredTypes.getInstance($T.class)", ClassName.get(parameter.asType()), name, ClassName.get(parameter.asType()));

			if (parameter.getAnnotation(Nullable.class) == null) {
				builder.beginControlFlow("if($L == null)", name)
						.addStatement("throw new $T($S)", IllegalStateException.class, "Could not find any instance of " + ClassName.get(parameter.asType()) + " to inject")
						.endControlFlow();
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (variableNames.size() >= 1) {
			stringBuilder.append(variableNames.get(0));

			for (int i = 1; i < variableNames.size(); i++) {
				stringBuilder.append(", ").append(variableNames.get(i));
			}
		}

		if (executableElement.getModifiers().contains(Modifier.PRIVATE)) {
			builder.addStatement("$T.invokeMethod($L, $L, $L)", ReflectionsHelper.class, instanceName, executableElement.getSimpleName().toString(), stringBuilder.toString());
		} else {
			builder.addStatement("$L.$L($L)", instanceName, executableElement.getSimpleName().toString(), stringBuilder.toString());
		}

		return builder.build();
	}

	private CodeBlock generateFieldInjection(String instanceName, VariableElement field) {
		CodeBlock.Builder builder = CodeBlock.builder();
		String fieldName = field.getSimpleName().toString();
		if (field.getModifiers().contains(Modifier.PRIVATE)) {
			builder.addStatement("$T.setField($L, $L, wiredTypes.getInstance($T))", ReflectionsHelper.class, fieldName, instanceName, ClassName.get(field.asType()));
		} else {
			builder.addStatement("$L.$L = wiredTypes.getInstance($T.class)", instanceName, fieldName, ClassName.get(field.asType()));
		}

		if (field.getAnnotation(Nullable.class) == null) {
			builder.beginControlFlow("if($L.$L == null)", instanceName, fieldName)
					.addStatement("throw new $T($S)", IllegalStateException.class, "Could not wire the field " + field.getSimpleName().toString() + ". No instance wired for the type " + ClassName.get(field.asType()))
					.endControlFlow();
		}

		return builder.build();
	}

	public void addExecutableElement(Element element) {
		if (element.getAnnotation(Inject.class) != null) {
			toInjectInto.add(element);
		}
	}

	@Override
	public MethodSpec create() {
		MethodSpec.Builder builder = MethodSpec.methodBuilder("inject")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addAnnotation(Override.class)
				.addParameter(TypeName.get(WiredTypes.class), "wiredTypes");

		for (Element element : toInjectInto) {
			if (element.getKind() == ElementKind.METHOD) {
				builder.addCode(generateMethodInjection("presenter", (ExecutableElement) element));
			} else if (element.getKind() == ElementKind.FIELD) {
				VariableElement field = (VariableElement) element;
				builder.addCode(generateFieldInjection("presenter", field));
			}
		}

		return builder.build();
	}

	@Override
	public boolean willCreate() {
		System.out.println("Will create: " + toInjectInto.isEmpty());
		return !toInjectInto.isEmpty();
	}
}
