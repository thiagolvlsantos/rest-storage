package io.github.thiagolvlsantos.rest.storage.rest.basic;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestReadEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String commit;
	private Long at;

	public RestReadEvent(Object source) {
		super(source);
	}
}
