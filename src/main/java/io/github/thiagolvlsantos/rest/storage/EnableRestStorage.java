package io.github.thiagolvlsantos.rest.storage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.github.thiagolvlsantos.file.storage.EnableFileStorage;
import io.github.thiagolvlsantos.git.transactions.EnableGitTransactions;
import io.github.thiagolvlsantos.rest.storage.EnableRestStorage.RestStorage;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ RestStorage.class })
@EnableFileStorage
@EnableGitTransactions
public @interface EnableRestStorage {

	@Configuration
	@ComponentScan("io.github.thiagolvlsantos.rest.storage")
	public static class RestStorage {
	}
}