package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.annotations.Presenter;
import com.github.thorbenkuck.powerfx.annotations.processors.Logger;
import com.github.thorbenkuck.powerfx.annotations.processors.MVPProcessor;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.annotation.Annotation;

@AutoService(Processor.class)
public class PresenterProcessor extends MVPProcessor {

	private void handle(TypeElement element, Logger logger) throws IOException {
		if (!(element.getEnclosingElement() instanceof PackageElement)) {
			logger.error("You may not annotate inner classes!", element);
			return;
		}
		System.out.println("Creating PresenterFactory");
		PresenterFactoryCreator presenterFactoryCreator = PresenterFactoryCreator.create(element, elements, types);
		System.out.println("Creating DefinablePresenter");
		DefinablePresenterCreator presenterCreator = DefinablePresenterCreator.create(element);

		JavaFile javaFile = presenterFactoryCreator.create(presenterCreator);
		javaFile.writeTo(filer);
	}

	@Override
	protected void handle(Element element, Logger logger) {
		if (!(element instanceof TypeElement)) {
			logger.error("Wrongly annotated element", element);
			markAsProcessed(element);
		} else {
			try {
				handle((TypeElement) element, logger);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	protected Class<? extends Annotation> supportedAnnotation() {
		return Presenter.class;
	}
}
