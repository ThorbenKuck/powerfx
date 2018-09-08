import com.github.thorbenkuck.powerfx.SuperController;
import com.github.thorbenkuck.powerfx.core.PowerFX;
import view.TestView;

public class Test extends PowerFX {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(SuperController superController) throws Exception {
		superController.show(TestView.class);
		addShutdownListener(() -> System.out.println("Platform just shut down"));
	}
}
