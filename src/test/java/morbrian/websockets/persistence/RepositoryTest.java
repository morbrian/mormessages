package morbrian.websockets.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.ForumEntityTest;
import morbrian.websockets.model.MessageEntity;
import morbrian.websockets.model.MessageEntityTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(Arquillian.class) public class RepositoryTest {

  private static final int FORUM_COUNT = 5;
  private static final int MESSAGE_COUNT = 10;

  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();
  private static Map<Long, ForumEntity> forums;
  private static Map<Long, List<MessageEntity>> forumMessages;
  @Inject private Repository repository;
  @Inject private Persistence persistence;

  @Deployment public static Archive<?> createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Before public void setup() {
    assertNotNull(repository);
    assertNotNull(persistence);
    forums = new HashMap<>();
    forumMessages = new HashMap<>();
    for (int forumIndex = 0; forumIndex < FORUM_COUNT; forumIndex++) {
      ForumEntity forum = persistence.createForum(ForumEntityTest.createRandomNewForum());
      forums.put(forum.getId(), forum);
      List<MessageEntity> messages = new ArrayList<>();
      for (int messageIndex = 0; messageIndex < MESSAGE_COUNT; messageIndex++) {
        messages.add(
            persistence.createMessage(MessageEntityTest.createRandomNewMessage(forum.getId())));
      }
      forumMessages.put(forum.getId(), messages);
    }
  }

  @After public void teardown() {
    for (ForumEntity forum : forums.values()) {
      for (MessageEntity message : forumMessages.get(forum.getId())) {
        persistence.removeEntity(message);
      }
      persistence.removeEntity(forum);
    }
    forums = null;
    forumMessages = null;
  }

  @Test public void shouldFindForumById() throws JsonProcessingException {
    for (ForumEntity expectedForum : forums.values()) {
      ForumEntity foundForum = repository.findForumById(expectedForum.getId());
      ForumEntityTest.verifyEqualityOfAllAttributes("found", expectedForum, foundForum);
    }
  }

  @Test public void shouldFindAllForumsOrderedByCreatedTime() throws JsonProcessingException {
    List<ForumEntity> sortedForums = new ArrayList<>(forums.values());
    sortedForums.sort(Comparator.comparing(ForumEntity::getCreatedTime));
    List<ForumEntity> forumList = repository.findAllForumsOrderedByCreatedTime();
    assertEquals("equal list sizes", sortedForums.size(), forumList.size());
    for (int i = 0; i < sortedForums.size(); i++) {
      ForumEntityTest
          .verifyEqualityOfAllAttributes("item-" + i, sortedForums.get(i), forumList.get(i));
    }
  }

  @Test public void shouldFindMessagesForForum() throws JsonProcessingException {
    for (ForumEntity forum : forums.values()) {
      List<MessageEntity> expectedMessages = forumMessages.get(forum.getId());
      expectedMessages.sort(Comparator.comparing(MessageEntity::getCreatedTime));
      List<MessageEntity> foundMessages =
          repository.findMessagesForForumOrderedByCreatedTime(forum.getId());
      assertEquals("message count", expectedMessages.size(), foundMessages.size());
      for (int i = 0; i < expectedMessages.size(); i++) {
        MessageEntityTest.verifyEqualityOfAllAttributes("messages", expectedMessages.get(i),
            foundMessages.get(i));
      }
    }
  }

  @Test public void shouldFindMessageById() throws JsonProcessingException {
    for (ForumEntity forum : forums.values()) {
      for (MessageEntity expectedMessage : forumMessages.get(forum.getId())) {
        MessageEntity foundMessage = repository.findMessageById(expectedMessage.getId());
        assertEquals("messages belongs to correct forum", forum.getId(), foundMessage.getForumId());
        MessageEntityTest
            .verifyEqualityOfAllAttributes("found message", expectedMessage, foundMessage);
      }
    }
  }

  @Test public void shouldFindMessagesWithRangeExclusive() {
    assertTrue("pre-test condition", MESSAGE_COUNT >= 10);
    Long forumId = new ArrayList<>(forums.keySet()).get(0);
    List<MessageEntity> messages = forumMessages.get(forumId);
    List<Long> messageIds =
        messages.stream().map(MessageEntity::getId).collect(Collectors.toList());

    int offset = 3;
    int expectedCount = offset * 2 - 1;
    int lowIndex = offset;
    int highIndex = messageIds.size() - offset;
    Long lowId = messageIds.get(lowIndex);
    Long highId = messageIds.get(highIndex);

    List<MessageEntity> foundMessages =
        repository.findMessagesForForumWithIdRangeOrderedByCreatedTime(forumId, lowId, highId);
    assertEquals("found message count", expectedCount, foundMessages.size());
    List<MessageEntity> verifiedMessages =
        messages.stream().filter(m -> m.getId() < lowId || m.getId() > highId)
            .collect(Collectors.toList());
    assertEquals("verified count", foundMessages.size(), verifiedMessages.size());
  }
}
