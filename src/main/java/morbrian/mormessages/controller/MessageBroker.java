package morbrian.mormessages.controller;

import morbrian.mormessages.event.Created;
import morbrian.mormessages.model.MessageEntity;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.List;

@ApplicationScoped public class MessageBroker {

  @Inject private Logger logger;
  @Inject private SubscriptionManager subscriptionManager;

  public void publishMessage(@Observes @Created MessageEntity message) {
    String topicId = message.getForumUuid();
    List<Session> sessions = subscriptionManager.sessionsForTopic(topicId);
    if (logger.isDebugEnabled()) {
      logger.debug(
          "publish message from user(" + message.getCreatedByUid() + ") " + "on topic(" + topicId
              + ") to count(" + sessions.size() + ") subscribers");
    }
    for (Session s : sessions) {
      s.getAsyncRemote().sendObject(message);
    }
  }

}
