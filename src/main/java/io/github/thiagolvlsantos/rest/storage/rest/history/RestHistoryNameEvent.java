package io.github.thiagolvlsantos.rest.storage.rest.history;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestHistoryNameEvent<T> extends AbstractRestEvent<T> {

	private String name;
	private String paging;

	public RestHistoryNameEvent(Object source) {
		super(source);
	}
}
