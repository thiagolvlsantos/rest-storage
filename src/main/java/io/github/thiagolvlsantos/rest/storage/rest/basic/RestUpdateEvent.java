package io.github.thiagolvlsantos.rest.storage.rest.basic;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestUpdateEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String content;

	public RestUpdateEvent(Object source) {
		super(source);
	}
}
