package morbrian.mormessages.rest;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;

public class PasswordAuthenticator implements ClientRequestFilter {

  private final PasswordAuthentication credentials;

  public PasswordAuthenticator(PasswordAuthentication credentials) {
    this.credentials = credentials;
  }

  public static String getToken(PasswordAuthentication credentials) {
    return credentials.getUserName() + ":" + new String(credentials.getPassword());
  }

  public static String getBasicAuthentication(PasswordAuthentication credentials) {
    String token = getToken(credentials);
    try {
      return "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      throw new IllegalStateException("Cannot encode with UTF-8", ex);
    }
  }

  public void filter(ClientRequestContext requestContext) throws IOException {
    MultivaluedMap<String, Object> headers = requestContext.getHeaders();
    final String basicAuthentication = getBasicAuthentication();
    headers.add("Authorization", basicAuthentication);

  }

  private String getBasicAuthentication() {
    return PasswordAuthenticator.getBasicAuthentication(credentials);
  }
}
