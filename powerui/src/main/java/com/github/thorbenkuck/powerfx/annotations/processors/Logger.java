package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class Logger {

	private final Element annotatedRoot;
	private final Messager messager;

	public Logger(@Nullable Element annotatedRoot, Messager messager) {
		this.annotatedRoot = annotatedRoot;
		this.messager = messager;
	}

	public void error(String msg, Element element, AnnotationMirror mirror) {
		messager.printMessage(Diagnostic.Kind.ERROR, msg, element, mirror);
		if (annotatedRoot != null) {
			messager.printMessage(Diagnostic.Kind.ERROR, msg, annotatedRoot, mirror);
		}
	}

	public void error(String msg, Element element) {
		error(msg, element, null);
	}

	public void error(String msg) {
		error(msg, null);
	}

	public void log(String msg) {
		log(msg, null);
	}

	public void log(String msg, Element element) {
		messager.printMessage(Diagnostic.Kind.NOTE, msg, element);
		if (annotatedRoot != null) {
			messager.printMessage(Diagnostic.Kind.NOTE, msg, annotatedRoot);
		}
	}
}
