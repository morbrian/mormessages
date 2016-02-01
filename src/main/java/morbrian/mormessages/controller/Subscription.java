package morbrian.mormessages.controller;


import javax.websocket.Session;
import java.util.Objects;

public class Subscription {
  private Session session;
  private String userIdentity;
  private Long topicId;

  public Subscription(Session session, String userIdentity, Long topicId) {
    this.session = session;
    this.userIdentity = userIdentity;
    this.topicId = topicId;
  }

  public Long getTopicId() {
    return topicId;
  }

  public Session getSession() {
    return session;
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
    return Objects.equals(getSession(), that.getSession()) &&
        Objects.equals(getUserIdentity(), that.getUserIdentity()) &&
        Objects.equals(getTopicId(), that.getTopicId());
  }

  @Override public int hashCode() {
    return Objects.hash(getSession(), getUserIdentity(), getTopicId());
  }
}
