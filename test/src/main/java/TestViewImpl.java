import com.github.thorbenkuck.powerfx.annotations.ViewImplementation;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@ViewImplementation(TestPresenter.class)
public class TestViewImpl implements TestView {

	private final TestPresenter testPresenter;
	private Stage stage;

	public TestViewImpl(TestPresenter testPresenter) {
		this.testPresenter = testPresenter;
	}

	@Override
	public void instantiate() {
		TextField textField = new TextField("This is an Example");
		Scene scene = new Scene(textField);
		stage.setScene(scene);
	}

	@Override
	public void injectStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public TestPresenter getPresenter() {
		return null;
	}
}