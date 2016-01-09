package morbrian.websockets.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.websockets.rest.RestConfigurationProvider;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CredentialsTest {

    @Test
    public void shouldSupportEquality() {
        String randomUsername = RestConfigurationProvider.randomAlphaNumericString();
        String randomPassword = RestConfigurationProvider.randomAlphaNumericString();
        Credentials first = new Credentials(randomUsername, randomPassword);

        Credentials second = new Credentials(randomUsername, randomPassword);

        assertEquals("equals", first, second);

        Credentials third = new Credentials(RestConfigurationProvider.randomAlphaNumericString(),
                RestConfigurationProvider.randomAlphaNumericString());
        assertNotEquals(first, third);
    }

    @Test
    public void shouldSerializeAndDeserialize() throws IOException {
        String randomUsername = RestConfigurationProvider.randomAlphaNumericString();
        String randomPassword = RestConfigurationProvider.randomAlphaNumericString();
        Credentials source = new Credentials(randomUsername, randomPassword);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writer().writeValueAsString(source);
        Credentials target = mapper.reader().forType(Credentials.class).readValue(json);

        assertEquals("deserialized", source, target);
    }

}
