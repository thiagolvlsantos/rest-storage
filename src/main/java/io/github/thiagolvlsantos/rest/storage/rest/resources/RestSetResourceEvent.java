package io.github.thiagolvlsantos.rest.storage.rest.resources;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestSetResourceEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private ResourceVO resource;

	public RestSetResourceEvent(Object source) {
		super(source);
	}
}
