package com.github.thorbenkuck.powerfx.annotations.processors;

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
import java.util.List;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public abstract class MVPProcessor extends AbstractProcessor {

	private final List<Element> doneProcessing = new ArrayList<>();
	protected Types types;
	protected Elements elements;
	protected Filer filer;
	protected Messager messager;

	protected boolean hasBeenProcessed(Element typeElement) {
		return doneProcessing.contains(typeElement);
	}

	protected void markAsProcessed(Element typeElement) {
		doneProcessing.add(typeElement);
	}

	protected void error(String msg, Element element, AnnotationMirror mirror) {
		messager.printMessage(Diagnostic.Kind.ERROR, msg, element, mirror);
	}

	protected void error(String msg, Element element) {
		messager.printMessage(Diagnostic.Kind.ERROR, msg, element);
	}

	protected void error(String msg) {
		messager.printMessage(Diagnostic.Kind.ERROR, msg);
	}

	protected void log(String msg) {
		messager.printMessage(Diagnostic.Kind.NOTE, msg);
	}

	protected void log(String msg, Element element) {
		messager.printMessage(Diagnostic.Kind.NOTE, msg, element);
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		types = processingEnv.getTypeUtils();
		elements = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
	}
}
