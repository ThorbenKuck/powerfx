package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

public interface SuperController {

	static SuperController open() {
		return new NativeSuperController();
	}

	void show(Class<? extends View> type);

	<T extends View> T showSeparate(Class<T> type);

	void setMainStage(Stage stage);

	<T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T> presenterFactory, ViewFactory<T, S> viewFactory);

}
