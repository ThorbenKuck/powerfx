package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.exceptions.NotCachedException;

public interface UICache {

	static UICache create() {
		return new NativeUICache();
	}

	void cache(Class<? extends View> type, View view);

	void cache(Class<? extends Presenter> type, Presenter presenter);

	void cache(View view);

	void cache(Presenter presenter);

	boolean isViewCached(Class<? extends View> type);

	boolean isPresenterCached(Class<? extends Presenter> type);

	<T extends View> T getCachedView(Class<T> viewType) throws NotCachedException;

	<T extends View, S extends Presenter<T>> S getCachedPresenter(Class<S> presenterType) throws NotCachedException;
}
