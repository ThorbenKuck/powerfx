package com.github.thorbenkuck.powerfx.annotations.processors.view;

import com.github.thorbenkuck.powerfx.annotations.processors.MVPProcessor;
import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@AutoService(Processor.class)
public class ViewProcessor extends MVPProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		return false;
	}
}