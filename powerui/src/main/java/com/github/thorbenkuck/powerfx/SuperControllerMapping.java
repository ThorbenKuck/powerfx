package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.exceptions.NoPresenterFactorySetException;
import com.github.thorbenkuck.powerfx.exceptions.NoViewFactorySetException;

import java.util.*;

public class SuperControllerMapping {

	private static final Map<Class<?>, ViewFactory<?>> viewFactoryMap = new HashMap<>();
	private static final Map<Class<?>, PresenterFactory<?>> presenterFactoryMap = new HashMap<>();

	static {
		ServiceLoader.load(ViewFactory.class)
				.forEach(viewFactory -> viewFactory.getIdentifier()
					.forEach(type -> register((Class<?>) type, viewFactory)));
		ServiceLoader.load(PresenterFactory.class)
				.forEach(presenterFactory -> presenterFactory.getIdentifier()
					.forEach(type -> register((Class<?>) type, presenterFactory)));
	}

	public static <T> ViewFactory<T> getViewFactory(Class<T> type) {
		ViewFactory<T> viewFactory;

		synchronized (viewFactoryMap) {
			viewFactory = (ViewFactory<T>) viewFactoryMap.get(type);
		}

		if (viewFactory == null) {
			throw new NoViewFactorySetException("There is no ViewFactory registered for the View type" + type);
		}

		return viewFactory;
	}

	public static <T> PresenterFactory<T> getPresenterFactory(Class<T> type) {
		PresenterFactory<T> viewFactory;

		synchronized (viewFactoryMap) {
			viewFactory = (PresenterFactory<T>) presenterFactoryMap.get(type);
		}

		if (viewFactory == null) {
			throw new NoPresenterFactorySetException("There is no ViewFactory registered for the View type" + type);
		}

		return viewFactory;
	}

	public static void register(Class<?> type, ViewFactory viewFactory) {
		synchronized (viewFactoryMap) {
			viewFactoryMap.put(type, viewFactory);
		}
	}

	public static void register(Class<?> type, PresenterFactory presenterFactory) {
		synchronized (presenterFactoryMap) {
			presenterFactoryMap.put(type, presenterFactory);
		}
	}

	public static List<PresenterFactory> getAllPresenterFactories() {
		return new ArrayList<>(presenterFactoryMap.values());
	}

	public static List<ViewFactory> getAllViewFactories() {
		return new ArrayList<>(viewFactoryMap.values());
	}
}