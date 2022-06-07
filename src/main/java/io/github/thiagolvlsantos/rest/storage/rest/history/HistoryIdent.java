package io.github.thiagolvlsantos.rest.storage.rest.history;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

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
public class HistoryIdent implements Serializable {
	private String name;
	private String emailAddress;
	private Date when;
	private TimeZone timeZone;
}
