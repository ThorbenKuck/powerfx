package com.github.thorbenkuck.powerfx.annotations.processors;

import com.github.thorbenkuck.powerfx.annotations.PresenterImplementation;
import com.github.thorbenkuck.powerfx.annotations.ViewImplementation;

import javax.annotation.processing.Filer;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.stream.Collectors;

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
			annotation.value();
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

	private boolean isOkay(PresenterContainer container, ViewContainer viewContainer) {
		TypeMirror presenterAnnotationValue = getTypeMirror(container.getElement().getAnnotation(PresenterImplementation.class));
		return presenterAnnotationValue.equals(viewContainer.getRepresentedInterface());
	}

	private void handleViewElement(ViewContainer element) throws ProcessingException {
		List<PresenterContainer> matchingPresenters = presenterContainers.stream()
				.filter(e -> isOkay(e, element))
				.collect(Collectors.toList());

		if (matchingPresenters.size() == 0) {
			throw new ProcessingException("Found no matching Presenters for the element " + element.getElement(), element.getElement());
		}

		if (matchingPresenters.size() > 1) {
			throw new ProcessingException("Found multiple matching Presenters for the element " + element.getElement(), element.getElement());
		}

		PresenterContainer matchingPresenter = matchingPresenters.get(0);

		classes.generate(element, matchingPresenter, filer);
	}

	public void doProcessing(Filer filer) throws ProcessingException {
		System.out.println("Starting to process");
		this.filer = filer;
		for (ViewContainer e : viewContainers) {
			System.out.println(e);
			handleViewElement(e);
		}
	}
}
