package view;

import com.github.thorbenkuck.powerfx.annotations.*;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import presenter.TestPresenter;

@View
@FactoryConfiguration(name = "TestViewFactory")
public class TestView {

	private TestPresenter testPresenter;
	private Stage stage;

	@Construct
	public void instantiate() {
		TextField textField = new TextField("This is an Example");
		Scene scene = new Scene(textField);
		stage.setScene(scene);
	}

	@Display
	public void show() {
		if(!stage.isShowing()) {
			stage.show();
		}
	}

	@Destroy
	public void destroy() {
		System.out.println("View has been destroyed");
	}

	@InjectPresenter
	void inject(TestPresenter testPresenter) {
		this.testPresenter = testPresenter;
	}

	@InjectStage
	public void injectStage(Stage stage) {
		this.stage = stage;
	}

	public TestPresenter getPresenter() {
		return testPresenter;
	}
}
