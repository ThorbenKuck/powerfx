package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.exceptions.NoPresenterFactorySetException;
import com.github.thorbenkuck.powerfx.exceptions.NoViewFactorySetException;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class SuperControllerMapping {

	private static final Map<Class<?>, ViewFactory<?, ?>> viewFactoryMap = new HashMap<>();
	private static final Map<Class<?>, PresenterFactory<?, ?>> presenterFactoryMap = new HashMap<>();

	static {
		ServiceLoader.load(ViewFactory.class).forEach(ViewFactory::getModifiers);
		ServiceLoader.load(PresenterFactory.class).forEach(PresenterFactory::getModifiers);
	}

	public static <T extends View, S extends Presenter<T>> ViewFactory<T, S> getViewFactory(Class<T> type) {
		ViewFactory<T, S> viewFactory;

		synchronized (viewFactoryMap) {
			viewFactory = (ViewFactory<T, S>) viewFactoryMap.get(type);
		}

		if (viewFactory == null) {
			throw new NoViewFactorySetException("There is no ViewFactory registered for the View type" + type);
		}

		return viewFactory;
	}

	public static <T extends View, S extends Presenter<T>> PresenterFactory<T, S> getPresenterFactory(Class<T> type) {
		PresenterFactory<T, S> viewFactory;

		synchronized (viewFactoryMap) {
			viewFactory = (PresenterFactory<T, S>) presenterFactoryMap.get(type);
		}

		if (viewFactory == null) {
			throw new NoPresenterFactorySetException("There is no ViewFactory registered for the View type" + type);
		}

		return viewFactory;
	}

	public static <T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T, S> presenterFactory, ViewFactory<T, S> viewFactory) {
		register(type, presenterFactory);
		register(type, viewFactory);
	}

	public static <T extends View, S extends Presenter<T>> void register(Class<T> type, ViewFactory<T, S> viewFactory) {
		synchronized (viewFactoryMap) {
			viewFactoryMap.put(type, viewFactory);
		}
	}

	public static <T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T, S> presenterFactory) {
		synchronized (presenterFactoryMap) {
			presenterFactoryMap.put(type, presenterFactory);
		}
	}
}