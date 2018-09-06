package com.github.thorbenkuck.powerfx.pipe;

import com.github.thorbenkuck.powerfx.SuperController;

import java.util.function.BiFunction;

public interface PipelineElement<T> extends BiFunction<T, SuperController, T> {

	/**
	 * 1 > 2 > 3 > 4 > 5 ...
	 *
	 * @return
	 */
	default int priority() {
		return 3;
	}

}
