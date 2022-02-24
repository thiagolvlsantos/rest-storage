package io.github.thiagolvlsantos.rest.storage.rest.basic;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestSaveEvent<T> extends AbstractRestEvent<T> {

	// inputs
	private String content;

	public RestSaveEvent(Object source) {
		super(source);
	}
}
