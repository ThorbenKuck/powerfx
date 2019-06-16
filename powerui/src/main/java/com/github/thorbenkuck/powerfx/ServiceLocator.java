package com.github.thorbenkuck.powerfx;

public interface ServiceLocator {

	<T> T find(Class<T> type);

}
