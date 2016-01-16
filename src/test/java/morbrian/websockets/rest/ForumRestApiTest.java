package morbrian.websockets.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.test.provisioning.ContainerConfigurationProvider;
import morbrian.test.provisioning.VendorSpecificProvisioner;
import morbrian.websockets.controller.Controller;
import morbrian.websockets.model.Credentials;
import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.ForumEntityTest;
import morbrian.websockets.model.MessageEntity;
import morbrian.websockets.model.MessageEntityTest;
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
import javax.persistence.NoResultException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(Arquillian.class) public class ForumRestApiTest {

  private static final int SAMPLE_DATA_COUNT = 10;
  private static final ContainerConfigurationProvider configProvider =
      new ContainerConfigurationProvider();
  private static final String AUTH_BASE_PATH = "api/rest/auth/";
  private static final String FORUM_BASE_PATH = "api/rest/forum/";
  private static Logger logger = LoggerFactory.getLogger(ForumRestApi.class);
  @Rule public final ExpectedException exception = ExpectedException.none();
  @Inject Controller controller;
  @ArquillianResource private URL webappUrl;
  private SimpleClient client;
  private Credentials credentials;

  @Deployment public static Archive<?> createDeployment() {
    return configProvider.createDeployment();
  }

  @BeforeClass public static void setupClass() throws Throwable {
    VendorSpecificProvisioner provisioner = configProvider.getVendorSpecificProvisioner();
    provisioner.setup();
  }

  @Before public void setup() {
    client = new SimpleClient(webappUrl.toString());
    credentials = getCredentials();
    client.post(credentials, Arrays.asList(AUTH_BASE_PATH, "login"), null).close();
  }

  @After public void teardown() {
    client = null;
    credentials = null;
    for (ForumEntity forum : controller.forumList()) {
      controller.deleteForum(forum.getId());
    }
  }

  @Test public void shouldReturnEmptyList() throws IOException {
    //test
    Response response = client.get(Arrays.asList(FORUM_BASE_PATH), null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    List<ForumEntity> forumList = response.readEntity(new GenericType<List<ForumEntity>>() {
    });
    response.close();

    //verify
    assertEquals("forumList.size", 0, forumList.size());
  }

  @Test public void shouldReturnSortedForumList() throws IOException {
    // sample data
    List<ForumEntity> expectedList = new ArrayList<>();
    for (int i = 0; i < SAMPLE_DATA_COUNT; i++) {
      expectedList.add(controller.createForum(ForumEntityTest.createRandomNewForum()));
    }
    expectedList.sort(Comparator.comparing(ForumEntity::getCreatedTime));

    // test
    Response response = client.get(Arrays.asList(FORUM_BASE_PATH), null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    List<ForumEntity> resultList = response.readEntity(new GenericType<List<ForumEntity>>() {
    });
    response.close();

    // verify
    assertEquals("forumList.size", SAMPLE_DATA_COUNT, resultList.size());
    for (int i = 0; i < expectedList.size(); i++) {
      ForumEntityTest
          .verifyEqualityOfAllAttributes("item-" + i, expectedList.get(i), resultList.get(i));
    }
  }

  @Test public void shouldReturnForumIdentifiedById() throws IOException {
    // sample data
    ForumEntity expectedForum = controller.createForum(ForumEntityTest.createRandomNewForum());

    // test
    Response response = client.get(Arrays.asList(FORUM_BASE_PATH, expectedForum.getId()), null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    ForumEntity resultForum = response.readEntity(ForumEntity.class);
    response.close();

    // verify
    ForumEntityTest.verifyEqualityOfAllAttributes("forum", expectedForum, resultForum);
  }

  @Test public void shouldCreateNewForum() throws IOException {
    // test
    ForumEntity submittedForum = ForumEntityTest.createRandomNewForum();
    Response response = client.put(submittedForum, Arrays.asList(FORUM_BASE_PATH), null);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
    // even thouh readEntity works for other tests, it does not work here and I cannot figure out why.
    // trying it results in the following error:
    // ProcessingException: Unable to find a MessageBodyReader of content-type */* and type class morbrian.websockets.model.ForumEntity
    // but otherwise the REST API call appears to work fine
    // ForumEntity responseForum = response.readEntity(ForumEntity.class);
    String forumString = response.readEntity(String.class);
    ObjectMapper mapper = new ObjectMapper();
    ForumEntity responseForum = mapper.readValue(forumString, ForumEntity.class);
    response.close();

    //verify
    ForumEntity createdForum = controller.getForumById(responseForum.getId());

    // does the created forum match the data we submitted
    assertEquals("forum.title", submittedForum.getTitle(), createdForum.getTitle());
    assertEquals("forum.description", submittedForum.getDescription(),
        createdForum.getDescription());
    assertEquals("forum.imageUrl", submittedForum.getImageUrl(), createdForum.getImageUrl());

    // verify the created by matches our user
    assertEquals("forum.createdBy", credentials.getUsername(), createdForum.getCreatedByUid());

    // verify the forum data we recieved back matches database
    ForumEntityTest.verifyEqualityOfAllAttributes("forum", createdForum, responseForum);
  }

  @Test public void shouldModifyForum() throws IOException {
    // sample data
    ForumEntity sampleForum = controller.createForum(ForumEntityTest.createRandomNewForum());
    String updateTitle = ContainerConfigurationProvider.randomAlphaNumericString();
    String updateDescription = ContainerConfigurationProvider.randomAlphaNumericString();
    String updateImageUrl = ContainerConfigurationProvider.randomAlphaNumericString();

    // test
    sampleForum.setDescription(updateDescription);
    sampleForum.setTitle(updateTitle);
    sampleForum.setImageUrl(updateImageUrl);
    Response response =
        client.post(sampleForum, Arrays.asList(FORUM_BASE_PATH, sampleForum.getId()), null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    ForumEntity responseForum = response.readEntity(ForumEntity.class);
    response.close();

    // verify
    assertEquals("updatedByUid", credentials.getUsername(), responseForum.getModifiedByUid());
    assertEquals("update title", updateTitle, responseForum.getTitle());
    assertEquals("update description", updateDescription, responseForum.getDescription());
    assertEquals("update imageurl", updateImageUrl, responseForum.getImageUrl());
    ForumEntity expectedForum = controller.getForumById(sampleForum.getId());
    ForumEntityTest.verifyEqualityOfAllAttributes("forum", expectedForum, responseForum);
  }

  @Test public void shouldRemoveForumSpecifiedById() throws IOException {
    // sample data
    ForumEntity expectedForum = controller.createForum(ForumEntityTest.createRandomNewForum());
    assertEquals("pre-test assumption", expectedForum.getTitle(),
        controller.getForumById(expectedForum.getId()).getTitle());

    // test
    Response response = client.delete(Arrays.asList(FORUM_BASE_PATH, expectedForum.getId()), null);
    assertThat("success or no content", response.getStatus(),
        either(equalTo(Response.Status.OK.getStatusCode()))
            .or(equalTo(Response.Status.NO_CONTENT.getStatusCode())));
    response.close();

    // verify
    exception.expect(NoResultException.class);
    assertNull("removed", controller.getForumById(expectedForum.getId()));
  }

  @Test public void shouldPostMessageToForum() throws IOException {
    // sample data
    ForumEntity submittedForum = controller.createForum(ForumEntityTest.createRandomNewForum());
    MessageEntity submittedMessage =
        MessageEntityTest.createRandomNewMessage(submittedForum.getId());

    // test
    Response response = client
        .put(submittedMessage, Arrays.asList(FORUM_BASE_PATH, submittedForum.getId(), "message"),
            null);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
    // even thouh readEntity works for other tests, it does not work here,
    // perhaps it does not work correctly for PUT responses in general?
    // trying it results in the following error:
    // ProcessingException: Unable to find a MessageBodyReader of content-type */* and type class morbrian.websockets.model.ForumEntity
    // but otherwise the REST API call appears to work fine
    // MessageEntity responseMessage = response.readEntity(MessageEntity.class);
    String messageString = response.readEntity(String.class);
    ObjectMapper mapper = new ObjectMapper();
    MessageEntity responseMessage = mapper.readValue(messageString, MessageEntity.class);
    response.close();

    //verify
    MessageEntity createdMessage = controller.getMessageById(responseMessage.getId());

    // does the created forum match the data we submitted
    assertEquals("messge.text", submittedMessage.getText(), createdMessage.getText());
    assertEquals("message.imageUrl", submittedMessage.getImageUrl(), createdMessage.getImageUrl());
    assertEquals("message.forumId", submittedMessage.getForumId(), createdMessage.getForumId());

    // verify the created by matches our user
    assertEquals("message.createdBy", credentials.getUsername(), createdMessage.getCreatedByUid());

    // verify the forum data we recieved back matches database
    MessageEntityTest.verifyEqualityOfAllAttributes("message", createdMessage, responseMessage);
  }

  @Test public void shouldReturnMessageList() {
    // sample data
    ForumEntity forum = controller.createForum(ForumEntityTest.createRandomNewForum());
    List<MessageEntity> sampleMessages = new ArrayList<>();
    for (int i = 0; i < SAMPLE_DATA_COUNT; i++) {
      sampleMessages.add(controller
          .postMessageToForum(MessageEntityTest.createRandomNewMessage(forum.getId()),
              forum.getId()));
    }

    // test
    Response response = client.get(Arrays.asList(FORUM_BASE_PATH, forum.getId(), "message"), null);
    assertEquals("response", Response.Status.OK.getStatusCode(), response.getStatus());
    List<MessageEntity> responseMessages =
        response.readEntity(new GenericType<List<MessageEntity>>() {
        });
    response.close();

    // verify
    assertEquals("message count", sampleMessages.size(), responseMessages.size());
  }

  @Test public void shouldSubscribeToForum() {
    fail("not implemented");
  }

  @Test public void shouldUnsubscribeFromForum() {
    fail("not implemented");
  }

  private Credentials getCredentials() {
    return new Credentials(configProvider.getUsername(), configProvider.getPassword());
  }

}
