package morbrian.mormessages.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.mormessages.model.MessageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public class MessageEntityDecoder implements Decoder.Text<MessageEntity> {

  private Logger logger = LoggerFactory.getLogger(MessageEntityDecoder.class);

  // create a Message object from JSON
  @Override public MessageEntity decode(String msg) throws DecodeException {
    logger.info("Decoding: " + msg);
    ObjectMapper mapper = new ObjectMapper();
    MessageEntity message = null;
    try {
      return mapper.readValue(msg, MessageEntity.class);
    } catch (IOException e) {
      throw new DecodeException(msg, "failed to process message string into MessageEntity object",
          e);
    }
  }

  @Override public boolean willDecode(String msg) {
    return true;
  }

  @Override public void destroy() {
  }

  @Override public void init(EndpointConfig config) {
  }

}
