package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.annotations.processors.MethodCallDelegate;
import com.github.thorbenkuck.powerfx.annotations.processors.MethodCreator;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.ExecutableElement;

public class DestroyMethodCreator implements MethodCreator {

	private ExecutableElement destroy;

	@Override
	public MethodSpec create() {
		return MethodCallDelegate.delegateMethodCall(destroy, "destroy", "presenter");
	}

	@Override
	public boolean willCreate() {
		return destroy != null;
	}

	public void setDestroy(ExecutableElement destroy) {
		this.destroy = destroy;
	}
}
