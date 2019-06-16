package com.github.thorbenkuck.powerfx;

public interface ViewFactory<T> extends UIFactory {

	DefinableView<T> create();

}
