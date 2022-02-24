package io.github.thiagolvlsantos.rest.storage.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.file.storage.util.repository.IObjectMapper;
import ma.glasnost.orika.MapperFacade;

@Component
public class ObjectMapperDefault implements IObjectMapper {
	private @Autowired MapperFacade mapper;

	@Override
	public <P, Q> Q map(P source, Class<Q> type) {
		return mapper.map(source, type);
	}

	@Override
	public <P, Q> List<Q> mapList(Iterable<P> source, Class<Q> type) {
		return mapper.mapAsList(source, type);
	}

}
