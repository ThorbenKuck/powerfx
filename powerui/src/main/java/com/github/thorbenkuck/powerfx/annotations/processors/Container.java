package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class Container {

	private final TypeElement element;
	private final TypeMirror representedInterface;

	public Container(TypeElement element, TypeMirror representedInterface) {
		this.element = element;
		this.representedInterface = representedInterface;
	}

	public TypeElement getElement() {
		return element;
	}

	public TypeMirror getRepresentedInterface() {
		return representedInterface;
	}

	@Override
	public String toString() {
		return "Container{" +
				"element=" + element +
				", representedInterface=" + representedInterface +
				'}';
	}
}
