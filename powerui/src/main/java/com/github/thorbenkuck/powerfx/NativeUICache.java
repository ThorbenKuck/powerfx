package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.exceptions.NotCachedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class NativeUICache implements UICache {

	private final Map<Class<? extends View>, View> viewCache = new HashMap<>();
	private final Map<Class<? extends Presenter>, Presenter> presenterCache = new HashMap<>();

	private Class<?> tryToFindKey(Class<?> findType, Set<Class<?>> classes) throws NotCachedException {
		for (Class<?> type : classes) {
			if (type.isAssignableFrom(findType)) {
				return type;
			}
		}

		throw new NotCachedException();
	}

	private <T extends View> T tryFindCachedView(Class<T> viewType) throws NotCachedException {
		Set<Class<? extends View>> keys;
		synchronized (viewCache) {
			keys = viewCache.keySet();
		}

		for (Class<? extends View> type : keys) {
			if (type.isAssignableFrom(viewType)) {
				T t;

				try {
					synchronized (presenterCache) {
						t = (T) presenterCache.get(type);
					}
				} catch (ClassCastException e) {
					throw new NotCachedException();
				}

				return t;
			}
		}

		throw new NotCachedException();
	}

	private <T extends Presenter> T tryFindCachedPresenter(Class<T> presenterType) throws NotCachedException {
		Set<Class<? extends Presenter>> keys;
		synchronized (presenterCache) {
			keys = presenterCache.keySet();
		}

		for (Class<? extends Presenter> type : keys) {
			if (type.isAssignableFrom(presenterType)) {
				T t;

				try {
					synchronized (presenterCache) {
						t = (T) presenterCache.get(type);
					}
				} catch (ClassCastException e) {
					throw new NotCachedException();
				}

				return t;
			}
		}

		throw new NotCachedException();
	}

	@Override
	public void cache(Class<? extends View> type, View view) {
		synchronized (viewCache) {
			viewCache.put(type, view);
		}
	}

	@Override
	public void cache(Class<? extends Presenter> type, Presenter presenter) {
		synchronized (presenterCache) {
			presenterCache.put(type, presenter);
		}
	}

	@Override
	public void cache(View view) {
		synchronized (viewCache) {
			viewCache.put(view.getClass(), view);
		}
	}

	@Override
	public void cache(Presenter presenter) {
		synchronized (presenterCache) {
			presenterCache.put(presenter.getClass(), presenter);
		}
	}

	@Override
	public boolean isViewCached(Class<? extends View> type) {
		View view;
		synchronized (viewCache) {
			view = viewCache.get(type);
		}

		return view != null;
	}

	@Override
	public boolean isPresenterCached(Class<? extends Presenter> type) {
		Presenter presenter;
		synchronized (presenterCache) {
			presenter = presenterCache.get(type);
		}

		return presenter != null;
	}

	@Override
	public <T extends View> T getCachedView(Class<T> viewType) throws NotCachedException {
		if (!isViewCached(viewType)) {
			throw new NotCachedException();
		}

		View view;
		synchronized (viewCache) {
			view = viewCache.get(viewType);
		}

		try {
			if (view != null) {
				return (T) view;
			} else {
				return tryFindCachedView(viewType);
			}
		} catch (ClassCastException e) {
			return tryFindCachedView(viewType);
		}
	}

	@Override
	public <T extends View, S extends Presenter<T>> S getCachedPresenter(Class<S> presenterType) throws NotCachedException {
		if (!isPresenterCached(presenterType)) {
			throw new NotCachedException();
		}

		Presenter presenter;
		synchronized (presenterCache) {
			presenter = presenterCache.get(presenterType);
		}

		try {
			if (presenter != null) {
				return (S) presenter;
			} else {
				return tryFindCachedPresenter(presenterType);
			}
		} catch (ClassCastException e) {
			return tryFindCachedPresenter(presenterType);
		}
	}
}
