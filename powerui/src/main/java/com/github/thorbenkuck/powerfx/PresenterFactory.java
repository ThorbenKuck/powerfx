package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.pipe.PipelineElement;

import java.util.Collections;
import java.util.List;

public interface PresenterFactory<T extends View, S extends Presenter<T>> extends Identifiable<T> {

	S create();

	default List<PipelineElement<S>> getModifiers() {
		return Collections.emptyList();
	}

}
