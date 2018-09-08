import com.github.thorbenkuck.powerfx.SuperController;
import com.github.thorbenkuck.powerfx.core.FXUtils;
import com.github.thorbenkuck.powerfx.core.PowerFX;

import java.util.concurrent.ExecutionException;

public class FXUtilsTest extends PowerFX {

	@Override
	public void start(SuperController superController) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());
		final Object object = new Object();

		new Thread(() -> {
			try {
				FXUtils.consumeOnFXThread(object, o -> {
					throw new IllegalArgumentException("Foo");
				}).await();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace(System.out);
			}
		}).start();

		new Thread(() -> {
			try {
				FXUtils.createOnFXThread(() -> {
					throw new IllegalArgumentException("Bar");
				}).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace(System.out);
			}
		}).start();

		new Thread(() -> {
			try {
				System.out.println(FXUtils.createOnFXThread(() -> "Heyho!").get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
