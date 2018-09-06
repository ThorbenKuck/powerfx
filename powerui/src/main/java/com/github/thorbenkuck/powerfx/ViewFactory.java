package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.pipe.PipelineElement;

import java.util.Collections;
import java.util.List;

public interface ViewFactory<T extends View, S extends Presenter<T>> {

	T create(S presenter);

	default void apply(Object object, SuperController superController) {
	}

	default List<PipelineElement<T>> getModifiers() {
		return Collections.emptyList();
	}

}
