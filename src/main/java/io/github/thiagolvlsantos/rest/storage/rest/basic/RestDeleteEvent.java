package io.github.thiagolvlsantos.rest.storage.rest.basic;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestDeleteEvent<T> extends AbstractRestEvent<T> {

	private String name;

	public RestDeleteEvent(Object source) {
		super(source);
	}
}
