package morbrian.mormessages.controller;

import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MockSession implements Session {

  private String sessionId;

  public MockSession() {
    sessionId = UUID.randomUUID().toString();
  }

  @Override public WebSocketContainer getContainer() {
    return null;
  }

  @Override public void addMessageHandler(MessageHandler handler) throws IllegalStateException {

  }

  @Override public Set<MessageHandler> getMessageHandlers() {
    return null;
  }

  @Override public void removeMessageHandler(MessageHandler handler) {

  }

  @Override public String getProtocolVersion() {
    return null;
  }

  @Override public String getNegotiatedSubprotocol() {
    return null;
  }

  @Override public List<Extension> getNegotiatedExtensions() {
    return null;
  }

  @Override public boolean isSecure() {
    return false;
  }

  @Override public boolean isOpen() {
    return false;
  }

  @Override public long getMaxIdleTimeout() {
    return 0;
  }

  @Override public void setMaxIdleTimeout(long milliseconds) {

  }

  @Override public int getMaxBinaryMessageBufferSize() {
    return 0;
  }

  @Override public void setMaxBinaryMessageBufferSize(int length) {

  }

  @Override public int getMaxTextMessageBufferSize() {
    return 0;
  }

  @Override public void setMaxTextMessageBufferSize(int length) {

  }

  @Override public Async getAsyncRemote() {
    return null;
  }

  @Override public Basic getBasicRemote() {
    return null;
  }

  @Override public String getId() {
    return sessionId;
  }

  @Override public void close() throws IOException {

  }

  @Override public void close(CloseReason closeReason) throws IOException {

  }

  @Override public URI getRequestURI() {
    return null;
  }

  @Override public Map<String, List<String>> getRequestParameterMap() {
    return null;
  }

  @Override public String getQueryString() {
    return null;
  }

  @Override public Map<String, String> getPathParameters() {
    return null;
  }

  @Override public Map<String, Object> getUserProperties() {
    return null;
  }

  @Override public Principal getUserPrincipal() {
    return null;
  }

  @Override public Set<Session> getOpenSessions() {
    return null;
  }
}
