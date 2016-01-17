package morbrian.websockets.controller;

import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.MessageEntity;
import morbrian.websockets.persistence.Persistence;
import morbrian.websockets.persistence.Repository;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.security.Principal;
import java.util.List;

@ApplicationScoped public class ControllerImplJpa implements Controller {

  @Inject private Persistence persistence;
  @Inject private Repository repository;
  @Inject private Principal principal;
  @Inject private Logger logger;

  @Override public List<ForumEntity> listForums() {
    return repository.listForums();
  }

  @Override public List<ForumEntity> listForums(Integer offset, Integer resultSize) {
    return repository.listForums(offset, resultSize);
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
  public List<MessageEntity> listMessagesInForum(Long forumId, Integer offset, Integer resultSet) {
    return repository.listMessagesInForum(forumId, offset, resultSet);
  }

  @Override public MessageEntity postMessageToForum(MessageEntity message, Long forumId) {
    message.setForumId(forumId);
    return persistence.createMessage(message);
  }
}
