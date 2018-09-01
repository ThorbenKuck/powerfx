package com.github.thorbenkuck.powerfx.annotations.processors;

import com.github.thorbenkuck.powerfx.PresenterFactory;
import com.github.thorbenkuck.powerfx.SuperControllerMapping;
import com.github.thorbenkuck.powerfx.ViewFactory;
import com.github.thorbenkuck.powerfx.annotations.PresenterFactoryDefinition;
import com.github.thorbenkuck.powerfx.annotations.PresenterImplementation;
import com.github.thorbenkuck.powerfx.annotations.ViewFactoryDefinition;
import com.github.thorbenkuck.powerfx.annotations.ViewImplementation;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;

public class Classes {

	private final Elements elements;
	private final Types types;

	String s = "package blablabla;" +
			"" +
			"static {" +
			"    SuperControllerMapping.register(Bla.class, new BlaViewFactory(), new FooPresenterFactory());" +
			"}" +
			"" +
			"public class BlaViewFactory implements ViewFactory<...> {" +
			"    public Bla create(Foo presenter) {" +
			"        return new BlaImpl(presenter);" +
			"    }" +
			"}";

	public Classes(Elements elements, Types types) {
		this.elements = elements;
		this.types = types;
	}

	public void generatePresenterFactory(PresenterContainer container, TypeMirror viewValue, TypeMirror presenterValue, Filer filer) throws ProcessingException {
		TypeMirror presenterFactoryMirror = elements.getTypeElement(PresenterFactory.class.getName()).asType();
		String factoryName = container.getElement().getSimpleName() + "Factory";

		try {
			MethodSpec createsMethod = MethodSpec.methodBuilder("create")
					.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
					.returns(TypeName.get(presenterValue))
					.addStatement("return new $T()", TypeName.get(container.getElement().asType()))
					.build();

			TypeSpec helloWorld = TypeSpec.classBuilder(factoryName)
					.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
					.addSuperinterface(ParameterizedTypeName.get(ClassName.get(PresenterFactory.class),
							ClassName.get(viewValue)))
					.addAnnotation(PresenterFactoryDefinition.class)
					.addMethod(createsMethod)
					.addStaticBlock(CodeBlock.builder().addStatement("$T.register($T.class, new $L())", SuperControllerMapping.class, TypeName.get(viewValue), factoryName).build())
					.build();

			Element packaged = container.getElement().getEnclosingElement();

			if(packaged.getKind() != ElementKind.PACKAGE) {
				throw new ProcessingException("Could not locate the package", packaged);
			}

			PackageElement packageElement = (PackageElement) packaged;

			JavaFile.builder(packageElement.getQualifiedName().toString(), helloWorld)
					.addFileComment("This file has been auto generated")
					.build()
					.writeTo(filer);

		} catch (IOException e) {
			throw new ProcessingException("Could not create the new Factory while trying to " + e.getClass() + e.getMessage(), container.getElement());
		}
	}

	public void generateViewFactory(ViewContainer container, TypeMirror parameter, TypeMirror returnValue, Filer filer) throws ProcessingException {
		try {
			String factoryName = container.getElement().getSimpleName() + "Factory";

			MethodSpec createsMethod = MethodSpec.methodBuilder("create")
					.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
					.returns(TypeName.get(returnValue))
					.addParameter(TypeName.get(parameter), "presenter")
					.addStatement("return new $T(presenter)", TypeName.get(container.getElement().asType()))
					.build();

			TypeSpec helloWorld = TypeSpec.classBuilder(factoryName)
					.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
					.addAnnotation(ViewFactoryDefinition.class)
					.addSuperinterface(ParameterizedTypeName.get(ClassName.get(ViewFactory.class),
							ClassName.get(container.getRepresentedInterface()), ClassName.get(parameter)))
					.addStaticBlock(CodeBlock.builder().addStatement("$T.register($T.class, new $L())", SuperControllerMapping.class, TypeName.get(container.getRepresentedInterface()), factoryName).build())
					.addMethod(createsMethod)
					.build();

			Element packaged = container.getElement().getEnclosingElement();

			if(packaged.getKind() != ElementKind.PACKAGE) {
				throw new ProcessingException("Could not locate the package", packaged);
			}

			PackageElement packageElement = (PackageElement) packaged;

			JavaFile.builder(packageElement.getQualifiedName().toString(), helloWorld)
					.addFileComment("This file has been auto generated")
					.build()
					.writeTo(filer);

		} catch (IOException e) {
			throw new ProcessingException("Could not create the new Factory while trying to " + e.getClass() + e.getMessage(), container.getElement());
		}
	}

	public void generate(ViewContainer viewContainer, PresenterContainer presenterContainer, Filer filer) throws ProcessingException {
		ViewImplementation requestedPresenterType = viewContainer.getElement().getAnnotation(ViewImplementation.class);
		PresenterImplementation requestedViewType = presenterContainer.getElement().getAnnotation(PresenterImplementation.class);

		generateViewFactory(viewContainer, FactoryProcessor.getTypeMirror(requestedPresenterType), FactoryProcessor.getTypeMirror(requestedViewType), filer);
		generatePresenterFactory(presenterContainer, FactoryProcessor.getTypeMirror(requestedViewType), FactoryProcessor.getTypeMirror(requestedPresenterType), filer);
	}

}
