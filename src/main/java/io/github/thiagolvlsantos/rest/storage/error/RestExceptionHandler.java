package io.github.thiagolvlsantos.rest.storage.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends AbstractExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(//
			HttpMessageNotReadableException ex, //
			HttpHeaders headers, //
			HttpStatus status, //
			WebRequest request) {
		return buildResponseEntity(new ApiFailure(HttpStatus.BAD_REQUEST, "Malformed JSON request.", ex));
	}

	@ExceptionHandler(ApiFailure.class)
	protected ResponseEntity<Object> handleApiException(ApiFailure ex) {
		return buildResponseEntity(ex);
	}

	@ExceptionHandler(Throwable.class)
	protected ResponseEntity<Object> handleGenericException(Throwable ex) {
		return buildResponseEntity(new ApiFailure(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	}
}