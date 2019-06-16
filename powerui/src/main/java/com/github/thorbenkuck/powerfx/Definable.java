package com.github.thorbenkuck.powerfx;

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
	 * @param object the Object to Inject
	 */
	default void inject(Object object) {}

}
