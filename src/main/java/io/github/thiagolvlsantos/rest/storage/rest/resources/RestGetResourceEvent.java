package io.github.thiagolvlsantos.rest.storage.rest.resources;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestGetResourceEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String path;
	private String commit;
	private Long at;

	public RestGetResourceEvent(Object source) {
		super(source);
	}
}
