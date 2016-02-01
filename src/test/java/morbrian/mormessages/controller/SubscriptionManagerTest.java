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

  @Test public void testSubscribeUnsubscribe() {
    Long forumId = 1L;
    Session mockSession = new MockSession();
    String sessionId = mockSession.getId();

    Subscription sampleSubscription = new Subscription(mockSession, "nobody", forumId);
    int preSessionCount = subscriptionManager.sessionsForTopic(forumId).size();

    subscriptionManager.subscribe(sampleSubscription);
    int postSessionCount = subscriptionManager.sessionsForTopic(forumId).size();
    assertEquals("subscribed session count", preSessionCount + 1, postSessionCount);

    subscriptionManager.unsubscribe(sampleSubscription);
    int closedSessionCount = subscriptionManager.sessionsForTopic(forumId).size();
    assertEquals("unsubscribed session count", preSessionCount, closedSessionCount);
  }

}
