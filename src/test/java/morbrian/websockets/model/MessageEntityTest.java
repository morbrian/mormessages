package morbrian.websockets.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MessageEntityTest {

  public static MessageEntity createRandomNewMessage(Long forumId) {
    return new MessageEntity(ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString(), forumId);
  }

  public static void verifyEqualityOfAllAttributes(String tag, MessageEntity expectedMessage,
      MessageEntity message) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    assertEquals(tag + " message.id", expectedMessage.getId(), message.getId());
    assertEquals(tag + " message.getCreatedByUid", expectedMessage.getCreatedByUid(),
        message.getCreatedByUid());
    assertEquals(tag + " message.getCreatedTime",
        mapper.writeValueAsString(expectedMessage.getCreatedTime()),
        mapper.writeValueAsString(message.getCreatedTime()));
    assertEquals(tag + " message.getModifiedByUid", expectedMessage.getModifiedByUid(),
        message.getModifiedByUid());
    assertEquals(tag + " message.getModifiedTime",
        mapper.writeValueAsString(expectedMessage.getModifiedTime()),
        mapper.writeValueAsString(message.getModifiedTime()));
    assertEquals(tag + " message.getText", expectedMessage.getText(), message.getText());
    assertEquals(tag + " message.getImageUrl", expectedMessage.getImageUrl(),
        message.getImageUrl());
    assertEquals(tag + " message.getForumId", expectedMessage.getForumId(), message.getForumId());
  }

  @Test public void shouldSerializeAndDeserialize() throws IOException {
    MessageEntity sourceMessage = createRandomNewMessage(1l);
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writer().writeValueAsString(sourceMessage);
    MessageEntity targetMessage = mapper.reader().forType(MessageEntity.class).readValue(json);

    verifyEqualityOfAllAttributes("message-response", sourceMessage, targetMessage);
  }

  //  @Test public void shouldDeserializeFromString() throws Exception {
  //    String sampleData =
  //        "{\"title\":\"a3eb0997aadb74a4f48abf7ba2ae842b8a7c7\",\"description\":\"aa5496740af464a42d6a8a12a730b234d9251\",\"imageUrl\":\"a834f1cb7afb9fa4855a85f7ab7500e5b342a\",\"createdTime\":null,\"modifiedTime\":null,\"createdByUid\":\"MOR\",\"modifiedByUid\":\"BRIAN\",\"id\":null}";
  //    ObjectMapper mapper = new ObjectMapper();
  //    ForumEntity targetForum = mapper.reader().forType(ForumEntity.class).readValue(sampleData);
  //    assertEquals("string equality", sampleData, mapper.writeValueAsString(targetForum));
  //  }
}
