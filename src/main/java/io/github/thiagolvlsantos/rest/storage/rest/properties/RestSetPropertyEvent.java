package io.github.thiagolvlsantos.rest.storage.rest.properties;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestSetPropertyEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String property;
	private String dataAsString;

	public RestSetPropertyEvent(Object source) {
		super(source);
	}
}
