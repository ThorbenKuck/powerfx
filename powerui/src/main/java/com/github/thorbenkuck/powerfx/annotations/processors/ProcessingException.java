package com.github.thorbenkuck.powerfx.annotations.processors;

import javax.lang.model.element.Element;

public class ProcessingException extends RuntimeException {

	private final String msg;
	private final Element element;

	public ProcessingException(String msg, Element element) {
		this.msg = msg;
		this.element = element;
	}

	public String getMsg() {
		return msg;
	}

	public Element getElement() {
		return element;
	}
}
