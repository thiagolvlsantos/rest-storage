package io.github.thiagolvlsantos.rest.storage.rest.history;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestHistoryResourceEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String paging;
	private String path;

	public RestHistoryResourceEvent(Object source) {
		super(source);
	}
}
