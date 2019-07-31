package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.DefinablePresenter;
import com.github.thorbenkuck.powerfx.annotations.Construct;
import com.github.thorbenkuck.powerfx.annotations.Destroy;
import com.github.thorbenkuck.powerfx.annotations.Displayed;
import com.github.thorbenkuck.powerfx.annotations.InjectView;
import com.squareup.javapoet.*;

import javax.annotation.Generated;
import javax.lang.model.element.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DefinablePresenterCreator {

	private final TypeElement annotatedClass;
	private final List<ExecutableElement> methods = new ArrayList<>();
	private final ConstructMethodCreator constructMethodCreator = new ConstructMethodCreator();
	private final DisplayedMethodCreator displayedMethodCreator = new DisplayedMethodCreator();
	private final InjectViewMethodCreator injectViewMethodCreator = new InjectViewMethodCreator();
	private final DestroyMethodCreator destroyMethodCreator = new DestroyMethodCreator();
	private final InjectMethodCreator injectMethodCreator = new InjectMethodCreator();
	private final DefineMethodCreator defineMethodCreator;
	private boolean samePackageNeeded = false;
	private String name;


	private DefinablePresenterCreator(TypeElement annotatedClass) {
		this.annotatedClass = annotatedClass;
		defineMethodCreator = new DefineMethodCreator(annotatedClass);
	}

	public static DefinablePresenterCreator create(TypeElement typeElement) {
		DefinablePresenterCreator presenterCreator = new DefinablePresenterCreator(typeElement);
		presenterCreator.analyze();
		presenterCreator.processMethods();

		return presenterCreator;
	}

	private void processMethods() {
		for (ExecutableElement executableElement : methods) {
			injectMethodCreator.addExecutableElement(executableElement);
			if (executableElement.getAnnotation(Displayed.class) != null) {
				if (executableElement.getParameters().isEmpty()) {
					displayedMethodCreator.setDisplay(executableElement);
					verifyPackageVisibility(executableElement);
				} else {
					// TODO Handle else
				}
			}

			if (executableElement.getAnnotation(Construct.class) != null) {
				if (executableElement.getParameters().isEmpty()) {
					constructMethodCreator.setConstruct(executableElement);
					verifyPackageVisibility(executableElement);
				} else {
					// TODO Handle else
				}
			}

			if (executableElement.getAnnotation(InjectView.class) != null) {
				if (executableElement.getParameters().isEmpty()) {
					// TODO Handle
				} else if (executableElement.getParameters().size() == 1) {
					injectViewMethodCreator.setInjectView(executableElement);
					injectViewMethodCreator.setVariableElement(executableElement.getParameters().get(0));
					verifyPackageVisibility(executableElement);
				} else {
					// TODO Handle
				}
			}

			if (executableElement.getAnnotation(Destroy.class) != null) {
				if (executableElement.getParameters().isEmpty()) {
					destroyMethodCreator.setDestroy(executableElement);
					verifyPackageVisibility(executableElement);
				} else {
					// TODO Handle else
				}
			}
		}

		if (!injectViewMethodCreator.willCreate()) {
			// TODO Handle inject view annotation missing
		}
	}

	private void analyze() {
		for (Element enclosedElement : annotatedClass.getEnclosedElements()) {
			if (enclosedElement instanceof ExecutableElement) {
				ExecutableElement executableElement = (ExecutableElement) enclosedElement;
				if (executableElement.getKind() == ElementKind.METHOD) {
					methods.add(executableElement);
				}
				if (executableElement.getKind() == ElementKind.CONSTRUCTOR) {
					if (executableElement.getParameters().isEmpty()) {
						verifyPackageVisibility(executableElement);
					}
				}
			}
		}

		name = "Definable" + annotatedClass.getSimpleName();
	}

	private void verifyPackageVisibility(ExecutableElement executableElement) {
		if (executableElement.getModifiers().contains(Modifier.PRIVATE)) {
			// TODO Show warning and generate Reflection-Code
		} else if (!executableElement.getModifiers().contains(Modifier.PUBLIC)) {
			samePackageNeeded = true;
		}
	}

	public TypeSpec create() {
		TypeSpec.Builder builder = TypeSpec.classBuilder(getName())
				.addModifiers(Modifier.PRIVATE)
				.addAnnotation(AnnotationSpec.builder(Generated.class)
						.addMember("value", "$S", PresenterFactoryCreator.class.getName())
						.addMember("date", "$S", LocalDateTime.now().toString())
						.build())
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(DefinablePresenter.class), TypeName.get(annotatedClass.asType())))
				.addField(FieldSpec.builder(TypeName.get(annotatedClass.asType()), "presenter")
						.addModifiers(Modifier.FINAL, Modifier.PRIVATE)
						.build())
				.addInitializerBlock(CodeBlock.builder()
						.addStatement("presenter = new $T()", ClassName.get(annotatedClass))
						.build());

		constructMethodCreator.apply(builder);
		destroyMethodCreator.apply(builder);
		displayedMethodCreator.apply(builder);
		injectViewMethodCreator.apply(builder);
		injectMethodCreator.apply(builder);
		defineMethodCreator.apply(builder);

		return builder.build();
	}

	public boolean isSamePackageNeeded() {
		return samePackageNeeded;
	}

	public String getName() {
		return name;
	}
}
