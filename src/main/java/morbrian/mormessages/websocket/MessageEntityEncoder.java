package morbrian.mormessages.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.mormessages.model.MessageEntity;

import javax.websocket.EncodeException;
import javax.websocket.Encoder.Text;
import javax.websocket.EndpointConfig;

public class MessageEntityEncoder implements Text<MessageEntity> {

  @Override public void destroy() {

  }

  @Override public void init(EndpointConfig config) {

  }

  @Override public String encode(MessageEntity message) throws EncodeException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(message);
    } catch (JsonProcessingException e) {
      throw new EncodeException(message, "failed to convert MessageEntity into json string", e);
    }
  }
}
