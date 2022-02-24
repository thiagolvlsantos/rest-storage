package io.github.thiagolvlsantos.rest.storage.rest.resources;

import io.github.thiagolvlsantos.file.storage.util.repository.ResourceVO;
import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestUpdateResourceEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private ResourceVO resource;

	public RestUpdateResourceEvent(Object source) {
		super(source);
	}
}
