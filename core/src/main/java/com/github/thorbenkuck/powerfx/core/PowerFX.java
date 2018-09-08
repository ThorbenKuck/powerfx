package com.github.thorbenkuck.powerfx.core;

import com.github.thorbenkuck.powerfx.SuperController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class PowerFX extends Application {

	private final SuperController superController = SuperController.open();
	private final List<Runnable> platformShutdownHooks = new ArrayList<>();
	private final ShutdownListener shutdownListener = new ShutdownListener();
	private final Thread shutdownListenerThread = new Thread(shutdownListener);

	{
		shutdownListenerThread.setName("PowerFX-Shutdown-Listener");
		shutdownListenerThread.setPriority(3);
	}

	protected final Stage createNewStage() {
		try {
			return FXUtils.createOnFXThread(Stage::new).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public final void start(Stage primaryStage) throws Exception {
		if (!Platform.isFxApplicationThread()) {
			Platform.exit();
			throw new IllegalStateException("Could not locate the FXApplicationThread!\nThis is a real issue, please submit this error to github");
		}

		superController.setMainStage(primaryStage);
		superController.setStageSupplier(this::createNewStage);

		shutdownListener.setToWaitFor(Thread.currentThread());
		shutdownListenerThread.start();

		start(superController);
	}

	public final void addShutdownListener(Runnable runnable) {
		synchronized (platformShutdownHooks) {
			platformShutdownHooks.add(runnable);
		}
	}

	public final void stopShutdownListener() {
		shutdownListenerThread.interrupt();
	}

	public final SuperController getSuperController() {
		return superController;
	}

	public abstract void start(SuperController superController) throws Exception;

	private final class ShutdownListener implements Runnable {

		private Thread toWaitFor;

		private void run(Runnable runnable) {
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			if (toWaitFor == null) {
				return;
			}
			try {
				toWaitFor.join();
			} catch (InterruptedException e) {
				return;
			}

			final List<Runnable> shutdownListeners;

			synchronized (platformShutdownHooks) {
				shutdownListeners = new ArrayList<>(platformShutdownHooks);
			}

			shutdownListeners.forEach(this::run);

			synchronized (platformShutdownHooks) {
				platformShutdownHooks.clear();
			}
		}

		void setToWaitFor(Thread toWaitFor) {
			this.toWaitFor = toWaitFor;
		}
	}
}
