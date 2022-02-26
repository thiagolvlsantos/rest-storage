package io.github.thiagolvlsantos.rest.storage.rest;

import java.io.Serializable;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class AbstractRestEvent<T extends Serializable> extends ApplicationEvent {

	// inputs
	private String entity;

	// outputs
	private T result;

	public AbstractRestEvent(Object source) {
		super(source);
	}
}
