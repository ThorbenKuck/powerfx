package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class PresenterContainer {

	private final Element element;
	private final TypeMirror representedInterface;

	public PresenterContainer(Element element, TypeMirror representedInterface) {
		this.element = element;
		this.representedInterface = representedInterface;
	}

	public Element getElement() {
		return element;
	}

	public TypeMirror getRepresentedInterface() {
		return representedInterface;
	}
}
