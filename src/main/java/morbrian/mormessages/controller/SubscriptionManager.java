package morbrian.mormessages.controller;

import morbrian.mormessages.event.Closed;
import morbrian.mormessages.event.Opened;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped public class SubscriptionManager {

  @Inject private Logger logger;

  private Map<String, Subscription> idToSubscription =
      Collections.synchronizedMap(new HashMap<>());
  private Map<String, Set<Subscription>> topicToSubscription =
      Collections.synchronizedMap(new HashMap<>());
  private Map<String, Set<Subscription>> userToSubscription =
      Collections.synchronizedMap(new HashMap<>());

  // these are mapped when subscription is active
  private Map<String, Set<Session>> topicToSession =
      Collections.synchronizedMap(new HashMap<>());
  //TODO: we should probably permit multiple subscriptions per session
  //TODO: and we should probably have an envelope class concept
  private Map<String, Subscription> sessionToSubscription =
      Collections.synchronizedMap(new HashMap<>());
  private Map<String, Session> subscriptionToSession =
      Collections.synchronizedMap(new HashMap<>());

  public void onOpenedSubscription(@Observes @Opened final SubscriptionActivator activator) {
    activateSubscription(activator.getSession(), activator.getSubscriptionId());
  }

  public void onClosedSubscription(@Observes @Closed final SubscriptionActivator activator) {
    deactivateSubscription(activator.getSession(), activator.getSubscriptionId());
  }

  public synchronized List<Session> sessionsForTopic(String topicId) {
    Set<Session> sessionSet = topicToSession.get(topicId);
    if (sessionSet == null) {
      return Collections.emptyList();
    } else {
      return new ArrayList(sessionSet);
    }
  }

  public synchronized void activateSubscription(Session session, String subscriptionId) {
    String sessionId = session.getId();
    Subscription subscription = idToSubscription.get(subscriptionId);
    String topicId = subscription.getTopicId();

    // map session by topicId - one-to-one
    sessionToSubscription.put(sessionId, subscription);
    subscriptionToSession.put(subscriptionId, session);

    // map topicId to session - one-to-many
    Set<Session> sessionGroup = topicToSession.get(topicId);
    if (sessionGroup == null) {
      sessionGroup = Collections.synchronizedSet(new HashSet<>());
      topicToSession.put(topicId, sessionGroup);
    }
    sessionGroup.add(session);
  }

  public synchronized Subscription createSubscription(String topicId, String userId) {
    String subscriptionId = UUID.randomUUID().toString();

    // create new subscription object with id
    Subscription subscription = new Subscription(subscriptionId, userId, topicId);

    // map by subscription-id
    idToSubscription.put(subscriptionId, subscription);

    // map to topic - one-to-many
    Set<Subscription> topicGroup = topicToSubscription.get(topicId);
    if (topicGroup == null) {
      topicGroup = Collections.synchronizedSet(new HashSet<>());
      topicToSubscription.put(topicId, topicGroup);
    }
    topicGroup.add(subscription);

    // map to user
    Set<Subscription> userTopics = userToSubscription.get(userId);
    if (userTopics == null) {
      userTopics = Collections.synchronizedSet(new HashSet<>());
      userToSubscription.put(userId, userTopics);
    }
    userTopics.add(subscription);

    return subscription;
  }

  public synchronized void deactivateSubscription(Session session, String subscriptionId) {
    subscriptionToSession.remove(subscriptionId);
    if (session == null) {
      return;
    }

    String sessionId = session.getId();
    Subscription subscription = idToSubscription.get(subscriptionId);
    String topicId = subscription.getTopicId();

    Set<Session> sessionGroup = topicToSession.get(topicId);
    if (sessionGroup != null) {
      sessionGroup.remove(session);
    }

    sessionToSubscription.remove(sessionId);
  }

  public synchronized void deleteSubscription(String subscriptionId) {
    Subscription subscription = idToSubscription.get(subscriptionId);
    String topicId = subscription.getTopicId();
    String userId = subscription.getUserIdentity();

    deactivateSubscription(subscriptionToSession.get(subscriptionId), subscriptionId);

    // map by id - one-to-one
    idToSubscription.remove(subscriptionId);

    // map to topic - one-to-many
    Set<Subscription> topicGroup = topicToSubscription.get(topicId);
    if (topicGroup != null) {
      topicGroup.remove(subscription);
    }

    // map to user
    Set<Subscription> userTopics = userToSubscription.get(userId);
    if (userTopics != null) {
      userTopics.remove(subscription);
    }
  }

  public int getSubscriptionCount() {
    return idToSubscription.size();
  }

  public int getSubscriptionCountForTopic(String topicId) {
    Set<Subscription> subscriptions = topicToSubscription.get(topicId);
    return (subscriptions != null) ? subscriptions.size() : 0;
  }

  public int getSubscriptionCountForUsername(String username) {
    Set<Subscription> subscriptions = userToSubscription.get(username);
    return (subscriptions != null) ? subscriptions.size() : 0;
  }

  public int getActiveSubscriptionCount() {
    assert(subscriptionToSession.size() == sessionToSubscription.size());
    return subscriptionToSession.size();
  }

  public int getActiveSubscriptionCountForTopic(String topicId) {
    Set<Session> sessions = topicToSession.get(topicId);
    return (sessions != null) ? sessions.size() : 0;
  }
}
