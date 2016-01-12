package morbrian.websockets.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import morbrian.websockets.model.Credentials;
import morbrian.websockets.model.ForumEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Arquillian.class) public class ForumRestApiTest {

  private static final ContainerConfigurationProvider
      configProvider = new ContainerConfigurationProvider();
  private static final String AUTH_BASE_PATH = "/test/api/rest/auth/";
  private static final String FORUM_BASE_PATH = "/test/api/rest/forum/";
  private static Logger logger = LoggerFactory.getLogger(ForumRestApi.class);
  private SimpleClient client;
  private Credentials credentials;
  private ObjectMapper mapper;

  @Deployment public static JavaArchive createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Before public void setup() {
    client = new SimpleClient(configProvider.getRestProtocolHostPort());
    credentials = getCredentials();
    client.invokeRequest(HttpMethod.POST, AUTH_BASE_PATH + "login", credentials).close();
    mapper = new ObjectMapper();
  }

  @After public void teardown() {
    client.invokeRequest(HttpMethod.DELETE, AUTH_BASE_PATH + "logout", null);
    client = null;
    credentials = null;
    mapper = null;
  }

  @Test public void m01shouldReturnEmptyList() throws IOException {
    Response response = client.invokeRequest(HttpMethod.GET, FORUM_BASE_PATH, null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    List<ForumEntity> forumList = response.readEntity(new GenericType<List<ForumEntity>>(){});
    response.close();
    assertEquals("forumList.size", 0, forumList.size());
  }

  @Test public void m02shouldCreateNewForum() throws IOException {
    ForumEntity expectedForum = createRandomNewForum();
    Response response = client.invokeRequest(HttpMethod.PUT, FORUM_BASE_PATH, expectedForum);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
    ForumEntity forum = response.readEntity(ForumEntity.class);
    response.close();
    assertEquals("forum.title", expectedForum.getTitle(), forum.getTitle());
    assertEquals("forum.description", expectedForum.getDescription(), forum.getDescription());
    assertEquals("forum.imageUrl", expectedForum.getImageUrl(), forum.getImageUrl());
    assertEquals("forum.createdBy", credentials.getUsername(), forum.getCreatedByUid());
  }

  private Credentials getCredentials() {
    return new Credentials(configProvider.getUsername(), configProvider.getPassword());
  }

  private ForumEntity createRandomNewForum() {
    ForumEntity expectedForum = new ForumEntity(
        ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString(),
        ContainerConfigurationProvider.randomAlphaNumericString()
    );
    return expectedForum;
  }

}
