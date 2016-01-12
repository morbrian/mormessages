package morbrian.websockets.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
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
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class) public class AuthRestApiTest {

  private static final ContainerConfigurationProvider
      configProvider = new ContainerConfigurationProvider();
  private static final String AUTH_BASE_PATH = "/test/api/rest/auth/";
  private static Logger logger = LoggerFactory.getLogger(AuthRestApi.class);
  private SimpleClient client;

  @Deployment public static JavaArchive createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Before public void setup() {
    client = new SimpleClient(configProvider.getRestProtocolHostPort());
  }

  @After public void teardown() {
    client.invokeRequest(HttpMethod.DELETE, "/test/api/rest/auth/logout", null).close();
    client = null;
  }

  @Test public void badCredentialLoginShouldRespondWithUnauthorized()
      throws Exception {
    Credentials credentials =
        new Credentials(configProvider.getUsername(), ContainerConfigurationProvider.randomAlphaNumericString());
    ObjectMapper mapper = new ObjectMapper();
    System.out.println(mapper.writeValueAsString(credentials));
    Response response = client.invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", credentials);
    assertEquals("response.status", Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    BaseResponse base = response.readEntity(BaseResponse.class);
    response.close();
    assertEquals("status.type", Status.Type.UNAUTHORIZED.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.UNAUTHORIZED.ordinal(), base.getStatus().getCode());
  }

  @Test public void goodCredentialLoginShouldRespondWithSuccess() throws Exception {
    Response response = client.invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", getCredentials());
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    BaseResponse base = response.readEntity(BaseResponse.class);
    response.close();
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
  }

  @Test public void loggedOutWhoamiShouldRespondWithAnonymous() {
    Response response = client.invokeRequest(HttpMethod.GET, AUTH_BASE_PATH + "whoami", null);
    BaseResponse base = response.readEntity(BaseResponse.class);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    response.close();
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
    assertEquals("username", AuthRestApi.ANONYMOUS, base.getData().get(AuthRestApi.USERNAME));
  }

  @Test public void loggedInWhoamiShouldRespondWithUsername() throws Exception {
    Credentials credentials = getCredentials();
    client.invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", credentials).close();
    Response response = client.invokeRequest(HttpMethod.GET, AUTH_BASE_PATH + "whoami", null);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    BaseResponse base = response.readEntity(BaseResponse.class);
    response.close();
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
    assertEquals("username", credentials.getUsername(), base.getData().get(AuthRestApi.USERNAME));
  }

  @Test public void logoutShouldRemoveSessionAuthorization() throws Exception {
    Credentials credentials = getCredentials();
    client.invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", credentials).close();
    Response response = client.invokeRequest(HttpMethod.GET, AUTH_BASE_PATH + "whoami", null);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    BaseResponse base = response.readEntity(BaseResponse.class);
    // verify we are logged in
    assertEquals("username", credentials.getUsername(), base.getData().get(AuthRestApi.USERNAME));

    // now logout
    response = client.invokeRequest(HttpMethod.DELETE, AUTH_BASE_PATH + "logout", null);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    base = response.readEntity(BaseResponse.class);
    response.close();
    assertTrue("username",
        base.getData() == null || base.getData().get(AuthRestApi.USERNAME) == null);
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
  }

  private Credentials getCredentials() throws Exception {
    Credentials credentials =  new Credentials(configProvider.getUsername(), configProvider.getPassword());
    ObjectMapper mapper = new ObjectMapper();
    System.out.println("CREDENTIALS: " + mapper.writeValueAsString(credentials));
    return credentials;
  }

}
