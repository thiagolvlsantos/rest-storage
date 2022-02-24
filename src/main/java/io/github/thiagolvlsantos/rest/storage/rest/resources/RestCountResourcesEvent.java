package io.github.thiagolvlsantos.rest.storage.rest.resources;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestCountResourcesEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String filter;
	private String paging;
	private String commit;
	private Long at;

	public RestCountResourcesEvent(Object source) {
		super(source);
	}
}
