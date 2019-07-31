package com.github.thorbenkuck.powerfx.annotations.processors.view;

import com.github.thorbenkuck.powerfx.annotations.View;
import com.github.thorbenkuck.powerfx.annotations.processors.Logger;
import com.github.thorbenkuck.powerfx.annotations.processors.MVPProcessor;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

//@AutoService(Processor.class)
public class ViewProcessor extends MVPProcessor {

	@Override
	protected void handle(Element element, Logger logger) {

	}

	@Override
	protected Class<? extends Annotation> supportedAnnotation() {
		return View.class;
	}
}