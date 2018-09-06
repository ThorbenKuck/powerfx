package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class PresenterContainer extends Container {
	public PresenterContainer(TypeElement element, TypeMirror representedInterface) {
		super(element, representedInterface);
	}
}
