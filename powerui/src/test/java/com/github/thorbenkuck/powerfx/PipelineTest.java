package com.github.thorbenkuck.powerfx;

import com.github.thorbenkuck.powerfx.pipe.Pipeline;
import com.github.thorbenkuck.powerfx.pipe.PipelineElement;
import javafx.stage.Stage;
import org.junit.Test;

public class PipelineTest {

	@Test
	public void testSimpleAddition() {
		Pipeline<TestView, TestPresenter> pipeline = Pipeline.create();

		pipeline.addPresenterModifier(new PresenterModifier());
		pipeline.addViewModifier(new ViewModifier());

		pipeline.apply(new TestViewImpl(), new TestPresenterImpl(), null);
	}

	private interface TestView extends View<TestPresenter> {

	}

	private interface TestPresenter extends Presenter<TestView> {

	}

	private class PresenterModifier implements PipelineElement<TestPresenter> {

		@Override
		public TestPresenter apply(TestPresenter testPresenter, SuperController superController) {
			System.out.println("PRESENTER");
			return testPresenter;
		}

		@Override
		public int priority() {
			return 1;
		}
	}

	private class ViewModifier implements PipelineElement<TestView> {

		@Override
		public TestView apply(TestView testView, SuperController superController) {
			System.out.println("VIEW");
			return testView;
		}

		@Override
		public int priority() {
			return 2;
		}
	}

	private class TestViewImpl implements TestView {

		@Override
		public void instantiate() {

		}

		@Override
		public void injectStage(Stage stage) {

		}

		@Override
		public TestPresenter getPresenter() {
			return null;
		}
	}

	private class TestPresenterImpl implements TestPresenter {

		@Override
		public void injectView(TestView testView) {

		}

		@Override
		public TestView getView() {
			return null;
		}
	}
}
