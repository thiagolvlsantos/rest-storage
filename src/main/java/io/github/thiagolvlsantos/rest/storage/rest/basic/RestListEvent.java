package io.github.thiagolvlsantos.rest.storage.rest.basic;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestListEvent<T> extends AbstractRestEvent<T> {

	private String filter;
	private String paging;
	private String sorting;
	private String commit;
	private Long at;

	public RestListEvent(Object source) {
		super(source);
	}
}
