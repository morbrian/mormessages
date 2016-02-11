package morbrian.mormessages.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.mormessages.controller.Subscription;
import morbrian.mormessages.controller.SubscriptionActivator;
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

@ServerEndpoint(value = "/api/websocket/{subscriptionId}", encoders = {
    MessageEntityEncoder.class}, decoders = {MessageEntityDecoder.class})
public class ForumSocketEndpoint {

  @Inject private Logger logger;
  @Inject private Event<SubscriptionActivator> subscriptionEventSrc;

  @PostConstruct public void startIntervalNotifier() {
    // anything to do?
  }

  @OnOpen public void onOpen(Session session, @PathParam("subscriptionId") String subscriptionId) {
    Principal principal = session.getUserPrincipal();
    String userIdentity = ((principal != null) ? principal.getName() : null);
    if (userIdentity == null || userIdentity.equals("anonymous")) {
      logger.warn("Ignoring connected session(" + session.getId() + ") for " + userIdentity + " userIdentity");
    } else {
      if (logger.isDebugEnabled()) {
        logger.info("New websocket session opened: " + wrapForLogging(session, userIdentity, subscriptionId));
      }
      subscriptionEventSrc.select(Opened.SELECTOR).fire(new SubscriptionActivator(session, subscriptionId));
    }
  }

  @OnClose public void onClose(Session session, @PathParam("subscriptionId") String subscriptionId) {
    Principal principal = session.getUserPrincipal();
    String userIdentity = ((principal != null) ? principal.getName() : null);
    if (userIdentity == null || userIdentity.equals("anonymous")) {
      logger.warn("Ignoring disconnected session(" + session.getId() + ") for " + userIdentity + " userIdentity");
    } else {
      if (logger.isDebugEnabled()) {
        logger.info("Websocket session closed: " + wrapForLogging(session, userIdentity, subscriptionId));
      }
      subscriptionEventSrc.select(Closed.SELECTOR).fire(new SubscriptionActivator(session, subscriptionId));
    }
  }

  @OnMessage public void onMessage(Session session, MessageEntity message,
      @PathParam("subscriptionId") String subscriptionId) throws IOException, EncodeException {
    Principal principal = session.getUserPrincipal();
    String userIdentity = ((principal != null) ? principal.getName() : null);
    if (logger.isTraceEnabled()) {
      logger.trace("Received Message: " + wrapForLogging(session, userIdentity, subscriptionId) +
          " with content: " + new ObjectMapper().writeValueAsString(message));
    }
  }

  @OnError public void error(Session session, Throwable t, @PathParam("subscriptionId") String subscriptionId) {
    Principal principal = session.getUserPrincipal();
    String userIdentity = ((principal != null) ? principal.getName() : null);
    logger.error(
        "Web socket error via OnError: " + wrapForLogging(session, userIdentity, subscriptionId));
  }

  private String wrapForLogging(Session session, String userIdentity, String subscriptionId) {
    return "principal(" + userIdentity + "), sessionId(" + session.getId()
        + "), subscriptionId(" + subscriptionId + ")";
  }

}
