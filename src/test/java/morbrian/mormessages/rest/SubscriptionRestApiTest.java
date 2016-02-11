package morbrian.mormessages.rest;


import morbrian.mormessages.controller.Controller;
import morbrian.mormessages.controller.Subscription;
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
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
    for (Subscription subscription : controller.listSubscriptions()) {
      controller.deleteSubscription(subscription.getSubscriptionId());
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

  private Credentials getCredentials() {
    PasswordAuthentication auth = configProvider.getPasswordAuthentication();
    return new Credentials(auth.getUserName(), new String(auth.getPassword()));
  }

}
