package com.github.thorbenkuck.powerfx.annotations.processors;

import com.github.thorbenkuck.powerfx.annotations.PresenterImplementation;
import com.github.thorbenkuck.powerfx.annotations.ViewImplementation;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class MVPProcessor extends AbstractProcessor {

	private final List<ViewContainer> viewElements = new ArrayList<>();
	private final List<PresenterContainer> presenterElements = new ArrayList<>();
	private Types types;
	private Elements elements;
	private Filer filer;
	private Messager messager;
	private boolean doneProcessing = false;

	private boolean checkAgainstInterface(TypeMirror toCheck, TypeMirror viewInterface) {
		return types.isAssignable(toCheck, viewInterface);
	}

	private boolean isOfInterface(Element element, TypeMirror viewInterfaceMirror) {
		log("Checking " + element + " and  " + viewInterfaceMirror + " ar assignable", element);
		TypeElement typeElement = (TypeElement) element;
		if (!checkAgainstInterface(element.asType(), viewInterfaceMirror)) {
			return false;
		} else {
			return true;
		}
	}

	private void error(String msg, Element element, AnnotationMirror mirror) {
		messager.printMessage(Diagnostic.Kind.ERROR, msg, element, mirror);
	}

	private void error(String msg, Element element) {
		messager.printMessage(Diagnostic.Kind.ERROR, msg, element);
	}

	private void error(String msg) {
		messager.printMessage(Diagnostic.Kind.ERROR, msg);
	}

	private void log(String msg) {
		messager.printMessage(Diagnostic.Kind.NOTE, msg);
	}

	private void log(String msg, Element element) {
		messager.printMessage(Diagnostic.Kind.NOTE, msg, element);
	}

	private boolean match(Element element, TypeMirror mirror, String firstErrorMessage) {
		log("Checking for " + element);

		if (element.getKind().isInterface()) {
			error(firstErrorMessage, element);
			return false;
		}

		if (isOfInterface(element, mirror)) {
			log("Element is okay and will be processed", element);
			return true;
		} else {
			error("The Annotated element " + element.getSimpleName() + " does not implement " + mirror, element);
			return false;
		}
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		types = processingEnv.getTypeUtils();
		elements = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotations = new LinkedHashSet<>();
		annotations.add(ViewImplementation.class.getCanonicalName());
		annotations.add(PresenterImplementation.class.getCanonicalName());
		return annotations;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if(doneProcessing) {
			return true;
		}
		Element viewInterfaceElement = elements.getTypeElement("com.github.thorbenkuck.powerfx.View");
		Element presenterInterfaceElement = elements.getTypeElement("com.github.thorbenkuck.powerfx.Presenter");

		TypeMirror viewInterface = types.erasure(viewInterfaceElement.asType());
		TypeMirror presenterInterface = types.erasure(presenterInterfaceElement.asType());

		log("Trying to match all annotated classes against " + viewInterface);

		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ViewImplementation.class)) {
			if (match(annotatedElement, viewInterface, "Only classes can be annotated with @ViewImplementation")) {
				System.out.println("Looking at " + annotatedElement);
				viewElements.add(new ViewContainer(annotatedElement, findMatchingInterface(annotatedElement, viewInterface)));
			} else {
				return false;
			}
		}

		log("Trying to match all annotated classes against " + presenterInterface);

		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(PresenterImplementation.class)) {
			if (match(annotatedElement, presenterInterface, "Only classes can be annotated with @PresenterImplementation")) {
				presenterElements.add(new PresenterContainer(annotatedElement, findMatchingInterface(annotatedElement, presenterInterface)));
			} else {
				return false;
			}
		}

		FactoryProcessor factoryProcessor = new FactoryProcessor(viewElements, presenterElements, elements, types);

		try {
			factoryProcessor.doProcessing(filer);
			doneProcessing = true;
			return true;
		} catch (ProcessingException e) {
			error(e.getMsg(), e.getElement());
			return false;
		}
	}

	private TypeMirror findMatchingInterface(Element annotatedElement, TypeMirror viewInterface) {
		TypeElement typeElement = (TypeElement) annotatedElement;

		List<? extends TypeMirror> collect = typeElement.getInterfaces()
				.stream().filter(typeMirror -> checkAgainstInterface(typeMirror, viewInterface))
				.collect(Collectors.toList());

		if(collect.size() == 0) {
			error("Could not locate the correct interface, which implements " + viewInterface, annotatedElement);
			return null;
		}

		if(collect.size() > 1)  {
			error("Located multiple interfaces, which abstract the " + viewInterface + "(" + collect + ")", annotatedElement);
			return null;
		}

		return collect.get(0);
	}
}
