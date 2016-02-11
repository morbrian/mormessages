package morbrian.mormessages.controller;


import javax.websocket.Session;
import java.util.Objects;
import java.util.UUID;

public class Subscription {
  private String subscriptionId;
  private String userIdentity;
  private String topicId;

  public Subscription(String subscriptionId, String userIdentity, String topicId) {
    this.subscriptionId = subscriptionId;
    this.userIdentity = userIdentity;
    this.topicId = topicId;
  }

  public String getId() {
    return subscriptionId;
  }

  public String getTopicId() {
    return topicId;
  }

  public String getUserIdentity() {
    return userIdentity;
  }


  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Subscription that = (Subscription) o;
    return Objects.equals(subscriptionId, that.subscriptionId) &&
        Objects.equals(getUserIdentity(), that.getUserIdentity()) &&
        Objects.equals(getTopicId(), that.getTopicId());
  }

  @Override public int hashCode() {
    return Objects.hash(subscriptionId, getUserIdentity(), getTopicId());
  }
}
