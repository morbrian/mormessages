package morbrian.mormessages.controller;

import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.MessageEntity;

import java.util.Calendar;
import java.util.List;

public interface Controller {

  List<ForumEntity> listForums();

  List<ForumEntity> listForums(Integer offset, Integer resultSize, Calendar greaterThan);

  ForumEntity getForumByUuid(String forumUuid);

  boolean titleExists(String title);

  boolean uuidExists(String uuid);

  ForumEntity modifyForum(ForumEntity forum);

  ForumEntity createForum(ForumEntity forum);

  void deleteForum(String forumUuid);

  MessageEntity getMessageByUuid(String messageUuid);

  List<MessageEntity> listMessagesInForum(String forumUuid);

  List<MessageEntity> listMessagesInForum(String forumUuid, Integer offset, Integer resultSize,
      Calendar greaterThan);

  MessageEntity postMessageToForum(MessageEntity message, String forumUuid);

  List<Subscription> listSubscriptions();

  Subscription getSubscription(String subscriptionId);

  void deleteSubscription(String subscriptionId);
}
