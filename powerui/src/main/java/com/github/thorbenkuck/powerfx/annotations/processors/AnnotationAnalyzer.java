package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class AnnotationAnalyzer {

	public static <T extends Annotation> List<? extends TypeMirror> fetchTypeElements(T t, Consumer<T> consumer) {
		try {
			System.out.println("Calling method..");
			consumer.accept(t);
			return Collections.emptyList();
		} catch (MirroredTypesException e) {
			return e.getTypeMirrors();
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

}
