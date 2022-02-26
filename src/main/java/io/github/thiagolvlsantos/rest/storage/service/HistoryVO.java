package io.github.thiagolvlsantos.rest.storage.service;

import java.io.Serializable;

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
@SuppressWarnings("serial")
public class HistoryVO implements Serializable {
	private String id;
	private PersonIdent authorIdent;
	private PersonIdent committerIdent;
	private String shortMessage;
	private String fullMessage;
}