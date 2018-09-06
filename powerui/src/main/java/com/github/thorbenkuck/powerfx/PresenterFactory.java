package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.pipe.PipelineElement;

import java.util.Collections;
import java.util.List;

public interface PresenterFactory<T extends View, S extends Presenter<T>> {

	S create();

	default void apply(Object presenter, SuperController superController) {
	}

	default List<PipelineElement<S>> getModifiers() {
		return Collections.emptyList();
	}

}
