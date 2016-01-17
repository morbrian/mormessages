package morbrian.websockets.controller;

import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.MessageEntity;

import java.util.List;

public interface Controller {

  List<ForumEntity> listForums();

  List<ForumEntity> listForums(Integer offset, Integer resultSize);

  ForumEntity getForumById(Long forumId);

  boolean titleExists(String title);

  ForumEntity modifyForum(ForumEntity forum);

  ForumEntity createForum(ForumEntity forum);

  void deleteForum(Long forumId);

  MessageEntity getMessageById(Long messageId);

  List<MessageEntity> listMessagesInForum(Long forumId);

  List<MessageEntity> listMessagesInForum(Long forumId, Integer offset, Integer resultSize);

  MessageEntity postMessageToForum(MessageEntity message, Long forumId);

}
