package io.github.thiagolvlsantos.rest.storage.rest.history;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestHistoryNameEvent<T> extends RestHistoryEvent<T> {

	private String name;

	public RestHistoryNameEvent(Object source) {
		super(source);
	}
}
