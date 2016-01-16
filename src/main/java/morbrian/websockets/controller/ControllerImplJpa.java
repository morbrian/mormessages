package morbrian.websockets.controller;

import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.MessageEntity;
import morbrian.websockets.persistence.Persistence;
import morbrian.websockets.persistence.Repository;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.Principal;
import java.util.List;

@ApplicationScoped public class ControllerImplJpa implements Controller {

  @Inject private Persistence persistence;
  @Inject private Repository repository;
  @Inject private Principal principal;
  @Inject private Logger logger;

  @Override public List<ForumEntity> forumList() {
    return repository.findAllForumsOrderedByCreatedTime();
  }

  @Override public ForumEntity getForumById(Long forumId) {
    return repository.findForumById(forumId);
  }

  @Override public ForumEntity modifyForum(ForumEntity forum) {
    return persistence.updateForum(forum);
  }

  @Override public ForumEntity createForum(ForumEntity forum) {
    return persistence.createForum(forum);
  }

  @Override public void deleteForum(Long forumId) {
    persistence.removeEntity(repository.findForumById(forumId));
  }

  @Override public MessageEntity getMessageById(Long messageId) {
    return repository.findMessageById(messageId);
  }

  @Override public List<MessageEntity> messageList(Long forumId) {
    return repository.findMessagesForForumOrderedByCreatedTime(forumId);
  }

  @Override
  public List<MessageEntity> messageListFilteredById(Long forumId, Long lowId, Long highId) {
    return repository.findMessagesForForumWithIdRangeOrderedByCreatedTime(forumId, lowId, highId);
  }

  @Override public MessageEntity postMessageToForum(MessageEntity message, Long forumId) {
    message.setForumId(forumId);
    return persistence.createMessage(message);
  }
}
