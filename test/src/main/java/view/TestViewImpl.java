package view;

import com.github.thorbenkuck.powerfx.annotations.InjectPresenter;
import com.github.thorbenkuck.powerfx.annotations.ViewImplementation;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import presenter.TestPresenter;

@ViewImplementation(TestPresenter.class)
class TestViewImpl implements TestView {

	private final TestPresenter testPresenter;
	private Stage stage;

	TestViewImpl(TestPresenter testPresenter) {
		this.testPresenter = testPresenter;
	}

	@Override
	public void instantiate() {
		TextField textField = new TextField("This is an Example");
		Scene scene = new Scene(textField);
		stage.setScene(scene);
	}

	@InjectPresenter
	void inject(TestPresenter testPresenter) {
		System.out.println("presenter.TestPresenter injected. Same as mine: " + testPresenter.equals(this.testPresenter));
	}

	@Override
	public void injectStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public TestPresenter getPresenter() {
		return testPresenter;
	}
}
