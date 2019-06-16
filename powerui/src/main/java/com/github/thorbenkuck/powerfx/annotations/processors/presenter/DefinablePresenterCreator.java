package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.DefinablePresenter;
import com.github.thorbenkuck.powerfx.annotations.Construct;
import com.github.thorbenkuck.powerfx.annotations.Destroy;
import com.github.thorbenkuck.powerfx.annotations.Displayed;
import com.github.thorbenkuck.powerfx.annotations.InjectView;
import com.squareup.javapoet.*;

import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.List;

public class DefinablePresenterCreator {

	private final TypeElement annotatedClass;
	private final List<ExecutableElement> methods = new ArrayList<>();
	private ExecutableElement construct;
	private ExecutableElement displayed;
	private ExecutableElement injectView;
	private ExecutableElement destroy;
	private boolean samePackageNeeded = false;
	private String name;


	private DefinablePresenterCreator(TypeElement annotatedClass) {
		this.annotatedClass = annotatedClass;
	}

	public static DefinablePresenterCreator create(TypeElement typeElement) {
		DefinablePresenterCreator presenterCreator = new DefinablePresenterCreator(typeElement);
		presenterCreator.analyze();
		presenterCreator.processMethods();

		return presenterCreator;
	}

	private void processMethods() {
		for (ExecutableElement executableElement : methods) {
			if (executableElement.getAnnotation(Displayed.class) != null) {
				if (executableElement.getParameters().isEmpty()) {
					displayed = executableElement;
					verifyPackageVisibility(executableElement);
				} else {
					// TODO Handle else
				}
			}

			if (executableElement.getAnnotation(Construct.class) != null) {
				if (executableElement.getParameters().isEmpty()) {
					construct = executableElement;
					verifyPackageVisibility(executableElement);
				} else {
					// TODO Handle else
				}
			}

			if (executableElement.getAnnotation(InjectView.class) != null) {
				if (executableElement.getParameters().isEmpty()) {
					// TODO Handle
				} else if (executableElement.getParameters().size() == 1) {
					injectView = executableElement;
					verifyPackageVisibility(executableElement);
				} else {
					// TODO Handle
				}
			}

			if (executableElement.getAnnotation(Destroy.class) != null) {
				if (executableElement.getParameters().isEmpty()) {
					destroy = executableElement;
					verifyPackageVisibility(executableElement);
				} else {
					// TODO Handle else
				}
			}
		}

		if (injectView == null) {
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
				if(executableElement.getKind() == ElementKind.CONSTRUCTOR) {
					if(executableElement.getParameters().isEmpty()) {
						verifyPackageVisibility(executableElement);
					}
				}
			}
		}

		name = "Definable" + annotatedClass.getSimpleName();
	}

	private void verifyPackageVisibility(ExecutableElement executableElement) {
		if(executableElement.getModifiers().contains(Modifier.PRIVATE)) {
			// TODO Handle method not accessible
		} else if(!executableElement.getModifiers().contains(Modifier.PUBLIC)) {
			samePackageNeeded = true;
		}
	}

	public TypeSpec create() {
		TypeSpec.Builder builder = TypeSpec.classBuilder(getName())
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(DefinablePresenter.class), TypeName.get(annotatedClass.asType())));

		builder.addField(FieldSpec.builder(TypeName.get(annotatedClass.asType()), "presenter")
				.addModifiers(Modifier.FINAL, Modifier.PRIVATE)
				.build());

		builder.addInitializerBlock(CodeBlock.builder()
				.add("presenter = new $T()", ClassName.get(annotatedClass))
				.build());

		if(construct != null) {
			delegateMethodCall(construct, "construct", builder);
		}

		if(destroy != null) {
			delegateMethodCall(destroy, "destroy", builder);
		}

		if(displayed != null) {
			delegateMethodCall(displayed, "displayed", builder);
		}

		builder.addMethod(MethodSpec.methodBuilder("injectView")
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addParameter(ParameterSpec.builder(TypeName.OBJECT, "view").build())
				.addCode(CodeBlock.builder()
						.add("presenter.$L(view)", injectView.getSimpleName())
						.build())
				.build());

		builder.addMethod(MethodSpec.methodBuilder("define")
				.returns(TypeName.get(annotatedClass.asType()))
				.addCode(CodeBlock.builder().add("return presenter").build())
				.addAnnotation(Override.class)
				.build());

		return builder.build();
	}

	private void delegateMethodCall(ExecutableElement realMethod, String delegateName, TypeSpec.Builder builder) {
		builder.addMethod(MethodSpec.methodBuilder(delegateName)
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addCode(CodeBlock.builder()
						.add("presenter.$L()", realMethod.getSimpleName())
						.build())
				.build());
	}

	public boolean isSamePackageNeeded() {
		return samePackageNeeded;
	}

	public String getName() {
		return name;
	}
}
