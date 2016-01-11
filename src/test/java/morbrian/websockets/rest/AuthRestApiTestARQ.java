package morbrian.websockets.rest;

import morbrian.websockets.model.BaseResponse;
import morbrian.websockets.model.Credentials;
import morbrian.websockets.model.Status;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class) public class AuthRestApiTestARQ {

  private static final RestConfigurationProvider configProvider = new RestConfigurationProvider();
  private static final String AUTH_BASE_PATH = "/test/api/rest/auth/";
  private static Logger logger = LoggerFactory.getLogger(AuthRestApi.class);
  private Client client;

  @Deployment public static JavaArchive createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Before public void setup() {
    client = ClientBuilder.newClient();
  }

  @After public void teardown() {
    invokeRequest(HttpMethod.DELETE, "/test/api/rest/auth/logout", null);
    client.close();
    client = null;
  }

  @Test public void badCredentialLoginShouldRespondWithUnauthorized() {
    Credentials credentials =
        new Credentials(configProvider.getUsername(), configProvider.randomAlphaNumericString());
    BaseResponse base = invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", credentials);
    assertEquals("status.type", Status.Type.UNAUTHORIZED.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.UNAUTHORIZED.ordinal(), base.getStatus().getCode());
  }

  @Test public void goodCredentialLoginShouldRespondWithSuccess() {
    BaseResponse base =
        invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", getCredentials());
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
  }

  @Test public void loggedOutWhoamiShouldRespondWithAnonymous() {
    BaseResponse base = invokeRequest(HttpMethod.GET, AUTH_BASE_PATH + "whoami", null);
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
    assertEquals("username", AuthRestApi.ANONYMOUS, base.getData().get(AuthRestApi.USERNAME));
  }

  @Test public void loggedInWhoamiShouldRespondWithUsername() {
    Credentials credentials = getCredentials();
    invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", credentials);
    BaseResponse base = invokeRequest(HttpMethod.GET, AUTH_BASE_PATH + "whoami", null);
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
    assertEquals("username", credentials.getUsername(), base.getData().get(AuthRestApi.USERNAME));
  }

  @Test public void logoutShouldRemoveSessionAuthorization() {
    Credentials credentials = getCredentials();
    invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", credentials);
    BaseResponse base = invokeRequest(HttpMethod.GET, AUTH_BASE_PATH + "whoami", null);
    // verify we are logged in
    assertEquals("username", credentials.getUsername(), base.getData().get(AuthRestApi.USERNAME));

    // now logout
    base = invokeRequest(HttpMethod.DELETE, AUTH_BASE_PATH + "logout", null);
    assertTrue("username",
        base.getData() == null || base.getData().get(AuthRestApi.USERNAME) == null);
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
  }

  private Credentials getCredentials() {
    return new Credentials(configProvider.getUsername(), configProvider.getPassword());
  }

  private BaseResponse invokeRequest(String method, String path, Object data) {
    WebTarget target = client.target(configProvider.getRestProtocolHostPort() + path);
    Invocation.Builder builder = target.request().accept(MediaType.APPLICATION_JSON);
    Invocation invocation;
    if (data == null) {
      invocation = builder.build(method);
    } else {
      invocation = builder.build(method, Entity.json(data));
    }
    Response response = invocation.invoke();
    return response.readEntity(BaseResponse.class);
  }
}
