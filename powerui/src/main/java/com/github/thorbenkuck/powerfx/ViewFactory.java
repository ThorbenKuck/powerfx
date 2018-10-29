package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.pipe.PipelineElement;

import java.util.Collections;
import java.util.List;

public interface ViewFactory<T extends View, S extends Presenter<T>> extends Identifiable<T> {

	T create(S presenter);

	default List<PipelineElement<T>> getModifiers() {
		return Collections.emptyList();
	}

}
