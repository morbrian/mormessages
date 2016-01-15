package morbrian.websockets.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SimpleClient {

  private Client client;
  private String baseUrl;

  public SimpleClient(String baseUrl) {
    this.baseUrl = baseUrl;
    client = ClientBuilder.newClient();
  }

  public Response invokeRequest(String method, String path, Object data) {
    WebTarget target = client.target(baseUrl + path);
    Invocation.Builder builder = target.request().accept(MediaType.APPLICATION_JSON_TYPE);

    Invocation invocation;

    if (data == null) {
      invocation = builder.build(method);
    } else {
      invocation = builder.build(method, Entity.json(data));
    }

    return invocation.invoke();
  }
}
