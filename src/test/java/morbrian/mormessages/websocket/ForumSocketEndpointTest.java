package morbrian.mormessages.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.mormessages.controller.Controller;
import morbrian.mormessages.controller.Subscription;
import morbrian.mormessages.controller.SubscriptionManager;
import morbrian.mormessages.model.Credentials;
import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.ForumEntityTest;
import morbrian.mormessages.model.MessageEntity;
import morbrian.mormessages.model.MessageEntityTest;
import morbrian.mormessages.rest.ForumRestApi;
import morbrian.mormessages.rest.PasswordAuthenticator;
import morbrian.mormessages.rest.SimpleClient;
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
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class) public class ForumSocketEndpointTest {
  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();
  private static final String AUTH_REST_PATH = "api/rest/auth/";
  private static final String FORUM_SOCKET_PATH = "api/websocket/";
  private static Logger logger = LoggerFactory.getLogger(ForumRestApi.class);
  // static data holders for testing messages
  private static MessageEntity receivedMessageClient1;
  @Rule public final ExpectedException exception = ExpectedException.none();
  @Inject Controller controller;
  @Inject private SubscriptionManager subscriptionManager;
  @ArquillianResource private URL webappUrl;
  private SimpleClient client;
  private String cookie;

  @Deployment public static Archive<?> createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Before public void setup() {
    client = new SimpleClient(webappUrl.toString(), configProvider.getPasswordAuthentication());
    Response response = client.post(getCredentials(), Arrays.asList(AUTH_REST_PATH, "login"), null);
    cookie = response.getStringHeaders().get("Set-Cookie").get(0);
    response.close();
    receivedMessageClient1 = null;
  }

  @After public void teardown() {
    client = null;
    for (ForumEntity forum : controller.listForums()) {
      controller.deleteForum(forum.getUuid());
    }
    cookie = null;
    receivedMessageClient1 = null;
  }

  @Test public void testActivateDeactivate() throws Exception {
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Authorization", Collections.singletonList(
        PasswordAuthenticator.getBasicAuthentication(configProvider.getPasswordAuthentication())));
    headers.put("Cookie", Collections.singletonList(cookie));

    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    ClientEndpointConfig configuration = ClientEndpointConfig.Builder.create().build();
    configuration.getConfigurator().beforeRequest(headers);
    Endpoint client1 = new Endpoint() {
      @Override public void onOpen(Session session, EndpointConfig config) {
        // nothing to do
      }
      @Override public void onError(Session session, Throwable thr) {
        logger.error("Error received by websocket client endpoint", thr);
      }
    };

    String forumUuid = UUID.randomUUID().toString();
    Subscription subscription = subscriptionManager.createSubscription(forumUuid, configProvider.getUsername());
    int preSessionCount = subscriptionManager.sessionsForTopic(forumUuid).size();
    URI uri =
        UriBuilder.fromUri(webappUrl.toURI()).scheme("ws").path(FORUM_SOCKET_PATH).path(subscription.getSubscriptionId())
            .userInfo(PasswordAuthenticator.getToken(configProvider.getPasswordAuthentication()))
            .build();
    logger.info("connecting to: " + uri);
    Session session = container.connectToServer(client1, configuration, uri);

    Thread.sleep(2000);
    int postSessionCount = subscriptionManager.sessionsForTopic(forumUuid).size();
    assertEquals("subscribed session count", preSessionCount + 1, postSessionCount);

    session.close();
    Thread.sleep(2000);
    int closedSessionCount = subscriptionManager.sessionsForTopic(forumUuid).size();
    assertEquals("unsubscribed session count", preSessionCount, closedSessionCount);
  }

  @Test public void testReceiveMessageOnCreate() throws Exception {
    // create sample forum
    ForumEntity forum = controller.createForum(ForumEntityTest.createRandomNewForum());
    String forumUuid = forum.getUuid();

    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    ClientEndpointConfig configuration = ClientEndpointConfig.Builder.create()
        .decoders(Arrays.<Class<? extends Decoder>>asList(MessageEntityDecoder.class))
        .encoders(Arrays.<Class<? extends Encoder>>asList(MessageEntityEncoder.class)).build();

    Endpoint client1 = new Endpoint() {
      @Override public void onOpen(Session session, EndpointConfig config) {
        // warn: use caution before replace with lambda, first try caused runtime error,
        // jaxrs or cdi could not match parameter type
        session.addMessageHandler(new MessageHandler.Whole<MessageEntity>() {
          @Override public void onMessage(MessageEntity message) {
            logger.info("recieved something(" + message + ")");
            receivedMessageClient1 = message;
          }
        });
      }
    };


    Subscription subscription = subscriptionManager.createSubscription(forumUuid, configProvider.getUsername());
    URI uri =
        UriBuilder.fromUri(webappUrl.toURI()).scheme("ws").path(FORUM_SOCKET_PATH).path(subscription.getSubscriptionId())
            .build();
    container.connectToServer(client1, configuration, uri);

    // give the client a little time to connect
    Thread.sleep(2000);

    // post a new message
    MessageEntity sampleMessage = MessageEntityTest.createRandomNewMessage(forumUuid);
    logger.info("client creating message: " + new ObjectMapper().writeValueAsString(sampleMessage));
    controller.postMessageToForum(sampleMessage, forumUuid);

    // give everybody a little time to process the message
    Thread.sleep(2000);
    assertNotNull("recieved message should not be null", receivedMessageClient1);
    MessageEntityTest
        .verifyEqualityOfAllAttributes("received message", sampleMessage, receivedMessageClient1);
  }

  private Credentials getCredentials() {
    PasswordAuthentication auth = configProvider.getPasswordAuthentication();
    return new Credentials(auth.getUserName(), new String(auth.getPassword()));
  }

}
