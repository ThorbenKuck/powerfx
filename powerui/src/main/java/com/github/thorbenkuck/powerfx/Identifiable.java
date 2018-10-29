package com.github.thorbenkuck.powerfx;

public interface Identifiable<T> {

	default Class<T> getIdentifier() {
		return null;
	}

}
