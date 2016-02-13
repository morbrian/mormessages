package morbrian.mormessages.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.mormessages.model.Credentials;
import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.ForumEntityTest;
import morbrian.mormessages.model.MessageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class MorMessagesRestCli {
  private static final int SAMPLE_DATA_COUNT = 10;
  private static final int LARGE_DATA_COUNT = 50;
  private static final String AUTH_REST_PATH = "api/rest/auth/";
  private static final String FORUM_REST_PATH = "api/rest/forum/";
  private static Logger logger = LoggerFactory.getLogger(ForumRestApi.class);
  private URL webappUrl;
  private SimpleClient client;
  private PasswordAuthentication auth;

  public MorMessagesRestCli(URL webappUrl, PasswordAuthentication auth) {
    this.webappUrl = webappUrl;
    this.auth = auth;
    client = new SimpleClient(webappUrl.toString(), auth);
  }

  public static void main(String[] args) throws IOException {
    // if using SSL and self signed certs, the easiest way to trust it is with system property
    // referencing a keystore containing the server public cert
    // -Djavax.net.ssl.trustStore=src/test/certs/mormessages-trust.jks
    URL url = new URL("https://mormessages.morbrian.com:8443/mormessages");
    PasswordAuthentication credentials =
        new PasswordAuthentication("marley", "changeme".toCharArray());
    MorMessagesRestCli app = null;

    try {
      app = new MorMessagesRestCli(url, credentials);
    } catch (Exception e) {
      System.err.println("Failed to run app: " + e.getMessage());
      System.err.println(
          "If you are running this from maven or your IDE, remember to enable the 'cli-app-cxf' profile.");
      System.exit(1);
    }

    app.authenticate();

    ForumEntity forum = app.createNewForum(ForumEntityTest.createRandomNewForum());
    //    for (int fIdx = 0; fIdx < 100; ++fIdx) {
    //      ForumEntity forum = app.createNewForum(ForumEntityTest.createRandomNewForum());
    //
    //      for (int mIdx = 0; mIdx < 10; ++mIdx) {
    //        app.createNewMessage(MessageEntityTest.createRandomNewMessage(forum.getUuid()));
    //      }
    //    }

  }

  public void authenticate() throws JsonProcessingException {
    Credentials credentials = new Credentials(auth.getUserName(), new String(auth.getPassword()));
    client.post(new ObjectMapper().writeValueAsString(credentials),
        Arrays.asList(AUTH_REST_PATH, "login"), null).close();
  }

  public ForumEntity createNewForum(ForumEntity forum) throws IOException {
    Response response = client.put(new ObjectMapper().writeValueAsString(forum),
        Collections.singletonList(FORUM_REST_PATH), null);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
    String forumString = response.readEntity(String.class);
    ObjectMapper mapper = new ObjectMapper();
    ForumEntity responseForum = mapper.readValue(forumString, ForumEntity.class);
    response.close();
    return responseForum;
  }

  public void createNewMessage(MessageEntity message) throws JsonProcessingException {
    Response response = client.put(new ObjectMapper().writeValueAsString(message),
        Arrays.asList(FORUM_REST_PATH, message.getForumUuid(), "message"), null);
    assertEquals("response", Response.Status.CREATED.getStatusCode(), response.getStatus());
  }

}
