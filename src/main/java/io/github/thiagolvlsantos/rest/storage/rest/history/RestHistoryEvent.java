package io.github.thiagolvlsantos.rest.storage.rest.history;

import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestHistoryEvent<T> extends AbstractRestEvent<T> {

	private String paging;

	public RestHistoryEvent(Object source) {
		super(source);
	}
}
