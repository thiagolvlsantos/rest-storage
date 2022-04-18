package io.github.thiagolvlsantos.rest.storage.rest.resources;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@SuppressWarnings("serial")
public class ResourceMetadataVO implements Serializable {

	private String path;
	private String encoding;
	private String contentType;
	private LocalDateTime timestamp;
}