package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.annotations.processors.MethodCallDelegate;
import com.github.thorbenkuck.powerfx.annotations.processors.MethodCreator;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.ExecutableElement;

public class DisplayedMethodCreator implements MethodCreator {

	private ExecutableElement display;

	@Override
	public MethodSpec create() {
		return MethodCallDelegate.delegateMethodCall(display, "displayed", "presenter");
	}

	@Override
	public boolean willCreate() {
		return display != null;
	}

	public void setDisplay(ExecutableElement destroy) {
		this.display = destroy;
	}
}
