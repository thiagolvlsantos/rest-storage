package io.github.thiagolvlsantos.rest.storage.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.github.thiagolvlsantos.file.storage.exceptions.FileStorageException;
import io.github.thiagolvlsantos.file.storage.exceptions.FileStorageNotFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandlerStorage extends AbstractExceptionHandler {

	@ExceptionHandler(FileStorageNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(FileStorageNotFoundException ex) {
		return buildResponseEntity(new ApiFailure(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
	}

	@ExceptionHandler(FileStorageException.class)
	protected ResponseEntity<Object> handleEntityNotFound(FileStorageException ex) {
		return buildResponseEntity(new ApiFailure(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	}
}