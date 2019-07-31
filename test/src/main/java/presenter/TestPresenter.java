package presenter;

import com.github.thorbenkuck.powerfx.annotations.Destroy;
import com.github.thorbenkuck.powerfx.annotations.FactoryConfiguration;
import com.github.thorbenkuck.powerfx.annotations.InjectView;
import com.github.thorbenkuck.powerfx.annotations.Presenter;
import dependency.Dependency;
import view.TestView;

import javax.inject.Inject;

@Presenter
@FactoryConfiguration(name = "TestPresenterFactory")
public class TestPresenter {

	private TestView testView;
	@Inject
	private Dependency dependency;

	@InjectView
	public void injectView(TestView testView) {
		this.testView = testView;
		System.out.println("view.TestView was injected!");
	}

	@Destroy
	public void destroy() {
		System.out.println("The Presenter has been destroyed");
	}

	@Inject
	public void setDependency(Dependency dependency) {

	}

	public TestView getView() {
		return testView;
	}
}
