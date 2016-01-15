package morbrian.websockets.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import morbrian.websockets.model.Credentials;
import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.ForumEntityTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING) @RunWith(Arquillian.class)
public class ForumRestApiTest {

  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();
  private static final String AUTH_BASE_PATH = "/test/api/rest/auth/";
  private static final String FORUM_BASE_PATH = "/test/api/rest/forum/";
  private static Logger logger = LoggerFactory.getLogger(ForumRestApi.class);
  private SimpleClient client;
  private Credentials credentials;
  private ObjectMapper mapper;

  @Deployment public static Archive<?> createDeployment() {
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
    client = null;
    credentials = null;
    mapper = null;
  }

  @Test public void m01shouldReturnEmptyList() throws IOException {
    Response response = client.invokeRequest(HttpMethod.GET, FORUM_BASE_PATH, null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    List<ForumEntity> forumList = response.readEntity(new GenericType<List<ForumEntity>>() {
    });
    response.close();
    assertEquals("forumList.size", 0, forumList.size());
  }

  @Test public void m02shouldCreateNewForum() throws IOException {
    ForumEntity expectedForum = ForumEntityTest.createRandomNewForum();
    Response response = client.invokeRequest(HttpMethod.PUT, FORUM_BASE_PATH, expectedForum);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
    // TODO: I spent hours trying to make this line of code work but it never did.
    // TODO: It consistently said it could not find a MessageBodyReader for */* and ForumEntity
    // TODO: I tried various dependency changes and registered various providers, and still nothing.
    // ForumEntity createdForum = response.readEntity(ForumEntity.class);
    String forumString = response.readEntity(String.class);
    ObjectMapper mapper = new ObjectMapper();
    ForumEntity createdForum = mapper.readValue(forumString, ForumEntity.class);
    response.close();
    assertEquals("forum.title", expectedForum.getTitle(), createdForum.getTitle());
    assertEquals("forum.description", expectedForum.getDescription(),
        createdForum.getDescription());
    assertEquals("forum.imageUrl", expectedForum.getImageUrl(), createdForum.getImageUrl());
    assertEquals("forum.createdBy", credentials.getUsername(), createdForum.getCreatedByUid());
  }

  private Credentials getCredentials() {
    return new Credentials(configProvider.getUsername(), configProvider.getPassword());
  }

}
