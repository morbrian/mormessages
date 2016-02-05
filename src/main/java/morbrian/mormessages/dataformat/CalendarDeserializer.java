package morbrian.mormessages.dataformat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarDeserializer extends JsonDeserializer<Calendar> {
  @Override
  public Calendar deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JsonProcessingException {
    SimpleDateFormat format = new SimpleDateFormat(FormatConstants.DEFAULT_DATE_FORMAT);
    String dateString = jsonParser.getText();
    try {
      return new Calendar.Builder().setInstant(format.parse(dateString)).build();
    } catch (ParseException e) {
      throw new IOException("failed to deserialize date from " + dateString, e);
    }
  }
}
