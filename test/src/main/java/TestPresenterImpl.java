import com.github.thorbenkuck.powerfx.annotations.PresenterImplementation;

@PresenterImplementation(TestView.class)
public class TestPresenterImpl implements TestPresenter {

	private TestView testView;

	@Override
	public void injectView(TestView testView) {
		this.testView = testView;
	}

	@Override
	public TestView getView() {
		return testView;
	}
}
