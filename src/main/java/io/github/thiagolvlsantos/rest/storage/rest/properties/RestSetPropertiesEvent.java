package io.github.thiagolvlsantos.rest.storage.rest.properties;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestSetPropertiesEvent<T> extends AbstractRestEvent<T> {

	private String property;
	private String dataAsString;

	private String filter;
	private String paging;
	private String sorting;

	public RestSetPropertiesEvent(Object source) {
		super(source);
	}
}
