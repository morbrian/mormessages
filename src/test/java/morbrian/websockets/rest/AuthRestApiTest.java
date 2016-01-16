package morbrian.websockets.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import morbrian.websockets.model.BaseResponse;
import morbrian.websockets.model.Credentials;
import morbrian.websockets.model.Status;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class) public class AuthRestApiTest {

  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();
  private static final String AUTH_BASE_PATH = "api/rest/auth/";
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
    client = new SimpleClient(webappUrl.toString());
  }

  @After public void teardown() {
    client.delete(Arrays.asList(AUTH_BASE_PATH, "logout"), null).close();
    client = null;
  }

  @Test public void badCredentialLoginShouldRespondWithUnauthorized() throws Exception {
    Credentials credentials = new Credentials(configProvider.getUsername(),
        ContainerConfigurationProvider.randomAlphaNumericString());
    ObjectMapper mapper = new ObjectMapper();
    System.out.println(mapper.writeValueAsString(credentials));
    Response response = client.post(credentials, Arrays.asList(AUTH_BASE_PATH, "login"), null);
    assertEquals("response.status", Response.Status.UNAUTHORIZED.getStatusCode(),
        response.getStatus());
    BaseResponse base = response.readEntity(BaseResponse.class);
    response.close();
    assertEquals("status.type", Status.Type.UNAUTHORIZED.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.UNAUTHORIZED.ordinal(), base.getStatus().getCode());
  }

  @Test public void goodCredentialLoginShouldRespondWithSuccess() throws Exception {
    Response response = client.post(getCredentials(), Arrays.asList(AUTH_BASE_PATH, "login"), null);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    BaseResponse base = response.readEntity(BaseResponse.class);
    response.close();
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
  }

  @Test public void loggedOutWhoamiShouldRespondWithAnonymous() {
    Response response = client.get(Arrays.asList(AUTH_BASE_PATH, "whoami"), null);
    BaseResponse base = response.readEntity(BaseResponse.class);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    response.close();
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
    assertEquals("username", AuthRestApi.ANONYMOUS, base.getData().get(AuthRestApi.USERNAME));
  }

  @Test public void loggedInWhoamiShouldRespondWithUsername() throws Exception {
    Credentials credentials = getCredentials();
    client.post(credentials, Arrays.asList(AUTH_BASE_PATH, "login"), null).close();
    Response response = client.get(Arrays.asList(AUTH_BASE_PATH, "whoami"), null);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    BaseResponse base = response.readEntity(BaseResponse.class);
    response.close();
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
    assertEquals("status.code", Status.Type.SUCCESS.ordinal(), base.getStatus().getCode());
    assertEquals("username", credentials.getUsername(), base.getData().get(AuthRestApi.USERNAME));
  }

  @Test public void logoutShouldRemoveSessionAuthorization() throws Exception {
    Credentials credentials = getCredentials();
    client.post(credentials, Arrays.asList(AUTH_BASE_PATH, "login"), null).close();
    Response response = client.get(Arrays.asList(AUTH_BASE_PATH, "whoami"), null);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    BaseResponse base = response.readEntity(BaseResponse.class);
    // verify we are logged in
    assertEquals("username", credentials.getUsername(), base.getData().get(AuthRestApi.USERNAME));

    // now logout
    response = client.delete(Arrays.asList(AUTH_BASE_PATH, "logout"), null);
    assertEquals("response.status", Response.Status.OK.getStatusCode(), response.getStatus());
    base = response.readEntity(BaseResponse.class);
    response.close();
    assertTrue("username",
        base.getData() == null || base.getData().get(AuthRestApi.USERNAME) == null);
    assertEquals("status.type", Status.Type.SUCCESS.name(), base.getStatus().getType());
  }

  private Credentials getCredentials() {
    return new Credentials(configProvider.getUsername(), configProvider.getPassword());
  }

}
