package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

public interface DefinableView<T> extends Definable<T> {

	/**
	 * Defined by InjectPresenter
	 *
	 * @param presenter the Presenter
	 */
	void injectPresenter(Object presenter);

	/**
	 * Defined by Display
	 */
	void display();

	/**
	 * Specifically defined by InjectStage
	 *
	 * @param stage the Stage to maintain
	 */
	void inject(Stage stage);

	default boolean useNewStage() {return false;}
}
