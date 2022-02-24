package io.github.thiagolvlsantos.rest.storage.rest.resources;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestDeleteResourceEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String path;

	public RestDeleteResourceEvent(Object source) {
		super(source);
	}
}
