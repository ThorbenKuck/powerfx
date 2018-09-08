package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

import java.util.function.Supplier;

public interface SuperController {

	static SuperController open() {
		return new NativeSuperController();
	}

	void createNewMainStage();

	UICache getCache();

	void show(Class<? extends View> type);

	void setStageSupplier(Supplier<Stage> stageSupplier);

	<T extends View> T showSeparate(Class<T> type);

	void setViewDispatcher(ViewDispatcher viewDispatcher);

	void setMainStage(Stage stage);

	<T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T, S> presenterFactory, ViewFactory<T, S> viewFactory);

	<T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T, S> presenterFactory);

	<T extends View, S extends Presenter<T>> void register(Class<T> type, ViewFactory<T, S> viewFactory);
}
