package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.annotations.processors.MethodCallDelegate;
import com.github.thorbenkuck.powerfx.annotations.processors.MethodCreator;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.ExecutableElement;

public class ConstructMethodCreator implements MethodCreator {

	private ExecutableElement construct;

	public void setConstruct(ExecutableElement construct) {
		this.construct = construct;
	}

	@Override
	public MethodSpec create() {
		if (willCreate()) {
			return MethodCallDelegate.delegateMethodCall(construct, "construct", "presenter");
		}

		return null;
	}

	@Override
	public boolean willCreate() {
		return construct != null;
	}
}
