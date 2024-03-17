package com.ar.pckart.user.util;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ProtectDataSerializer extends JsonSerializer<Object> {

	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		//(personal identifiable information)
		String pii = value.toString().replaceAll("\\w(?=\\w{4})", "x");
		gen.writeString(pii);
	}

}
