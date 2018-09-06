package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.pipe.Pipeline;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

class NativeSuperController implements SuperController {

	private final AtomicReference<View> currentView = new AtomicReference<>();
	private final AtomicReference<Stage> mainStage = new AtomicReference<>();
	private final Map<Class<?>, ViewFactory<?, ?>> viewFactoryMap = new HashMap<>();
	private final Map<Class<?>, PresenterFactory<?, ?>> presenterFactoryMap = new HashMap<>();
	private final UICache cache = UICache.create();

	private void handleCurrentView() {
		View view = currentView.get();
		if (view != null) {
			view.getPresenter().destroy();
		}
		currentView.set(null);
	}

	private <T extends View, S extends Presenter<T>> PresenterFactory<T, S> getPresenterFactory(Class<T> type) {
		PresenterFactory<T, S> presenterFactory = getLocalPresenterFactory(type);

		if (presenterFactory != null) {
			return presenterFactory;
		}

		presenterFactory = SuperControllerMapping.getPresenterFactory(type);

		return presenterFactory;
	}

	private <T extends View, S extends Presenter<T>> ViewFactory<T, S> getViewFactory(Class<T> type) {
		ViewFactory<T, S> viewFactory = getLocalViewFactory(type);

		if (viewFactory != null) {
			return viewFactory;
		}

		viewFactory = SuperControllerMapping.getViewFactory(type);

		return viewFactory;
	}

	private <T extends View, S extends Presenter<T>> ViewFactory<T, S> getLocalViewFactory(Class<T> type) {
		synchronized (viewFactoryMap) {
			try {
				return (ViewFactory<T, S>) viewFactoryMap.get(type);
			} catch (ClassCastException e) {
				return null;
			}
		}
	}

	private <T extends View, S extends Presenter<T>> PresenterFactory<T, S> getLocalPresenterFactory(Class<T> type) {
		synchronized (presenterFactoryMap) {
			try {
				return (PresenterFactory<T, S>) presenterFactoryMap.get(type);
			} catch (ClassCastException e) {
				return null;
			}
		}
	}

	private <T extends View, S extends Presenter<T>> T createAndShowNewView(Class<T> type, Stage stage) {
		PresenterFactory<T, S> presenterFactory = getPresenterFactory(type);
		ViewFactory<T, S> viewFactory = getViewFactory(type);

		S presenter = presenterFactory.create();
		T view = viewFactory.create(presenter);

		Pipeline<T, S> pipeline = Pipeline.create();
		pipeline.addPresenterModifier(presenterFactory.getModifiers());
		pipeline.addViewModifier(viewFactory.getModifiers());

		pipeline.apply(view, presenter, this);

		view.injectStage(stage);
		presenter.instantiate(view);

		if (!stage.isShowing()) {
			stage.show();
		}

		return view;
	}

	private Stage createStage() {
		return new Stage();
	}

	@Override
	public UICache getCache() {
		return cache;
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
	public <T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T, S> presenterFactory, ViewFactory<T, S> viewFactory) {
		register(type, presenterFactory);
		register(type, viewFactory);
	}

	@Override
	public <T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T, S> presenterFactory) {
		synchronized (presenterFactoryMap) {
			presenterFactoryMap.put(type, presenterFactory);
		}
	}

	@Override
	public <T extends View, S extends Presenter<T>> void register(Class<T> type, ViewFactory<T, S> viewFactory) {
		synchronized (viewFactoryMap) {
			viewFactoryMap.put(type, viewFactory);
		}
	}
}
