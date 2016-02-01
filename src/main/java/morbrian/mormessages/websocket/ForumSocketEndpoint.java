package morbrian.mormessages.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.mormessages.controller.Subscription;
import morbrian.mormessages.event.Closed;
import morbrian.mormessages.event.Opened;
import morbrian.mormessages.model.MessageEntity;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.security.Principal;

@ServerEndpoint(value = "/api/websocket/forum/{forumId}", encoders = {
    MessageEntityEncoder.class}, decoders = {MessageEntityDecoder.class})
public class ForumSocketEndpoint {

  @Inject private Logger logger;
  @Inject private Event<Subscription> subscriptionEventSrc;

  @PostConstruct public void startIntervalNotifier() {
    // anything to do?
  }

  @OnOpen public void onOpen(Session session, @PathParam("forumId") Long forumId) {
    Principal principal = session.getUserPrincipal();
    String userIdentity = ((principal != null) ? principal.getName() : null);
    logger.info(
        "New websocket session opened: " + wrapForLogging(userIdentity, forumId, session.getId()));
    if (userIdentity == null) {
      // TODO: we don't want unauthenticated users subscribing
      logger.warn("Opening session(" + session.getId() + ") for null userIdentity");
    }
    Subscription subscription = new Subscription(session, userIdentity, forumId);
    subscriptionEventSrc.select(Opened.SELECTOR).fire(subscription);
  }

  @OnClose public void onClose(Session session, @PathParam("forumId") Long forumId) {
    Principal principal = session.getUserPrincipal();
    String userIdentity = ((principal != null) ? principal.getName() : null);
    logger.info(
        "Websocket session closed: " + wrapForLogging(userIdentity, forumId, session.getId()));
    if (userIdentity == null) {
      logger.warn("Closing session(" + session.getId() + ") for null userIdentity");
    }
    Subscription subscription = new Subscription(session, userIdentity, forumId);
    subscriptionEventSrc.select(Closed.SELECTOR).fire(subscription);

  }

  @OnMessage
  public void onMessage(Session session, MessageEntity message, @PathParam("forumId") Long forumId)
      throws IOException, EncodeException {
    Principal principal = session.getUserPrincipal();
    String userIdentity = ((principal != null) ? principal.getName() : null);
    logger.error("Received Message: " + wrapForLogging(userIdentity, forumId, session.getId()) +
        " with content: " + new ObjectMapper().writeValueAsString(message));
  }

  @OnError public void error(Session session, Throwable t, @PathParam("forumId") Long forumId) {
    Principal principal = session.getUserPrincipal();
    String userIdentity = ((principal != null) ? principal.getName() : null);
    logger.error(
        "Web socket error for session: " + wrapForLogging(userIdentity, forumId, session.getId()),
        t);
  }

  private String wrapForLogging(String userIdentity, Long forumId, String sessionId) {
    return "principal(" + userIdentity + "), forumId(" + forumId + "), sessionId(" + sessionId
        + ")";
  }

}
