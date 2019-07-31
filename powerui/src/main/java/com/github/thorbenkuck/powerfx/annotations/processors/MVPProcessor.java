package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class MVPProcessor extends AbstractProcessor {

	private final List<Element> doneProcessing = new ArrayList<>();
	protected Types types;
	protected Elements elements;
	protected Filer filer;
	protected Messager messager;

	protected boolean hasBeenProcessed(Element typeElement) {
		return doneProcessing.contains(typeElement);
	}

	protected void markAsProcessed(Element typeElement) {
		doneProcessing.add(typeElement);
	}

	protected abstract void handle(Element element, Logger logger);

	protected abstract Class<? extends Annotation> supportedAnnotation();

	@Override
	public final SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public final Set<String> getSupportedAnnotationTypes() {
		Set<String> annotations = new LinkedHashSet<>();
		annotations.add(supportedAnnotation().getCanonicalName());
		return annotations;
	}

	@Override
	public final synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		types = processingEnv.getTypeUtils();
		elements = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
	}

	@Override
	public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(supportedAnnotation())) {
			if (!hasBeenProcessed(element)) {
				Logger logger = new Logger(element, messager);
				try {
					handle(element, logger);
					markAsProcessed(element);
				} catch (ProcessingException e) {
					logger.error(e.getMsg(), e.getElement());
				} catch (Exception e) {
					logger.error("Could not write because of IOException: " + e.getMessage());
				}
			}
		}

		return true;
	}
}
