package morbrian.mormessages.dataformat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationAsLongMillisSerializer extends JsonSerializer<Duration> {

  @Override public void serialize(Duration duration, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    jsonGenerator.writeNumber(duration.toMillis());

  }
}
