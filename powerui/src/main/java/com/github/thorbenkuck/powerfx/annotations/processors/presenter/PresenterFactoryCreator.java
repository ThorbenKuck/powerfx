package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.DefinablePresenter;
import com.github.thorbenkuck.powerfx.PresenterFactory;
import com.github.thorbenkuck.powerfx.annotations.FactoryConfiguration;
import com.github.thorbenkuck.powerfx.annotations.Presenter;
import com.github.thorbenkuck.powerfx.annotations.processors.AnnotationAnalyzer;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PresenterFactoryCreator {

	private final TypeElement annotatedClass;
	private final Elements elements;
	private final Types types;
	private final List<TypeElement> identifier = new ArrayList<>();
	private boolean lazy;
	private String name;
	private boolean autoLoad;

	private PresenterFactoryCreator(TypeElement annotatedClass, Elements elements, Types types) {
		this.annotatedClass = annotatedClass;
		this.elements = elements;
		this.types = types;
	}

	public static PresenterFactoryCreator create(TypeElement typeElement, Elements elements, Types types) {
		PresenterFactoryCreator creator = new PresenterFactoryCreator(typeElement, elements, types);
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
		System.out.println("Finding identifiedBy for @Presenter annotation");
		List<? extends TypeMirror> typeMirrors = AnnotationAnalyzer.fetchTypeElements(annotation, Presenter::identifiedBy);
		if (typeMirrors.isEmpty()) {
			System.out.println("No identifier specified");
			identifier.add(annotatedClass);
		} else {
			System.out.println("Found multiple identifier");
			typeMirrors.stream()
					.map(types::asElement)
					.map(t -> (TypeElement) t)
					.forEach(identifier::add);
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
				.addAnnotation(AnnotationSpec.builder(Generated.class)
						.addMember("value", "$S", PresenterFactoryCreator.class.getName())
						.addMember("date", "$S", LocalDateTime.now().toString())
						.build())
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(PresenterFactory.class), TypeName.get(annotatedClass.asType())));

		builder.addType(creator.create());

		builder.addMethod(MethodSpec.methodBuilder("create")
				.returns(ParameterizedTypeName.get(ClassName.get(DefinablePresenter.class), TypeName.get(annotatedClass.asType())))
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addCode(CodeBlock.builder()
						.addStatement("return new $L()", creator.getName())
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
				.addCode(CodeBlock.builder().addStatement("return $L", lazy).build())
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
