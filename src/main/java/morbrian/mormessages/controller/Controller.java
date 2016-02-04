package morbrian.mormessages.controller;

import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.MessageEntity;

import java.util.List;

public interface Controller {

  List<ForumEntity> listForums();

  List<ForumEntity> listForums(Integer offset, Integer resultSize, Long greaterThan);

  ForumEntity getForumByUuid(String forumUuid);

  boolean titleExists(String title);

  boolean uuidExists(String uuid);

  ForumEntity modifyForum(ForumEntity forum);

  ForumEntity createForum(ForumEntity forum);

  void deleteForum(String forumUuid);

  MessageEntity getMessageByUuid(String messageUuid);

  List<MessageEntity> listMessagesInForum(String forumUuid);

  List<MessageEntity> listMessagesInForum(String forumUuid, Integer offset, Integer resultSize,
      Long greaterThan);

  MessageEntity postMessageToForum(MessageEntity message, String forumUuid);

}
