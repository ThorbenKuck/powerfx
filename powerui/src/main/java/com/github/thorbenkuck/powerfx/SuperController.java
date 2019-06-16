package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

import java.util.function.Supplier;

public interface SuperController {

	static SuperController open() {
		return new NativeSuperController();
	}

	<T> T show(Class<T> type);

	<T> T showSeparate(Class<T> type);

	void register(PresenterFactory presenterFactory);

	void register(ViewFactory viewFactory);

	void register(Class<?> type, PresenterFactory<?> presenterFactory);

	void register(Class<?> type, ViewFactory<?> viewFactory);

	void createNewMainStage();

	void setStageSupplier(Supplier<Stage> stageSupplier);

	void setViewDispatcher(ViewDispatcher viewDispatcher);

	void setMainStage(Stage stage);
}
