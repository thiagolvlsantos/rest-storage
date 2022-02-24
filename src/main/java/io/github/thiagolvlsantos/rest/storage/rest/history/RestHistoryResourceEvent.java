package io.github.thiagolvlsantos.rest.storage.rest.history;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class RestHistoryResourceEvent<T> extends RestHistoryNameEvent<T> {

	private String path;

	public RestHistoryResourceEvent(Object source) {
		super(source);
	}
}
