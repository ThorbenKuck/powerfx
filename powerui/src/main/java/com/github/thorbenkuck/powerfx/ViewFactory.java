package com.github.thorbenkuck.powerfx;

public interface ViewFactory<T extends View, S extends Presenter<T>> {

	T create(S presenter);

}
