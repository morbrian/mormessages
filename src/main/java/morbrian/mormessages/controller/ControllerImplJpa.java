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
import java.util.Calendar;
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
  public List<ForumEntity> listForums(Integer offset, Integer resultSize, Calendar greaterThan) {
    return repository.listForums(offset, resultSize, greaterThan);
  }

  @Override public ForumEntity getForumByUuid(String forumUuid) {
    return repository.findForumByUuid(forumUuid);
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

  @Override public boolean uuidExists(String uuid) {
    try {
      getForumByUuid(uuid);
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

  @Override public void deleteForum(String forumUuid) {
    persistence.removeEntity(repository.findForumByUuid(forumUuid));
  }

  @Override public MessageEntity getMessageByUuid(String messageUuid) {
    return repository.findMessageByUuid(messageUuid);
  }

  @Override public List<MessageEntity> listMessagesInForum(String forumUuid) {
    return repository.listMessagesInForum(forumUuid);
  }

  @Override public List<MessageEntity> listMessagesInForum(String forumUuid, Integer offset,
      Integer resultSize, Calendar greaterThan) {
    return repository.listMessagesInForum(forumUuid, offset, resultSize, greaterThan);
  }

  @Override public MessageEntity postMessageToForum(MessageEntity message, String forumUuid) {
    message.setForumUuid(forumUuid);
    MessageEntity createdMessage = persistence.createMessage(message);
    messageEventSrc.select(Created.SELECTOR).fire(createdMessage);
    return createdMessage;
  }

}
