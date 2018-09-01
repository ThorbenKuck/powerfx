package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class ViewContainer {

	private final Element element;
	private final TypeMirror representedInterface;

	public ViewContainer(Element element, TypeMirror representedInterface) {
		this.element = element;
		this.representedInterface = representedInterface;
	}

	public Element getElement() {
		return element;
	}

	public TypeMirror getRepresentedInterface() {
		return representedInterface;
	}

	@Override
	public String toString() {
		return "ViewContainer{" +
				"element=" + element +
				", representedInterface=" + representedInterface +
				'}';
	}
}
