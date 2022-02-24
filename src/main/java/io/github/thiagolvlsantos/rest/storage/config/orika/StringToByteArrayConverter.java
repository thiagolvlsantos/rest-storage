package io.github.thiagolvlsantos.rest.storage.config.orika;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class StringToByteArrayConverter extends BidirectionalConverter<String, byte[]> {

	@Override
	public byte[] convertTo(String source, Type<byte[]> destinationType, MappingContext mappingContext) {
		return source.getBytes();
	}

	@Override
	public String convertFrom(byte[] source, Type<String> destinationType, MappingContext mappingContext) {
		return new String(source);
	}

}