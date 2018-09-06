import com.github.thorbenkuck.powerfx.annotations.InjectView;
import com.github.thorbenkuck.powerfx.annotations.PresenterImplementation;

@PresenterImplementation(requireViewType = TestView.class)
class TestPresenterImpl implements TestPresenter {

	private TestView testView;

	@Override
	public void injectView(TestView testView) {
		this.testView = testView;
	}

	@Override
	public TestView getView() {
		return testView;
	}

	@InjectView
	public void inject(TestView testView) {
		System.out.println("TestView was injected!");
	}
}
