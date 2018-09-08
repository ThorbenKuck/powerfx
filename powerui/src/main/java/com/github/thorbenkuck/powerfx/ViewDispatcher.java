package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

public interface ViewDispatcher {

	<T extends View, S extends Presenter<T>> T dispatch(T view, S presenter, Stage stage);

}
