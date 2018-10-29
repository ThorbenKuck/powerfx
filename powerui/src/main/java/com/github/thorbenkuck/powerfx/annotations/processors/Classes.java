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
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class Classes {

	private final Elements elements;
	private final Types types;
	private final List<String> fullyQualifiedViewNames = new ArrayList<>();
	private final List<String> fullyQualifiedPresenterNames = new ArrayList<>();
	private final LocalDateTime created = LocalDateTime.now();

	Classes(Elements elements, Types types) {
		this.elements = elements;
		this.types = types;
	}

	private String generate(Container container, TypeElement typeElement, TypeSpec.Builder builder, String factoryName, TypeName viewIdentifier, Filer filer) throws ProcessingException {
		PipelineElementConstructor.apply(builder, typeElement, container);

		boolean preventAutoLoad = true;

		if (typeElement.getAnnotation(PreventAutoLoad.class) == null) {
			AutoLoadProvider.applyAutoLoad(builder, viewIdentifier, factoryName);
			preventAutoLoad = false;
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

			return preventAutoLoad ? null : packageElement.getQualifiedName().toString() + "." + factoryName;
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

		MethodSpec identifyMethod = MethodSpec.methodBuilder("getIdentifier")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addAnnotation(Override.class)
				.returns(ParameterizedTypeName.get(ClassName.get(Class.class), ClassName.get(viewValue)))
				.addStatement("return $T.class", ClassName.get(viewValue))
				.build();

		TypeSpec.Builder builder = TypeSpec.classBuilder(factoryName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(PresenterFactory.class),
						ClassName.get(viewValue), ClassName.get(container.getRepresentedInterface())))
				.addMethod(createsMethod)
				// This would lock us into
				// using java >= 9.
//				.addAnnotation(AnnotationSpec.builder(Generated.class)
//						.addMember("value", "$S", "com.github.thorbenkuck.powerfx.annotations.processors.MVPProcessor")
//						.addMember("date", "$S", created)
//						.build())
				.addMethod(identifyMethod);

		String presenterName = generate(container, presenterElement, builder, factoryName, TypeName.get(viewValue), filer);
		fullyQualifiedPresenterNames.add(presenterName);
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

		MethodSpec identifyMethod = MethodSpec.methodBuilder("getIdentifier")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addAnnotation(Override.class)
				.returns(ParameterizedTypeName.get(ClassName.get(Class.class), ClassName.get(container.getRepresentedInterface())))
				.addStatement("return $T.class", ClassName.get(container.getRepresentedInterface()))
				.build();

		TypeSpec.Builder builder = TypeSpec.classBuilder(factoryName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(ViewFactory.class),
						ClassName.get(container.getRepresentedInterface()), ClassName.get(parameter)))
				.addMethod(createsMethod)
				// This would lock us into
				// using java >= 9.
//				.addAnnotation(AnnotationSpec.builder(Generated.class)
//						.addMember("value", "$S", "com.github.thorbenkuck.powerfx.annotations.processors.MVPProcessor")
//						.addMember("date", "$S", created)
//						.build())
				.addMethod(identifyMethod);

		String viewName = generate(container, viewElement, builder, factoryName, TypeName.get(container.getRepresentedInterface()), filer);
		fullyQualifiedViewNames.add(viewName);
	}

	public void generate(ViewContainer viewContainer, Filer filer) throws ProcessingException {
		ViewImplementation requestedPresenterType = viewContainer.getElement().getAnnotation(ViewImplementation.class);

		generateViewFactory(viewContainer, FactoryProcessor.getTypeMirror(requestedPresenterType), viewContainer.getRepresentedInterface(), filer);
	}

	public void generate(PresenterContainer presenterContainer, Filer filer) throws ProcessingException {
		PresenterImplementation requestedViewType = presenterContainer.getElement().getAnnotation(PresenterImplementation.class);

		generatePresenterFactory(presenterContainer, FactoryProcessor.getTypeMirror(requestedViewType), presenterContainer.getRepresentedInterface(), filer);

	}

	private void log(String s) {
		System.out.println(s);
	}

	private void insert(String resourceFile, Filer filer, List<String> content) throws ProcessingException {
		log("Trying to update " + resourceFile);

		content = content.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		Set<String> all = new HashSet<>();
		try {
			FileObject existingFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
			log("Looking for existing resource file at " + existingFile.toUri());
			Set<String> temp = ServiceFiles.readServiceFile(existingFile.openInputStream());
			all.addAll(temp);
		} catch (IOException e) {
			log("File did not exist beforehand");
		}

		log("Service file " + resourceFile + " contains " + all);

		if (all.containsAll(content)) {
			log("No new additions");
			return;
		}

		all.addAll(content);

		try {
			log("Starting to write " + all + " to the new files");
			FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "",
					resourceFile);
			log("Fetching output");
			OutputStream out = fileObject.openOutputStream();
			log("Writing ..");
			ServiceFiles.writeServiceFile(all, out);
			log("Done!");
			out.close();
		} catch (IOException e) {
			throw new ProcessingException("Could not create the service file!", null);
		}
	}

	public void generateServiceFiles(Filer filer) throws ProcessingException {
		log("Updating ServiceFiles");
		String viewResourceFile = "META-INF/services/com.github.thorbenkuck.powerfx.ViewFactory";
		String presenterResourceFile = "META-INF/services/com.github.thorbenkuck.powerfx.PresenterFactory";

		insert(viewResourceFile, filer, fullyQualifiedViewNames);
		insert(presenterResourceFile, filer, fullyQualifiedPresenterNames);
	}

}
