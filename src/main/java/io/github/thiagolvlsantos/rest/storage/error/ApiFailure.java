package io.github.thiagolvlsantos.rest.storage.error;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
@Builder
public class ApiFailure extends RuntimeException {

	private HttpStatus status;
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();
	private String message;
	private String debugMessage;
	@JsonIgnore
	private Throwable ex;
	@Builder.Default
	private List<ApiFailureDetail> details = new LinkedList<>();

	public ApiFailure(HttpStatus status, String message, Throwable ex) {
		this.status = status;
		this.message = message;
		this.ex = ex;
		if (ex != null) {
			this.debugMessage = ex.getLocalizedMessage();
		}
	}
}
