package morbrian.websockets.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ForumEntityTest {

  @Test public void shouldSerializeAndDeserialize() throws IOException {
    ForumEntity sourceForum = createRandomNewForum();
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writer().writeValueAsString(sourceForum);
    ForumEntity targetForum = mapper.reader().forType(ForumEntity.class).readValue(json);

    verifyEqualityOfAllAttributes("forum-response", sourceForum, targetForum);
  }

  @Test public void shouldDeserializeFromString() throws Exception {
    String sampleData = "{\"title\":\"a3eb0997aadb74a4f48abf7ba2ae842b8a7c7\",\"description\":\"aa5496740af464a42d6a8a12a730b234d9251\",\"imageUrl\":\"a834f1cb7afb9fa4855a85f7ab7500e5b342a\",\"createdTime\":null,\"modifiedTime\":null,\"createdByUid\":\"MOR\",\"modifiedByUid\":\"BRIAN\",\"id\":null}";
    ObjectMapper mapper = new ObjectMapper();
    ForumEntity targetForum = mapper.reader().forType(ForumEntity.class).readValue(sampleData);
    assertEquals("string equality", sampleData, mapper.writeValueAsString(targetForum));
  }

  public static ForumEntity createRandomNewForum() {
    ForumEntity expectedForum =
        new ForumEntity(ContainerConfigurationProvider.randomAlphaNumericString(),
            ContainerConfigurationProvider.randomAlphaNumericString(),
            ContainerConfigurationProvider.randomAlphaNumericString());
    return expectedForum;
  }

  public static void verifyEqualityOfAllAttributes(String tag, ForumEntity expectedForum,
      ForumEntity forum) {
    assertEquals(tag + " forum.id", expectedForum.getId(), forum.getId());
    assertEquals(tag + " forum.getCreatedByUid", expectedForum.getCreatedByUid(),
        forum.getCreatedByUid());
    assertEquals(tag + " forum.getCreatedTime", expectedForum.getCreatedTime(),
        forum.getCreatedTime());
    assertEquals(tag + " forum.getModifiedByUid", expectedForum.getModifiedByUid(),
        forum.getModifiedByUid());
    assertEquals(tag + " forum.getModifiedTime", expectedForum.getModifiedTime(),
        forum.getModifiedTime());
    assertEquals(tag + " forum.getTitle", expectedForum.getTitle(), forum.getTitle());
    assertEquals(tag + " forum.getDescription", expectedForum.getDescription(),
        forum.getDescription());
    assertEquals(tag + " forum.getImageUrl", expectedForum.getImageUrl(), forum.getImageUrl());
  }
}
