package io.github.thiagolvlsantos.rest.storage.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ApiFailureDetail {
	private EErrorType type;
}