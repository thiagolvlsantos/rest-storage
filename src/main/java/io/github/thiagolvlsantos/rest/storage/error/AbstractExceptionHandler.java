package io.github.thiagolvlsantos.rest.storage.error;

import javax.annotation.PostConstruct;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractExceptionHandler extends ResponseEntityExceptionHandler {

	@PostConstruct
	public void init() {
		if (log.isInfoEnabled()) {
			log.info("Error handler: " + this);
		}
	}

	protected ResponseEntity<Object> buildResponseEntity(ApiFailure apiError) {
		Throwable ex = apiError.getEx();
		if (ex != null && log.isDebugEnabled()) {
			log.debug(ex.getMessage(), ex);
		}
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}