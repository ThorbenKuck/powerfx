package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

public interface SuperController {

	static SuperController open() {
		return new NativeSuperController();
	}

	UICache getCache();

	void show(Class<? extends View> type);

	<T extends View> T showSeparate(Class<T> type);

	void setMainStage(Stage stage);

	<T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T, S> presenterFactory, ViewFactory<T, S> viewFactory);

	<T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T, S> presenterFactory);

	<T extends View, S extends Presenter<T>> void register(Class<T> type, ViewFactory<T, S> viewFactory);
}
