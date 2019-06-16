package presenter;

import com.github.thorbenkuck.powerfx.annotations.*;
import view.TestView;

@Presenter
@FactoryConfiguration(name = "TestPresenterFactory")
public class TestPresenter {

	private TestView testView;

	@InjectView
	public void injectView(TestView testView) {
		this.testView = testView;
		System.out.println("view.TestView was injected!");
	}

	@Destroy
	public void destroy() {
		System.out.println("The Presenter has been destroyed");
	}

	public TestView getView() {
		return testView;
	}
}
