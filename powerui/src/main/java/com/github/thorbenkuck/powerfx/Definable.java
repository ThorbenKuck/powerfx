package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.di.WiredTypes;

public interface Definable<T> {

	T define();

	/**
	 * Defined by Construct
	 */
	default void construct() {}

	/**
	 * Defined by Displayed
	 */
	default void displayed() {}

	/**
	 * Defined by Destroy
	 */
	default void destroy() {}

	/**
	 * Defined by Inject
	 *
	 * @param wiredTypes the WiredTypes instance, to fetch the instances from
	 */
	default void inject(WiredTypes wiredTypes) {
	}

}
