package com.github.thorbenkuck.powerfx;

public interface DefinablePresenter<T> extends Definable<T> {

	/**
	 * Defined by InjectView
	 *
	 * @param view the view
	 */
	void injectView(Object view);

}
