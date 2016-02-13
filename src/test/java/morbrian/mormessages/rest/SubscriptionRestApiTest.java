package morbrian.mormessages.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.mormessages.controller.Controller;
import morbrian.mormessages.controller.Subscription;
import morbrian.mormessages.controller.SubscriptionManager;
import morbrian.mormessages.model.Credentials;
import morbrian.mormessages.model.ForumEntity;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class) public class SubscriptionRestApiTest {

  private static final int SAMPLE_DATA_COUNT = 10;
  private static final int LARGE_DATA_COUNT = 50;
  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();
  private static final String AUTH_REST_PATH = "api/rest/auth/";
  private static final String SUBSCRIPTION_REST_PATH = "api/rest/subscription/";
  private static Logger logger = LoggerFactory.getLogger(SubscriptionRestApiTest.class);
  @Rule public final ExpectedException exception = ExpectedException.none();
  @Inject Controller controller;
  @Inject SubscriptionManager subscriptionManager;
  @ArquillianResource private URL webappUrl;
  private SimpleClient client;

  @Deployment public static Archive<?> createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Before public void setup() {
    client = new SimpleClient(webappUrl.toString(), configProvider.getPasswordAuthentication());
    client.post(getCredentials(), Arrays.asList(AUTH_REST_PATH, "login"), null).close();
  }

  @After public void teardown() {
    client = null;
    for (ForumEntity forum : controller.listForums()) {
      controller.deleteForum(forum.getUuid());
    }
    for (Subscription subscription : subscriptionManager
        .listSubscriptions(configProvider.getUsername())) {
      subscriptionManager.deleteSubscription(subscription.getSubscriptionId());
    }
  }

  @Test public void shouldRespondWithEmptySubscriptionList() {
    // test
    Response response = client.get(Arrays.asList(SUBSCRIPTION_REST_PATH), null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    List<Subscription> responseSubscriptions =
        response.readEntity(new GenericType<List<Subscription>>() {
        });
    response.close();

    // verify
    assertEquals("subscription count", 0, responseSubscriptions.size());
  }

  @Test public void shouldRespondWithSubscription() {
    String forumUuid = UUID.randomUUID().toString();
    String username = configProvider.getUsername();
    Subscription expectedSubscription = subscriptionManager.createSubscription(forumUuid, username);

    // test
    Response response = client
        .get(Arrays.asList(SUBSCRIPTION_REST_PATH, expectedSubscription.getSubscriptionId()), null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    Subscription actualSubscription = response.readEntity(Subscription.class);
    response.close();

    // verify
    assertEquals("subscription", expectedSubscription, actualSubscription);
  }

  @Test public void shouldCreateSubscription() throws IOException {
    Subscription submitted =
        new Subscription(configProvider.getUsername(), UUID.randomUUID().toString());

    // test
    Response response = client.put(submitted, Arrays.asList(SUBSCRIPTION_REST_PATH), null);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
    String subscriptionString = response.readEntity(String.class);
    ObjectMapper mapper = new ObjectMapper();
    Subscription created = mapper.readValue(subscriptionString, Subscription.class);
    response.close();

    // verify
    assertEquals("topicId", submitted.getTopicId(), created.getTopicId());
    assertEquals("userIdentity", submitted.getUserIdentity(), created.getUserIdentity());
    assertNotNull("subscriptionId", created.getSubscriptionId());
  }

  @Test public void shouldRenewSubscription() throws IOException {
    Subscription submitted =
        new Subscription(configProvider.getUsername(), UUID.randomUUID().toString());

    // prepare
    Response response = client.put(submitted, Arrays.asList(SUBSCRIPTION_REST_PATH), null);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
    String subscriptionString = response.readEntity(String.class);
    ObjectMapper mapper = new ObjectMapper();
    Subscription created = mapper.readValue(subscriptionString, Subscription.class);
    response.close();

    // test
    long extendByMillis = 1000;
    Subscription extension = created.renew(Duration.ofMillis(extendByMillis));
    response = client
        .post(extension, Arrays.asList(SUBSCRIPTION_REST_PATH, extension.getSubscriptionId()),
            null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    Subscription renewed = response.readEntity(Subscription.class);
    response.close();

    assertEquals("renewed", extension, renewed);
  }

  @Test public void shouldDeleteSubscription() throws IOException {
    Subscription submitted =
        new Subscription(configProvider.getUsername(), UUID.randomUUID().toString());

    int initialCount =
        subscriptionManager.getSubscriptionCountForUsername(configProvider.getUsername());

    // prepare
    Response response = client.put(submitted, Arrays.asList(SUBSCRIPTION_REST_PATH), null);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
    String subscriptionString = response.readEntity(String.class);
    ObjectMapper mapper = new ObjectMapper();
    Subscription created = mapper.readValue(subscriptionString, Subscription.class);
    response.close();

    int subscribedCount =
        subscriptionManager.getSubscriptionCountForUsername(configProvider.getUsername());
    assertEquals("subscribed count", initialCount + 1, subscribedCount);

    // test
    response =
        client.delete(Arrays.asList(SUBSCRIPTION_REST_PATH, created.getSubscriptionId()), null);
    assertThat("success or no content", response.getStatus(),
        either(equalTo(Response.Status.OK.getStatusCode()))
            .or(equalTo(Response.Status.NO_CONTENT.getStatusCode())));
    response.close();

    int finalCount =
        subscriptionManager.getSubscriptionCountForUsername(configProvider.getUsername());
    assertEquals("final count", initialCount, finalCount);
  }

  private Credentials getCredentials() {
    PasswordAuthentication auth = configProvider.getPasswordAuthentication();
    return new Credentials(auth.getUserName(), new String(auth.getPassword()));
  }

}
