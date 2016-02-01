package morbrian.mormessages.controller;

import morbrian.mormessages.event.Created;
import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.MessageEntity;
import morbrian.mormessages.persistence.Persistence;
import morbrian.mormessages.persistence.Repository;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.security.Principal;
import java.util.List;

@ApplicationScoped public class ControllerImplJpa implements Controller {

  @Inject private Persistence persistence;
  @Inject private Repository repository;
  @Inject private Principal principal;
  @Inject private Event<MessageEntity> messageEventSrc;
  @Inject private Logger logger;

  @Override public List<ForumEntity> listForums() {
    return repository.listForums();
  }

  @Override
  public List<ForumEntity> listForums(Integer offset, Integer resultSize, Long greaterThan) {
    return repository.listForums(offset, resultSize, greaterThan);
  }

  @Override public ForumEntity getForumById(Long forumId) {
    return repository.findForumById(forumId);
  }

  public ForumEntity getForumByTitle(String title) {
    return repository.findForumByTitle(title);
  }

  @Override public boolean titleExists(String title) {
    try {
      getForumByTitle(title);
      return true;
    } catch (NoResultException exc) {
      return false;
    }
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

  @Override public List<MessageEntity> listMessagesInForum(Long forumId) {
    return repository.listMessagesInForum(forumId);
  }

  @Override
  public List<MessageEntity> listMessagesInForum(Long forumId, Integer offset, Integer resultSize,
      Long greaterThan) {
    return repository.listMessagesInForum(forumId, offset, resultSize, greaterThan);
  }

  @Override public MessageEntity postMessageToForum(MessageEntity message, Long forumId) {
    message.setForumId(forumId);
    MessageEntity createdMessage = persistence.createMessage(message);
    messageEventSrc.select(Created.SELECTOR).fire(createdMessage);
    return createdMessage;
  }

}
