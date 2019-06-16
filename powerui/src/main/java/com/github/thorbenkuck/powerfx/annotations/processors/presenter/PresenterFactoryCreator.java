package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.DefinablePresenter;
import com.github.thorbenkuck.powerfx.PresenterFactory;
import com.github.thorbenkuck.powerfx.annotations.FactoryConfiguration;
import com.github.thorbenkuck.powerfx.annotations.Presenter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PresenterFactoryCreator {

	private final TypeElement annotatedClass;
	private final Elements elements;
	private final List<TypeElement> identifier = new ArrayList<>();
	private boolean lazy;
	private String name;
	private boolean autoLoad;

	private PresenterFactoryCreator(TypeElement annotatedClass, Elements elements) {
		this.annotatedClass = annotatedClass;
		this.elements = elements;
	}

	public static PresenterFactoryCreator create(TypeElement typeElement, Elements elements) {
		PresenterFactoryCreator creator = new PresenterFactoryCreator(typeElement, elements);
		creator.analyzeAttributes();
		creator.analyze();

		return creator;
	}

	private void analyzeAttributes() {
		FactoryConfiguration annotation = annotatedClass.getAnnotation(FactoryConfiguration.class);

		if (annotation != null) {
			autoLoad = annotation.autoLoad();
			lazy = annotation.lazy();
			String givenName = annotation.name();

			if(givenName.equals(FactoryConfiguration.DEFAULT_NAME)) {
				name = constructName();
			} else {
				name = givenName;
			}
		} else {
			autoLoad = FactoryConfiguration.DEFAULT_AUTO_LOAD;
			lazy = FactoryConfiguration.DEFAULT_LAZY;
			name = constructName();
		}
	}

	private void analyze() {
		Presenter annotation = annotatedClass.getAnnotation(Presenter.class);
		if(annotation.identifiedBy().length == 0) {
			identifier.add(annotatedClass);
		} else {
			Stream.of(annotation.identifiedBy()).forEach(c -> identifier.add(elements.getTypeElement(c.getName())));
		}
	}

	private String constructName() {
		String name = annotatedClass.getSimpleName().toString();
		if(!name.endsWith("Presenter")) {
			name = name + "DefinablePresenter";
		}

		return name + "Factory";
	}

	public JavaFile create(DefinablePresenterCreator creator) {
		TypeSpec.Builder builder = TypeSpec.classBuilder(name)
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(PresenterFactory.class), TypeName.get(annotatedClass.asType())));

		builder.addType(creator.create());

		builder.addMethod(MethodSpec.methodBuilder("create")
				.returns(ParameterizedTypeName.get(ClassName.get(DefinablePresenter.class), TypeName.get(annotatedClass.asType())))
				.addAnnotation(Override.class)
				.addStatement(CodeBlock.builder()
						.add("return new $L()", creator.getName())
						.build())
				.build());

		if(autoLoad) {
			builder.addAnnotation(AnnotationSpec.builder(AutoService.class)
					.addMember("value", "$T.class", PresenterFactory.class)
					.build());
		}

		builder.addMethod(MethodSpec.methodBuilder("isLazy")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.returns(TypeName.BOOLEAN)
				.addAnnotation(Override.class)
				.addCode("return $L", lazy)
				.build());

		String packageName;

		if(creator.isSamePackageNeeded()) {
			packageName = ((PackageElement) annotatedClass.getEnclosingElement()).getQualifiedName().toString();
		} else {
			packageName = "de.thorbenkuck.powerfx.generated.presenter.factories";
		}

		return JavaFile.builder(packageName, builder.build())
				.addFileComment("This file has been auto generated")
				.indent("    ")
				.build();
	}
}
