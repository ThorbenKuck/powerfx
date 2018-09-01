package com.github.thorbenkuck.powerfx;

public interface Presenter<T extends View> {

	void injectView(T t);

	T getView();

	default void instantiate(T t) {
		injectView(t);
		t.instantiate();
		afterShown();
	}

	default void afterShown() {

	}

	default void destroy() {
		getView().destroy();
		onDestroy();
	}

	default void onDestroy() {

	}
}
