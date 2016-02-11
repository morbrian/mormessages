package morbrian.mormessages.controller;


import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class) public class SubscriptionManagerTest {

  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();

  @Inject private SubscriptionManager subscriptionManager;

  @Deployment public static Archive<?> createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Test public void shouldCreateDeleteInactiveSubscription() {
    String forumUuid = UUID.randomUUID().toString();
    String username = configProvider.getUsername();

    int totalBeforeCount = subscriptionManager.getSubscriptionCount();
    int topicBeforeCount = subscriptionManager.getActiveSubscriptionCountForTopic(forumUuid);
    int userBeforeCount = subscriptionManager.getSubscriptionCountForUsername(username);
    int totalActiveBeforeCount = subscriptionManager.getActiveSubscriptionCount();
    int topicActiveBeforeCount = subscriptionManager.getActiveSubscriptionCountForTopic(forumUuid);

    Subscription subscription = subscriptionManager.createSubscription(forumUuid, username);
    assertEquals("subscription username", username, subscription.getUserIdentity());
    assertEquals("subscrption topic", forumUuid, subscription.getTopicId());
    int totalAfterCount = subscriptionManager.getSubscriptionCount();
    int topicAfterCount = subscriptionManager.getSubscriptionCountForTopic(forumUuid);
    int userAfterCount = subscriptionManager.getSubscriptionCountForUsername(username);
    int totalActiveAfterCount = subscriptionManager.getActiveSubscriptionCount();
    int topicActiveAfterCount = subscriptionManager.getActiveSubscriptionCountForTopic(forumUuid);

    assertEquals("total subscription count", totalBeforeCount + 1, totalAfterCount);
    assertEquals("topic subscription count", topicBeforeCount + 1, topicAfterCount);
    assertEquals("user subscription count", userBeforeCount + 1, userAfterCount);
    assertEquals("total active subscription count", totalActiveBeforeCount, totalActiveAfterCount);
    assertEquals("topic active subscription count", topicActiveBeforeCount, topicActiveAfterCount);

    subscriptionManager.deleteSubscription(subscription.getSubscriptionId());
    int totalResetCount = subscriptionManager.getSubscriptionCount();
    int topicResetCount = subscriptionManager.getSubscriptionCountForTopic(forumUuid);
    int userResetCount = subscriptionManager.getSubscriptionCountForUsername(username);
    int totalActiveResetCount = subscriptionManager.getActiveSubscriptionCount();
    int topicActiveResetCount = subscriptionManager.getActiveSubscriptionCountForTopic(forumUuid);
    assertEquals("total subscription count", totalBeforeCount, totalResetCount);
    assertEquals("topic subscription count", topicBeforeCount, topicResetCount);
    assertEquals("user subscription count", userBeforeCount, userResetCount);
    assertEquals("total active subscription count", totalActiveBeforeCount, totalActiveResetCount);
    assertEquals("topic active subscription count", topicActiveBeforeCount, topicActiveResetCount);
  }

  @Test public void shouldCreateDeleteActiveSubscription() {
    String forumUuid = UUID.randomUUID().toString();
    String username = configProvider.getUsername();

    int totalBeforeCount = subscriptionManager.getSubscriptionCount();
    int topicBeforeCount = subscriptionManager.getActiveSubscriptionCountForTopic(forumUuid);
    int userBeforeCount = subscriptionManager.getSubscriptionCountForUsername(username);
    int totalActiveBeforeCount = subscriptionManager.getActiveSubscriptionCount();
    int topicActiveBeforeCount = subscriptionManager.getActiveSubscriptionCountForTopic(forumUuid);

    Subscription subscription = subscriptionManager.createSubscription(forumUuid, username);
    assertEquals("subscription username", username, subscription.getUserIdentity());
    assertEquals("subscrption topic", forumUuid, subscription.getTopicId());

    Session mockSession = new MockSession();
    subscriptionManager.activateSubscription(mockSession, subscription.getSubscriptionId());
    int totalAfterCount = subscriptionManager.getSubscriptionCount();
    int topicAfterCount = subscriptionManager.getSubscriptionCountForTopic(forumUuid);
    int userAfterCount = subscriptionManager.getSubscriptionCountForUsername(username);
    int totalActiveAfterCount = subscriptionManager.getActiveSubscriptionCount();
    int topicActiveAfterCount = subscriptionManager.getActiveSubscriptionCountForTopic(forumUuid);

    assertEquals("total subscription count", totalBeforeCount + 1, totalAfterCount);
    assertEquals("topic subscription count", topicBeforeCount + 1, topicAfterCount);
    assertEquals("user subscription count", userBeforeCount + 1, userAfterCount);
    assertEquals("total active subscription count", totalActiveBeforeCount + 1,
        totalActiveAfterCount);
    assertEquals("topic active subscription count", topicActiveBeforeCount + 1,
        topicActiveAfterCount);

    subscriptionManager.deleteSubscription(subscription.getSubscriptionId());
    int totalResetCount = subscriptionManager.getSubscriptionCount();
    int topicResetCount = subscriptionManager.getSubscriptionCountForTopic(forumUuid);
    int userResetCount = subscriptionManager.getSubscriptionCountForUsername(username);
    int totalActiveResetCount = subscriptionManager.getActiveSubscriptionCount();
    int topicActiveResetCount = subscriptionManager.getActiveSubscriptionCountForTopic(forumUuid);
    assertEquals("total subscription count", totalBeforeCount, totalResetCount);
    assertEquals("topic subscription count", topicBeforeCount, topicResetCount);
    assertEquals("user subscription count", userBeforeCount, userResetCount);
    assertEquals("total active subscription count", totalActiveBeforeCount, totalActiveResetCount);
    assertEquals("topic active subscription count", topicActiveBeforeCount, topicActiveResetCount);
  }
}
