package morbrian.mormessages.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SubscriptionTest {

  @Test public void shouldSerializeAndDeserialize() throws IOException {
    Subscription source =
        new Subscription(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString());

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writer().writeValueAsString(source);
    System.out.println("JSON: " + json);
    Subscription target = mapper.reader().forType(Subscription.class).readValue(json);

    String json2 = mapper.writer().writeValueAsString(target);
    System.out.println("JSON2: " + json2);

    assertEquals("deserialized", source, target);
  }
}
