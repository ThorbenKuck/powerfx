package com.github.thorbenkuck.powerfx.core;

import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FXUtils {

	private FXUtils() {
		throw new IllegalAccessError("You shall not pass!");
	}

	private static void extractToPlatform(Runnable runnable) {
		Platform.runLater(runnable);
	}

	public static Completable runOnFXThread(Runnable runnable) {
		CompletableFuture<Object> completableFuture = new CompletableFuture<>();
		Completable completable = new Completable(completableFuture);
		if (Platform.isFxApplicationThread()) {
			try {
				runnable.run();
				completableFuture.complete(null);
			} catch (Exception e) {
				completableFuture.completeExceptionally(e);
			}
		} else {
			extractToPlatform(() -> {
				try {
					runnable.run();
					completableFuture.complete(null);
				} catch (Exception e) {
					completableFuture.completeExceptionally(e);
				}
			});
		}
		return completable;
	}

	public static <T> Future<T> createOnFXThread(Supplier<T> supplier) {
		CompletableFuture<T> completableFuture = new CompletableFuture<>();
		if (Platform.isFxApplicationThread()) {
			try {
				T t = supplier.get();
				completableFuture.complete(t);
			} catch (Exception e) {
				completableFuture.completeExceptionally(e);
			}
		} else {
			extractToPlatform(() -> {
				try {
					T t = supplier.get();
					completableFuture.complete(t);
				} catch (Exception e) {
					completableFuture.completeExceptionally(e);
				}
			});
		}

		return completableFuture;
	}

	public static <T> Completable consumeOnFXThread(T t, Consumer<T> consumer) {
		CompletableFuture<T> completableFuture = new CompletableFuture<>();
		Completable completable = new Completable((CompletableFuture<Object>) completableFuture);
		if (Platform.isFxApplicationThread()) {
			try {
				consumer.accept(t);
				completableFuture.complete(t);
			} catch (Exception e) {
				completableFuture.completeExceptionally(e);
			}
		} else {
			extractToPlatform(() -> {
				try {
					consumer.accept(t);
					completableFuture.complete(t);
				} catch (Exception e) {
					completableFuture.completeExceptionally(e);
				}
			});
		}
		return completable;
	}
}
