package com.github.thorbenkuck.powerfx.annotations.processors.presenter;

import com.github.thorbenkuck.powerfx.annotations.Presenter;
import com.github.thorbenkuck.powerfx.annotations.View;
import com.github.thorbenkuck.powerfx.annotations.processors.MVPProcessor;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@AutoService(Processor.class)
public class PresenterProcessor extends MVPProcessor {

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotations = new LinkedHashSet<>();
		annotations.add(Presenter.class.getCanonicalName());
		return annotations;
	}
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(Presenter.class)) {
			if(!(element instanceof TypeElement)) {
				error("Wrongly annotated element", element);
				markAsProcessed(element);
			}
			if(!hasBeenProcessed(element)) {
				try {
					handle((TypeElement) element);
					markAsProcessed(element);
				} catch (IOException e) {
					error("Could not write because of IOException: " + e.getMessage(), element);
				}
			}
		}

		return true;
	}

	private void handle(TypeElement element) throws IOException {
		if(!(element.getEnclosingElement() instanceof PackageElement)) {
			error("You may not annotate inner classes!", element);
			return;
		}
		PresenterFactoryCreator presenterFactoryCreator = PresenterFactoryCreator.create(element, elements);
		DefinablePresenterCreator presenterCreator = DefinablePresenterCreator.create(element);

		JavaFile javaFile = presenterFactoryCreator.create(presenterCreator);
		javaFile.writeTo(filer);
	}
}
