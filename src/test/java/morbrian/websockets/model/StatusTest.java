package morbrian.websockets.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;

public class StatusTest {

    @Test
    public void shouldSupportEquality() {
        Status first = new Status(Status.Type.UNAUTHORIZED, "sample message");
        Status second = new Status(Status.Type.UNAUTHORIZED, "sample message");
        assertEquals("equals", first, second);

        Status third = new Status(Status.Type.SUCCESS, "other notes");
        assertNotEquals(first, third);
    }

    @Test
    public void shouldSerializeAndDeserialize() throws IOException {
        Status sourceStatus = new Status(Status.Type.UNAUTHORIZED, "sample message");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writer().writeValueAsString(sourceStatus);
        Status targetStatus = mapper.reader().forType(Status.class).readValue(json);

        assertEquals("status", sourceStatus, targetStatus);
    }
}
