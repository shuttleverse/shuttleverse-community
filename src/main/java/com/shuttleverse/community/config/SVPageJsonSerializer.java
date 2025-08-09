package com.shuttleverse.community.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

/**
 * Custom JSON serializer for Spring Data Page objects to provide a stable and consistent JSON
 * structure.
 */
@JsonComponent
public class SVPageJsonSerializer extends JsonSerializer<Page<?>> {

  @Override
  public void serialize(Page<?> page, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject();

    gen.writeObjectField("content", page.getContent());
    gen.writeNumberField("page", page.getNumber());
    gen.writeNumberField("size", page.getSize());
    gen.writeNumberField("totalElements", page.getTotalElements());
    gen.writeNumberField("totalPages", page.getTotalPages());
    gen.writeBooleanField("first", page.isFirst());
    gen.writeBooleanField("last", page.isLast());
    gen.writeBooleanField("empty", page.isEmpty());

    gen.writeEndObject();
  }
}