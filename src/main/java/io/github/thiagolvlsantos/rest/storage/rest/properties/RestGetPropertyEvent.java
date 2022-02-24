package io.github.thiagolvlsantos.rest.storage.rest.properties;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestGetPropertyEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String property;
	private String commit;
	private Long at;

	public RestGetPropertyEvent(Object source) {
		super(source);
	}
}
