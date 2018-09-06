package com.github.thorbenkuck.powerfx.annotations.processors;

import com.github.thorbenkuck.powerfx.SuperController;
import com.github.thorbenkuck.powerfx.annotations.InjectPresenter;
import com.github.thorbenkuck.powerfx.annotations.PreventCache;
import com.github.thorbenkuck.powerfx.exceptions.NotCachedException;
import com.github.thorbenkuck.powerfx.pipe.PipelineElement;
import com.squareup.javapoet.*;

import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipelineElementConstructor {

	private static void checkMethod(ExecutableElement method, TypeSpec.Builder builder, Container container, List<String> createList) {
		if (method.getAnnotation(InjectPresenter.class) != null) {
			String name = "InjectPresenterPipelineElement" + (createList.size() == 0 ? "" : createList.size());
			TypeSpec innerClass = TypeSpec.classBuilder(name)
					.addModifiers(Modifier.PRIVATE, Modifier.STATIC)
					.addSuperinterface(ParameterizedTypeName.get(ClassName.get(PipelineElement.class),
							ClassName.get(container.getRepresentedInterface())))
					.addMethod(MethodSpec.methodBuilder("apply")
							.addParameter(TypeName.get(container.getRepresentedInterface()), "t", Modifier.FINAL)
							.addParameter(SuperController.class, "superController", Modifier.FINAL)
							.addAnnotation(Override.class)
							.returns(TypeName.get(container.getRepresentedInterface()))
							.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
							.addCode(CodeBlock.builder()
									.beginControlFlow("try")
									.addStatement("(($T) t).$L(superController.getCache().getCachedPresenter($T.class))", TypeName.get(container.getElement().asType()), method.getSimpleName(), TypeName.get(method.getParameters().get(0).asType()))
									.addStatement("return t")
									.endControlFlow()
									.beginControlFlow("catch($T e)", NotCachedException.class)
									.addStatement("throw new $T(e)", IllegalStateException.class)
									.endControlFlow()
									.build())
							.build())
					.build();
			builder.addType(innerClass);
			createList.add("new " + name + "()");
		}
	}

	private static void applyCache(TypeSpec.Builder builder, Container container, List<String> createList) {
		if (container.getElement().getAnnotation(PreventCache.class) == null) {
			String name = "CachePresenterPipelineElement";
			TypeSpec innerClass = TypeSpec.classBuilder(name)
					.addModifiers(Modifier.PRIVATE, Modifier.STATIC)
					.addSuperinterface(ParameterizedTypeName.get(ClassName.get(PipelineElement.class),
							ClassName.get(container.getRepresentedInterface())))
					.addMethod(MethodSpec.methodBuilder("apply")
							.addParameter(TypeName.get(container.getRepresentedInterface()), "t", Modifier.FINAL)
							.addParameter(SuperController.class, "superController", Modifier.FINAL)
							.addAnnotation(Override.class)
							.returns(TypeName.get(container.getRepresentedInterface()))
							.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
							.addCode(CodeBlock.builder()
									.addStatement("superController.getCache().cache($T.class, t)", TypeName.get(container.getRepresentedInterface()))
									.addStatement("return t")
									.build())
							.build())
					.build();
			builder.addType(innerClass);
			createList.add("new " + name + "()");
		}
	}

	public static void apply(TypeSpec.Builder builder, TypeElement typeElement, Container container) {
		List<String> strings = new ArrayList<>();
		for (Element element : typeElement.getEnclosedElements()) {
			if (element.getKind() == ElementKind.METHOD) {
				ExecutableElement method = (ExecutableElement) element;
				checkMethod(method, builder, container, strings);
			}
		}
		applyCache(builder, container, strings);

		if (strings.isEmpty()) {
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < strings.size() - 2; i++) {
			stringBuilder.append(strings.get(i)).append(",");
		}

		stringBuilder.append(strings.get(strings.size() - 1));

		builder.addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class),
				ParameterizedTypeName.get(ClassName.get(PipelineElement.class),
						ClassName.get(container.getRepresentedInterface()))), "elements", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("$T.asList($L)", Arrays.class, stringBuilder.toString())
				.build());

		builder.addMethod(MethodSpec.methodBuilder("getModifiers")
				.returns(ParameterizedTypeName.get(ClassName.get(List.class),
						ParameterizedTypeName.get(ClassName.get(PipelineElement.class),
								ClassName.get(container.getRepresentedInterface()))))
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addAnnotation(Override.class)
				.addStatement("return elements")
				.build());
	}

}
