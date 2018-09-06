package com.github.thorbenkuck.powerfx.pipe;

import com.github.thorbenkuck.powerfx.Presenter;
import com.github.thorbenkuck.powerfx.SuperController;
import com.github.thorbenkuck.powerfx.View;

import java.util.*;
import java.util.stream.Collectors;

public class NativePipeline<T extends View, S extends Presenter> implements Pipeline<T, S> {

	private final List<CoreHandler<T, S>> handlerQueue = new ArrayList<>();

	@Override
	public void addViewModifier(PipelineElement<T> viewModifier) {
		synchronized (handlerQueue) {
			handlerQueue.add(new TCoreHandler(viewModifier));
		}
	}

	@Override
	public void addPresenterModifier(PipelineElement<S> presenterModifier) {
		synchronized (handlerQueue) {
			handlerQueue.add(new SCoreHandler(presenterModifier));
		}
	}

	@Override
	public Elements<T, S> apply(T view, S presenter, SuperController superController) {
		final List<CoreHandler<T, S>> list;

		synchronized (handlerQueue) {
			list = new ArrayList<>(handlerQueue);
		}

		final Queue<CoreHandler<T, S>> toWorkOn = list.stream()
				.sorted(Comparator.comparingInt(CoreHandler::prio))
				.collect(Collectors.toCollection(LinkedList::new));


		Elements<T, S> elements = new Elements<>(view, presenter);

		CoreHandler<T, S> current = toWorkOn.poll();

		while (current != null) {
			elements = current.apply(elements, superController);
			current = toWorkOn.poll();
		}

		return elements;
	}

	private interface CoreHandler<T extends View, S extends Presenter> {

		Elements<T, S> apply(Elements<T, S> elements, SuperController superController);

		int prio();

	}

	public static class Elements<T extends View, S extends Presenter> {

		private T view;
		private S presenter;

		private Elements(T th, S sh) {
			view = th;
			presenter = sh;
		}

		public T getView() {
			return view;
		}

		public void setView(T th) {
			view = th;
		}

		public S getPresenter() {
			return presenter;
		}

		public void setPresenter(S sh) {
			presenter = sh;
		}
	}

	private class TCoreHandler implements CoreHandler<T, S> {

		private final PipelineElement<T> pipelineElement;

		private TCoreHandler(PipelineElement<T> pipelineElement) {
			this.pipelineElement = pipelineElement;
		}

		public Elements<T, S> apply(Elements<T, S> elements, SuperController superController) {
			try {
				elements.setView(pipelineElement.apply(elements.getView(), superController));
				return elements;
			} catch (ClassCastException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public int prio() {
			return pipelineElement.priority();
		}
	}

	private class SCoreHandler implements CoreHandler<T, S> {

		private final PipelineElement<S> pipelineElement;

		private SCoreHandler(PipelineElement<S> pipelineElement) {
			this.pipelineElement = pipelineElement;
		}

		public Elements<T, S> apply(Elements<T, S> elements, SuperController superController) {
			try {
				elements.setPresenter(pipelineElement.apply(elements.getPresenter(), superController));
				return elements;
			} catch (ClassCastException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public int prio() {
			return pipelineElement.priority();
		}
	}
}
