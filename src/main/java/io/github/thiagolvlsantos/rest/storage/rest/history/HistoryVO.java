package io.github.thiagolvlsantos.rest.storage.rest.history;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SuppressWarnings("serial")
public class HistoryVO implements Serializable {
	private String id;
	private HistoryIdent authorIdent;
	private HistoryIdent committerIdent;
	private String shortMessage;
	private String fullMessage;
}