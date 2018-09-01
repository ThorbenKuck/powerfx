package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.annotations.PresenterFactoryDefinition;
import com.github.thorbenkuck.powerfx.annotations.ViewFactoryDefinition;
import com.github.thorbenkuck.powerfx.exceptions.NoPresenterFactorySetException;
import com.github.thorbenkuck.powerfx.exceptions.NoViewFactorySetException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuperControllerMapping {

	private static final Map<Class<?>, ViewFactory<?, ?>> viewFactoryMap = new HashMap<>();
	private static final Map<Class<?>, PresenterFactory<?>> presenterFactoryMap = new HashMap<>();

	private static void handle(ClassInfoList classInfoList) {
		List<Class<?>> classList = classInfoList.loadClasses();

		classList.forEach(clazz -> {
			try {
				clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	static {
		ClassGraph classGraph = new ClassGraph();
		ScanResult scanResult = classGraph.addClassLoader(SuperController.class.getClassLoader())
				.enableAnnotationInfo()
				.enableAllInfo()
				.scan();

		handle(scanResult.getClassesWithAnnotation(PresenterFactoryDefinition.class.getName()));

		handle(scanResult.getClassesWithAnnotation(ViewFactoryDefinition.class.getName()));
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

	public static <T extends View> PresenterFactory<T> getPresenterFactory(Class<T> type) {
		PresenterFactory<T> viewFactory;

		synchronized (viewFactoryMap) {
			viewFactory = (PresenterFactory<T>) presenterFactoryMap.get(type);
		}

		if (viewFactory == null) {
			throw new NoPresenterFactorySetException("There is no ViewFactory registered for the View type" + type);
		}

		return viewFactory;
	}

	public static <T extends View, S extends Presenter<T>> void register(Class<T> type, PresenterFactory<T> presenterFactory, ViewFactory<T, S> viewFactory) {
		register(type, presenterFactory);
		register(type, viewFactory);
	}

	public static <T extends View, S extends Presenter<T>> void register(Class<T> type, ViewFactory<T, S> viewFactory) {
		System.out.println("Registering " + type + " ViewFactory");
		synchronized (viewFactoryMap) {
			viewFactoryMap.put(type, viewFactory);
		}
	}

	public static <T extends View> void register(Class<T> type, PresenterFactory<T> presenterFactory) {
		System.out.println("Registering " + type + " PresenterFactory");
		synchronized (presenterFactoryMap) {
			presenterFactoryMap.put(type, presenterFactory);
		}
	}
}