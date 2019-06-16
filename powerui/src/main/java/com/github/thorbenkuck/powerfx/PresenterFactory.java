package com.github.thorbenkuck.powerfx;

public interface PresenterFactory<T> extends UIFactory {

	DefinablePresenter<T> create();

}
