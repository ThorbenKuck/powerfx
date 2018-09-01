package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;

class NativeSuperController implements SuperController {

	private final AtomicReference<View> currentView = new AtomicReference<>();
	private final AtomicReference<Stage> mainStage = new AtomicReference<>();

	private void handleCurrentView() {
		View view = currentView.get();
		if (view != null) {
			view.getPresenter().destroy();
		}
		currentView.set(null);
	}

	private Stage createStage() {
		return new Stage();
	}

	private <T extends View, S extends Presenter<T>> T createAndShowNewView(Class<T> type, Stage stage) {
		ViewFactory<T, S> viewFactory = SuperControllerMapping.getViewFactory(type);
		PresenterFactory<T> presenterFactory = SuperControllerMapping.getPresenterFactory(type);

		S presenter = presenterFactory.create();
		T view = viewFactory.create(presenter);

		view.injectStage(stage);

		presenter.instantiate(view);

		if (!stage.isShowing()) {
			stage.show();
		}

		return view;
	}

	@Override
	public void show(Class<? extends View> type) {
		handleCurrentView();
		View view = createAndShowNewView(type, mainStage.get());
		currentView.set(view);
	}

	@Override
	public <T extends View> T showSeparate(Class<T> type) {
		return createAndShowNewView(type, createStage());
	}

	@Override
	public void setMainStage(Stage stage) {
		mainStage.set(stage);
	}

	@Override
	public <T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T> presenterFactory, ViewFactory<T, S> viewFactory) {
		SuperControllerMapping.register(type, presenterFactory, viewFactory);
	}
}
