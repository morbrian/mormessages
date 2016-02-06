package morbrian.mormessages.dateformat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import morbrian.mormessages.dataformat.CalendarDeserializer;
import morbrian.mormessages.dataformat.FormatConstants;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarTest {

  @Test
  public void shouldSerializeDeserialize() throws IOException, ParseException {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule testModule = new SimpleModule("MyModule")
    .addDeserializer(Calendar.class, new CalendarDeserializer());
    mapper.registerModule(testModule);


    String epoch = "\"1969-12-31T16:00:00.000000-0800\"";

    Calendar epochCalendar = mapper.readValue(epoch, Calendar.class);
    //Calendar epochCalendar = deserialize(epoch);

    String other = "\"2016-02-05T21:34:56.000917-0800\"";

    Calendar otherCalendar = mapper.readValue(other, Calendar.class);
    //Calendar otherCalendar = deserialize(other);
  }

  private Calendar deserialize(String value) throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat(FormatConstants.DEFAULT_DATE_FORMAT);
    return new Calendar.Builder().setInstant(format.parse(value)).build();
  }
}
