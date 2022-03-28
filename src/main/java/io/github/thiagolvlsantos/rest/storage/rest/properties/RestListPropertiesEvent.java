package io.github.thiagolvlsantos.rest.storage.rest.properties;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestListPropertiesEvent<T> extends AbstractRestEvent<T> {

	private String properties;
	private String filter;
	private String paging;
	private String sorting;
	private String commit;
	private Long at;

	public RestListPropertiesEvent(Object source) {
		super(source);
	}
}
