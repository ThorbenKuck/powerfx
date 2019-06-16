package com.github.thorbenkuck.powerfx;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

class NativeSuperController implements SuperController {

	private final AtomicReference<DefinableView> currentView = new AtomicReference<>();
	private final AtomicReference<DefinablePresenter> currentPresenter = new AtomicReference<>();
	private final AtomicReference<Stage> mainStage = new AtomicReference<>();
	private final Map<Class<?>, ViewFactory<?>> viewFactoryMap = new HashMap<>();
	private final Map<Class<?>, PresenterFactory<?>> presenterFactoryMap = new HashMap<>();
	private final Map<Class<?>, DefinableView<?>> viewMap = new HashMap<>();
	private final Map<Class<?>, DefinablePresenter<?>> presenterMap = new HashMap<>();
	private final Object supplierLock = new Object();
	private final Object dispatcherLock = new Object();
	private Supplier<Stage> stageSupplier = Stage::new;
	private ViewDispatcher viewDispatcher = new AnonymousViewDispatcher();

	{
		SuperControllerMapping.getAllPresenterFactories().forEach(this::register);
		SuperControllerMapping.getAllViewFactories().forEach(this::register);
	}

	private void clearCurrentViewAndPresenter() {
		DefinableView view = currentView.get();
		DefinablePresenter presenter = currentPresenter.get();

		if (view != null) {
			view.destroy();
			presenter.destroy();
		}

		currentView.set(null);
		currentPresenter.set(null);
	}

	private <T> PresenterFactory<T> getPresenterFactory(Class<T> type) {
		PresenterFactory<T> presenterFactory = getLocalPresenterFactory(type);

		if (presenterFactory != null) {
			return presenterFactory;
		}

		presenterFactory = SuperControllerMapping.getPresenterFactory(type);

		return presenterFactory;
	}

	private <T> ViewFactory<T> getViewFactory(Class<T> type) {
		ViewFactory<T> viewFactory = getLocalViewFactory(type);

		if (viewFactory != null) {
			return viewFactory;
		}

		viewFactory = SuperControllerMapping.getViewFactory(type);

		return viewFactory;
	}

	private <T> ViewFactory<T> getLocalViewFactory(Class<T> type) {
		synchronized (viewFactoryMap) {
			try {
				return (ViewFactory<T>) viewFactoryMap.get(type);
			} catch (ClassCastException e) {
				return null;
			}
		}
	}

	private <T> PresenterFactory<T> getLocalPresenterFactory(Class<T> type) {
		synchronized (presenterFactoryMap) {
			try {
				return (PresenterFactory<T>) presenterFactoryMap.get(type);
			} catch (ClassCastException e) {
				return null;
			}
		}
	}

	private <T> DefinableView<T> createAndShowNewView(Class<T> type, Stage stage, boolean store) {
		PresenterFactory presenterFactory = getPresenterFactory(type);
		ViewFactory<T> viewFactory = getViewFactory(type);

		DefinablePresenter presenter = presenterFactory.create();
		DefinableView<T> view = viewFactory.create();

		presenter.injectView(view.define());
		view.injectPresenter(presenter.define());

		synchronized (dispatcherLock) {
			viewDispatcher.dispatch(view, presenter, stage, stageSupplier);
		}

		if (store) {
			currentView.set(view);
			currentPresenter.set(presenter);
		}


		return view;
	}

	private Stage createStage() {
		synchronized (supplierLock) {
			return stageSupplier.get();
		}
	}

	private Stage getOrCreateStage() {
		Stage stage = mainStage.get();
		if (stage == null) {
			stage = createStage();
		}
		if (stage == null) {
			throw new IllegalStateException("The StageSupplier supplied null as a Stage!");
		}
		return stage;
	}

	@Override
	public <T> T show(Class<T> type) {
		clearCurrentViewAndPresenter();
		DefinableView<T> definableView = createAndShowNewView(type, getOrCreateStage(), true);

		return definableView.define();
	}

	@Override
	public <T> T showSeparate(Class<T> type) {
		DefinableView<T> definableView = createAndShowNewView(type, getOrCreateStage(), false);

		return definableView.define();
	}

	@Override
	public void register(PresenterFactory presenterFactory) {
		presenterFactory.getIdentifier().forEach(type -> register(type, presenterFactory));
	}

	@Override
	public void register(ViewFactory viewFactory) {
		viewFactory.getIdentifier().forEach(type -> register(type, viewFactory));
	}

	@Override
	public void register(Class<?> type, PresenterFactory<?> presenterFactory) {
		if (!presenterFactory.getIdentifier().contains(type)) {
			throw new IllegalArgumentException("The PresenterFactoryCreator is not suited for the type " + type);
		}
		synchronized (presenterFactoryMap) {
			presenterFactoryMap.put(type, presenterFactory);
		}
		if (!presenterFactory.isLazy()) {
			synchronized (presenterMap) {
				presenterMap.put(type, presenterFactory.create());
			}
		}
	}

	@Override
	public void register(Class<?> type, ViewFactory<?> viewFactory) {
		if (!viewFactory.getIdentifier().contains(type)) {
			throw new IllegalArgumentException("The PresenterFactoryCreator is not suited for the type " + type);
		}

		synchronized (viewFactoryMap) {
			viewFactoryMap.put(type, viewFactory);
		}

		if (!viewFactory.isLazy()) {
			synchronized (viewMap) {
				viewMap.put(type, viewFactory.create());
			}
		}
	}

	@Override
	public void createNewMainStage() {
		final Stage stage = createStage();

		mainStage.set(stage);
	}

	@Override
	public void setStageSupplier(Supplier<Stage> stageSupplier) {
		if (stageSupplier == null) {
			throw new IllegalArgumentException("The Supplier<Stage> cannot be null!");
		}

		synchronized (supplierLock) {
			this.stageSupplier = stageSupplier;
		}
	}

	@Override
	public void setViewDispatcher(ViewDispatcher viewDispatcher) {
		synchronized (dispatcherLock) {
			this.viewDispatcher = viewDispatcher;
		}
	}

	@Override
	public void setMainStage(Stage stage) {
		mainStage.set(stage);
	}

	private static final class AnonymousViewDispatcher implements ViewDispatcher {

		@Override
		public void dispatch(DefinableView view, DefinablePresenter presenter, Stage mainStage, Supplier<Stage> stageSupplier) {
			if (view.useNewStage()) {
				view.inject(stageSupplier.get());
			} else {
				view.inject(mainStage);
			}

			presenter.construct();
			view.construct();

			view.display();

			presenter.displayed();
			view.displayed();
		}
	}
}
