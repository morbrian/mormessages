package morbrian.websockets.controller;

import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.MessageEntity;

import java.util.List;

public interface Controller {

  List<ForumEntity> forumList();

  ForumEntity getForumById(Long forumId);

  ForumEntity modifyForum(ForumEntity forum);

  ForumEntity createForum(ForumEntity forum);

  void deleteForum(Long forumId);

  MessageEntity getMessageById(Long messageId);

  List<MessageEntity> messageList(Long forumId);

  // TODO: look into options for mapping http queries to generic filter, but all we need right now is this one anyway
  List<MessageEntity> messageListFilteredById(Long forumId, Long lowId, Long highId);

  MessageEntity postMessageToForum(MessageEntity message, Long forumId);

}
