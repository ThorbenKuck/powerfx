package com.github.thorbenkuck.powerfx;

import java.util.Collections;
import java.util.List;

public interface UIFactory {

	default boolean isLazy() { return false; }

	default List<Class<?>> getIdentifier() {
		return Collections.emptyList();
	}

	default boolean producesInstanceOf(Class clazz) {
		return false;
	}
}
