package io.github.thiagolvlsantos.rest.storage.error;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiFailure {

	private HttpStatus status;
	private LocalDateTime timestamp = LocalDateTime.now();
	private String message;
	private String debugMessage;
	@JsonIgnore
	private Throwable ex;
	private List<ApiFailureDetail> details = new LinkedList<>();

	public ApiFailure(HttpStatus status, String message, Throwable ex) {
		this.status = status;
		this.message = message;
		this.ex = ex;
		this.debugMessage = ex.getLocalizedMessage();
	}
}
