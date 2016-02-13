package morbrian.mormessages.controller;

import javax.websocket.Session;

public class SubscriptionActivator {

  private Session session;
  private String subscriptionId;

  public SubscriptionActivator(Session session, String subscriptionId) {
    this.session = session;
    this.subscriptionId = subscriptionId;
  }

  public Session getSession() {
    return session;
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }
}
