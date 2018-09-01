package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

public interface View<T extends Presenter> {
	void instantiate();

	void injectStage(Stage stage);

	default void destroy() {

	}

	T getPresenter();
}
