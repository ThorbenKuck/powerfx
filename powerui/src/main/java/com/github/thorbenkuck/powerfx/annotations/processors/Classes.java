package com.github.thorbenkuck.powerfx.annotations.processors;

import com.github.thorbenkuck.powerfx.PresenterFactory;
import com.github.thorbenkuck.powerfx.ViewFactory;
import com.github.thorbenkuck.powerfx.annotations.PresenterImplementation;
import com.github.thorbenkuck.powerfx.annotations.PreventAutoLoad;
import com.github.thorbenkuck.powerfx.annotations.ViewImplementation;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;

class Classes {

	private final Elements elements;
	private final Types types;

	Classes(Elements elements, Types types) {
		this.elements = elements;
		this.types = types;
	}

	private void generate(Container container, TypeElement typeElement, TypeSpec.Builder builder, String factoryName, TypeName viewIdentifier, Filer filer) throws ProcessingException {
		PipelineElementConstructor.apply(builder, typeElement, container);

		if (typeElement.getAnnotation(PreventAutoLoad.class) == null) {
			AutoLoadProvider.applyAutoLoad(builder, viewIdentifier, factoryName);
		}

		Element packaged = typeElement.getEnclosingElement();

		if (packaged.getKind() != ElementKind.PACKAGE) {
			throw new ProcessingException("Could not locate the package", packaged);
		}

		PackageElement packageElement = (PackageElement) packaged;

		try {
			JavaFile.builder(packageElement.getQualifiedName().toString(), builder.build())
					.addFileComment("This file has been auto generated")
					.indent("    ")
					.build()
					.writeTo(filer);
		} catch (IOException e) {
			throw new ProcessingException("Could not generate the Factory " + factoryName, typeElement);
		}
	}

	private void generatePresenterFactory(PresenterContainer container, TypeMirror viewValue, TypeMirror presenterValue, Filer filer) throws ProcessingException {
		TypeElement presenterElement = container.getElement();
		String factoryName = presenterElement.getSimpleName() + "Factory";

		MethodSpec createsMethod = MethodSpec.methodBuilder("create")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addAnnotation(Override.class)
				.returns(TypeName.get(presenterValue))
				.addStatement("return new $T()", TypeName.get(presenterElement.asType()))
				.build();


		TypeSpec.Builder builder = TypeSpec.classBuilder(factoryName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(PresenterFactory.class),
						ClassName.get(viewValue), ClassName.get(container.getRepresentedInterface())))
				.addMethod(createsMethod);

		generate(container, presenterElement, builder, factoryName, TypeName.get(viewValue), filer);
	}

	private void generateViewFactory(ViewContainer container, TypeMirror parameter, TypeMirror returnValue, Filer filer) throws ProcessingException {
		TypeElement viewElement = container.getElement();
		String factoryName = viewElement.getSimpleName() + "Factory";

		MethodSpec createsMethod = MethodSpec.methodBuilder("create")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addAnnotation(Override.class)
				.returns(TypeName.get(returnValue))
				.addParameter(TypeName.get(parameter), "presenter")
				.addStatement("return new $T(presenter)", TypeName.get(viewElement.asType()))
				.build();

		TypeSpec.Builder builder = TypeSpec.classBuilder(factoryName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(ViewFactory.class),
						ClassName.get(container.getRepresentedInterface()), ClassName.get(parameter)))
				.addMethod(createsMethod);

		generate(container, viewElement, builder, factoryName, TypeName.get(container.getRepresentedInterface()), filer);
	}

	public void generate(ViewContainer viewContainer, Filer filer) throws ProcessingException {
		ViewImplementation requestedPresenterType = viewContainer.getElement().getAnnotation(ViewImplementation.class);

		generateViewFactory(viewContainer, FactoryProcessor.getTypeMirror(requestedPresenterType), viewContainer.getRepresentedInterface(), filer);
	}

	public void generate(PresenterContainer presenterContainer, Filer filer) throws ProcessingException {
		PresenterImplementation requestedViewType = presenterContainer.getElement().getAnnotation(PresenterImplementation.class);

		generatePresenterFactory(presenterContainer, FactoryProcessor.getTypeMirror(requestedViewType), presenterContainer.getRepresentedInterface(), filer);

	}

}
