package com.github.thorbenkuck.powerfx.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class Completable {

	private final CompletableFuture<Object> completableFuture;
	private Throwable thrown;

	public Completable(CompletableFuture<Object> completableFuture) {
		this.completableFuture = completableFuture.exceptionally(throwable -> {
			thrown = throwable;
			return null;
		});
	}

	public void await() throws InterruptedException, ExecutionException {
		completableFuture.get();
		if (thrown != null) {
			throw new ExecutionException("The extracted algorithm threw an Exception", thrown);
		}
	}

}
