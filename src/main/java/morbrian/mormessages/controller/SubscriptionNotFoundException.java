package morbrian.mormessages.controller;

public class SubscriptionNotFoundException extends Exception {

  public SubscriptionNotFoundException(String subscriptionId) {
    super(subscriptionId);
  }
}
