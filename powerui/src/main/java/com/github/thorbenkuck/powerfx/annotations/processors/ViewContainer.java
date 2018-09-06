package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class ViewContainer extends Container {
	public ViewContainer(TypeElement element, TypeMirror representedInterface) {
		super(element, representedInterface);
	}
}
