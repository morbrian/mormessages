package morbrian.mormessages.controller;

import morbrian.mormessages.event.Closed;
import morbrian.mormessages.event.Opened;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped public class SubscriptionManager {

  @Inject private Logger logger;

  private Map<String, Set<Subscription>> topicToSubscription =
      Collections.synchronizedMap(new HashMap<>());
  private Map<String, Subscription> sessionToSubscription =
      Collections.synchronizedMap(new HashMap<>());
  private Map<String, Set<Subscription>> userToSubscription =
      Collections.synchronizedMap(new HashMap<>());

  public void onOpenedSubscription(@Observes @Opened final Subscription subscription) {
    subscribe(subscription);
  }

  public void onClosedSubscription(@Observes @Closed final Subscription subscription) {
    unsubscribe(subscription);
  }

  public synchronized List<Session> sessionsForTopic(String topicId) {
    Set<Subscription> topicGroup = topicToSubscription.get(topicId);
    if (topicGroup == null) {
      return Collections.emptyList();
    } else {
      return topicGroup.stream().map(Subscription::getSession).collect(Collectors.toList());
    }
  }

  public synchronized void subscribe(Subscription subscription) {
    String topicId = subscription.getTopicId();
    String userId = subscription.getUserIdentity();
    String sessionId = subscription.getSession().getId();

    // map to topic - one-to-many
    Set<Subscription> topicGroup = topicToSubscription.get(topicId);
    if (topicGroup == null) {
      topicGroup = Collections.synchronizedSet(new HashSet<>());
      topicToSubscription.put(topicId, topicGroup);
    }
    topicGroup.add(subscription);

    // map to session - one-to-one
    Subscription sessionSubscription = sessionToSubscription.get(sessionId);
    if (sessionSubscription != null) {
      logger.warn("Subscription already exists for sessionId(" + sessionId + "), user(" + userId
          + "), topic(" + topicId + ")");
    }
    sessionToSubscription.put(sessionId, subscription);

    // map to user
    Set<Subscription> userTopics = userToSubscription.get(userId);
    if (userTopics == null) {
      userTopics = Collections.synchronizedSet(new HashSet<>());
      userToSubscription.put(userId, userTopics);
    }
    userTopics.add(subscription);
  }

  public synchronized void unsubscribe(Subscription subscription) {
    String topicId = subscription.getTopicId();
    String userId = subscription.getUserIdentity();
    String sessionId = subscription.getSession().getId();

    // map to topic - one-to-many
    Set<Subscription> topicGroup = topicToSubscription.get(topicId);
    if (topicGroup != null) {
      topicGroup.remove(subscription);
    }

    // map to session - one-to-one
    sessionToSubscription.remove(sessionId);

    // map to user
    Set<Subscription> userTopics = userToSubscription.get(userId);
    if (userTopics != null) {
      userTopics.remove(subscription);
    }
  }

}
