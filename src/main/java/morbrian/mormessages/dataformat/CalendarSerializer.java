package morbrian.mormessages.dataformat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarSerializer extends JsonSerializer<Calendar> {

  @Override public void serialize(Calendar calendar, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

    SimpleDateFormat dateFormat = new SimpleDateFormat(FormatConstants.DEFAULT_DATE_FORMAT);
    String dateString = dateFormat.format(calendar.getTime());
    jsonGenerator.writeString(dateString);

  }
}
