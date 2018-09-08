package com.github.thorbenkuck.powerfx.annotations.processors;

import com.github.thorbenkuck.powerfx.annotations.PresenterImplementation;
import com.github.thorbenkuck.powerfx.annotations.ViewImplementation;

import javax.annotation.processing.Filer;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

public class FactoryProcessor {

	private final List<ViewContainer> viewContainers;
	private final List<PresenterContainer> presenterContainers;
	private final Elements elements;
	private final Classes classes;
	private Filer filer;

	public FactoryProcessor(List<ViewContainer> viewContainers, List<PresenterContainer> presenterContainers, Elements elements, Types types) {
		this.viewContainers = viewContainers;
		this.presenterContainers = presenterContainers;
		this.elements = elements;
		classes = new Classes(elements, types);
	}

	public static TypeMirror getTypeMirror(PresenterImplementation annotation) {
		try {
			annotation.requireViewType();
		} catch (MirroredTypeException e) {
			return e.getTypeMirror();
		}

		return null;
	}

	public static TypeMirror getTypeMirror(ViewImplementation annotation) {
		try {
			annotation.value();
		} catch (MirroredTypeException e) {
			return e.getTypeMirror();
		}

		return null;
	}

	public void doProcessing(Filer filer) throws ProcessingException {
		this.filer = filer;
		for (ViewContainer e : viewContainers) {
			classes.generate(e, filer);
		}

		for (PresenterContainer e : presenterContainers) {
			classes.generate(e, filer);
		}
	}
}
