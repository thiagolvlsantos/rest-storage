package io.github.thiagolvlsantos.rest.storage.repository;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.file.storage.util.repository.IPredicateConverter;
import io.github.thiagolvlsantos.json.predicate.IPredicateFactory;

@Component
public class PredicateConverterDefault implements IPredicateConverter {
	private @Autowired IPredicateFactory predicateFactory;

	@Override
	public Predicate<Object> toPredicate(String filter) {
		return predicateFactory.read(filter.getBytes());
	}
}