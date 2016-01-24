package morbrian.mormessages.controller;

import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.MessageEntity;

import java.util.List;

public interface Controller {

  List<ForumEntity> listForums();

  List<ForumEntity> listForums(Integer offset, Integer resultSize, Long greaterThan);

  ForumEntity getForumById(Long forumId);

  boolean titleExists(String title);

  ForumEntity modifyForum(ForumEntity forum);

  ForumEntity createForum(ForumEntity forum);

  void deleteForum(Long forumId);

  MessageEntity getMessageById(Long messageId);

  List<MessageEntity> listMessagesInForum(Long forumId);

  List<MessageEntity> listMessagesInForum(Long forumId, Integer offset, Integer resultSize, Long greaterThan);

  MessageEntity postMessageToForum(MessageEntity message, Long forumId);

}
