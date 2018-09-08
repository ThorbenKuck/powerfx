package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.pipe.Pipeline;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

class NativeSuperController implements SuperController {

	private final AtomicReference<View> currentView = new AtomicReference<>();
	private final AtomicReference<Stage> mainStage = new AtomicReference<>();
	private final Map<Class<?>, ViewFactory<?, ?>> viewFactoryMap = new HashMap<>();
	private final Map<Class<?>, PresenterFactory<?, ?>> presenterFactoryMap = new HashMap<>();
	private final Object SUPPLIER_LOCK = new Object();
	private final Object DISPATCHER_LOCK = new Object();
	private final UICache cache = UICache.create();
	private Supplier<Stage> stageSupplier = Stage::new;
	private ViewDispatcher viewDispatcher = new AnonymousViewDispatcher();

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
		presenter.injectView(view);

		Pipeline<T, S> pipeline = Pipeline.create();
		pipeline.addPresenterModifier(presenterFactory.getModifiers());
		pipeline.addViewModifier(viewFactory.getModifiers());

		pipeline.apply(view, presenter, this);

		synchronized (DISPATCHER_LOCK) {
			return viewDispatcher.dispatch(view, presenter, stage);
		}
	}

	private Stage createStage() {
		synchronized (SUPPLIER_LOCK) {
			return stageSupplier.get();
		}
	}

	@Override
	public void createNewMainStage() {
		final Stage stage;
		synchronized (SUPPLIER_LOCK) {
			stage = createStage();
		}

		mainStage.set(stage);
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
	public void setStageSupplier(Supplier<Stage> stageSupplier) {
		if (stageSupplier == null) {
			throw new IllegalArgumentException("The Supplier<Stage> cannot be null!");
		}

		synchronized (SUPPLIER_LOCK) {
			this.stageSupplier = stageSupplier;
		}
	}

	@Override
	public void setViewDispatcher(ViewDispatcher viewDispatcher) {
		synchronized (DISPATCHER_LOCK) {
			this.viewDispatcher = viewDispatcher;
		}
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

	private final class AnonymousViewDispatcher implements ViewDispatcher {

		@Override
		public <T extends View, S extends Presenter<T>> T dispatch(T view, S presenter, Stage stage) {
			view.injectStage(stage);
			presenter.instantiate(view);

			if (!stage.isShowing()) {
				stage.show();
			}

			return view;
		}
	}
}
