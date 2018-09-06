package com.github.thorbenkuck.powerfx.pipe;

import java.util.Collections;
import java.util.List;

public interface ElementSupplier<T> {

	default List<PipelineElement<T>> get() {
		return Collections.emptyList();
	}

}
