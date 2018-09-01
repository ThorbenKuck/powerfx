package com.github.thorbenkuck.powerfx;

public interface PresenterFactory<T extends View> {

	<S extends Presenter<T>> S create();

}
