package io.github.thiagolvlsantos.rest.storage.config.orika;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.DefaultConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Configuration
public class OrikaConfig {

	private DefaultMapperFactory factory;

	@Bean
	public MapperFactory mapperFactory() {
		init();
		return factory;
	}

	private void init() {
		if (factory == null) {
			ConverterFactory cf = new DefaultConverterFactory();
			cf.registerConverter(new StringToByteArrayConverter());
			factory = new DefaultMapperFactory.Builder().converterFactory(cf).build();
		}
	}

	@Bean
	public MapperFacade mapperFacade() {
		init();
		return factory.getMapperFacade();
	}
}
