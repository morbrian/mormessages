package morbrian.mormessages.controller;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import morbrian.mormessages.dataformat.CalendarDeserializer;
import morbrian.mormessages.dataformat.CalendarSerializer;
import morbrian.mormessages.dataformat.DurationAsLongMillisSerializer;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true) public class Subscription {
  private String subscriptionId;
  private String userIdentity;
  private String topicId;
  private Calendar expirationTime;
  private Duration duration;

  public Subscription(String userIdentity, String topicId, String subscriptionId) {
    this.subscriptionId = subscriptionId;
    this.userIdentity = userIdentity;
    this.topicId = topicId;
    this.duration = Duration.ofSeconds(SubscriptionManager.DEFAULT_DURATION_SECONDS);
    this.expirationTime = Calendar.getInstance();
    this.expirationTime.setTime(Date.from(Instant.now().plusSeconds(duration.getSeconds())));
  }

  public Subscription(String userIdentity, String topicId) {
    this(userIdentity, topicId, UUID.randomUUID().toString());
  }

  public Subscription(String userIdentity, String topicId, Calendar expirationTime) {
    this(userIdentity, topicId);
    if (expirationTime != null) {
      this.expirationTime = expirationTime;
      this.duration =
          Duration.ofMillis(expirationTime.getTimeInMillis() - System.currentTimeMillis());
    }
  }

  public Subscription(String userIdentity, String topicId, Duration duration) {
    this(userIdentity, topicId);
    if (duration != null) {
      this.duration = duration;
      this.expirationTime = Calendar.getInstance();
      this.expirationTime.setTime(Date.from(Instant.now().plusSeconds(duration.getSeconds())));
    }
  }

  public Subscription(String userIdentity, String topicId, Calendar expiration, Duration duration) {
    this(userIdentity, topicId);
    this.duration = duration;
    this.expirationTime = expiration;
  }

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public static Subscription jsonCreator(@JsonProperty(value = "id") String subscriptionId,
      @JsonProperty(value = "userIdentity") String userIdentity,
      @JsonProperty(value = "topicId") String topicId,
      @JsonProperty(value = "durationMillis") long durationMillis,
      @JsonProperty(value = "expiration") Calendar expiration) {
    Subscription subscription =
        new Subscription(userIdentity, topicId, expiration, Duration.ofMillis(durationMillis));
    subscription.subscriptionId = subscriptionId;
    return subscription;
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }

  public String getTopicId() {
    return topicId;
  }

  public String getUserIdentity() {
    return userIdentity;
  }

  public Subscription renew(Duration duration) {
    Subscription extended = new Subscription(userIdentity, topicId, duration);
    extended.subscriptionId = this.subscriptionId;
    return extended;
  }

  @JsonSerialize(using = CalendarSerializer.class)
  @JsonDeserialize(using = CalendarDeserializer.class) public Calendar getExpiration() {
    return expirationTime;
  }

  @JsonSerialize(using = DurationAsLongMillisSerializer.class)
  @JsonProperty(value = "durationMillis") public Duration getDuration() {
    return duration;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Subscription that = (Subscription) o;
    return Objects.equals(subscriptionId, that.subscriptionId) &&
        Objects.equals(getUserIdentity(), that.getUserIdentity()) &&
        Objects.equals(getTopicId(), that.getTopicId()) &&
        Objects.equals(expirationTime, that.expirationTime) &&
        Objects.equals(getDuration(), that.getDuration());
  }

  @Override public int hashCode() {
    return Objects
        .hash(subscriptionId, getUserIdentity(), getTopicId(), expirationTime, getDuration());
  }
}
