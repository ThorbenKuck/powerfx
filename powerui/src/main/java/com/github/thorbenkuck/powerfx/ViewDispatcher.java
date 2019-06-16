package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

import java.util.function.Supplier;

public interface ViewDispatcher {

	void dispatch(DefinableView view, DefinablePresenter presenter, Stage mainStage, Supplier<Stage> stageSupplier);

}
