package io.github.thiagolvlsantos.rest.storage.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.github.thiagolvlsantos.json.predicate.exceptions.JsonPredicateException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandlerPredicate extends AbstractExceptionHandler {

	@ExceptionHandler(JsonPredicateException.class)
	protected ResponseEntity<Object> handleJsonPredicateException(JsonPredicateException ex) {
		return buildResponseEntity(new ApiFailure(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	}
}