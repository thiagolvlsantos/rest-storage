package io.github.thiagolvlsantos.rest.storage.service;

import org.eclipse.jgit.lib.PersonIdent;

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
public class HistoryVO {
	private String id;
	private PersonIdent authorIdent;
	private PersonIdent committerIdent;
	private String shortMessage;
	private String fullMessage;
}