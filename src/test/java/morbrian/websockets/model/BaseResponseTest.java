package morbrian.websockets.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BaseResponseTest {

  @Test public void shouldSupportEquality() {
    Status firstStatus = new Status(Status.Type.UNAUTHORIZED, "sample message");
    BaseResponse firstBase = new BaseResponse(firstStatus);

    Status secondStatus = new Status(Status.Type.UNAUTHORIZED, "sample message");
    BaseResponse secondBase = new BaseResponse(secondStatus);

    assertEquals("equals", firstBase, secondBase);

    Status thirdStatus = new Status(Status.Type.SUCCESS, "other notes");
    BaseResponse thirdBase = new BaseResponse(thirdStatus);
    assertNotEquals(firstBase, thirdBase);
  }

  @Test public void shouldSerializeAndDeserialize() throws IOException {
    BaseResponse sourceBase =
        new BaseResponse(new Status(Status.Type.UNAUTHORIZED, "sample message"));

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writer().writeValueAsString(sourceBase);
    BaseResponse targetBase = mapper.reader().forType(BaseResponse.class).readValue(json);

    assertEquals("base-response", sourceBase, targetBase);
  }

}
