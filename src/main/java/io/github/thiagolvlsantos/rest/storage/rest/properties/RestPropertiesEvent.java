package io.github.thiagolvlsantos.rest.storage.rest.properties;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestPropertiesEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String properties;
	private String commit;
	private Long at;

	public RestPropertiesEvent(Object source) {
		super(source);
	}
}
