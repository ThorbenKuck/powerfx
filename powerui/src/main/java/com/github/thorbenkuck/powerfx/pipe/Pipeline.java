package com.github.thorbenkuck.powerfx.pipe;

import com.github.thorbenkuck.powerfx.Presenter;
import com.github.thorbenkuck.powerfx.SuperController;
import com.github.thorbenkuck.powerfx.View;

import java.util.List;

public interface Pipeline<T extends View, S extends Presenter> {

	static <T extends View, S extends Presenter> Pipeline<T, S> create() {
		return new NativePipeline<>();
	}

	void addViewModifier(PipelineElement<T> viewModifier);

	default void addViewModifier(List<PipelineElement<T>> viewModifier) {
		viewModifier.forEach(this::addViewModifier);
	}

	void addPresenterModifier(PipelineElement<S> presenterModifier);

	default void addPresenterModifier(List<PipelineElement<S>> presenterModifier) {
		presenterModifier.forEach(this::addPresenterModifier);
	}

	NativePipeline.Elements<T, S> apply(T t, S s, SuperController superController);

}
