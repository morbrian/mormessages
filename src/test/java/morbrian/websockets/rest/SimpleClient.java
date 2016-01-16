package morbrian.websockets.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class SimpleClient {

  private final Client client;
  private final WebTarget target;
  private final String baseUrl;

  public SimpleClient(String baseUrl) {
    this.baseUrl = baseUrl;
    client = ClientBuilder.newClient();
    target = client.target(baseUrl);
  }

  public Response get(List<Object> path, Map<String, Object> params) {
    WebTarget targetGet = applyPath(target, path);
    targetGet = applyParams(targetGet, params);
    return targetGet.request(MediaType.APPLICATION_JSON).get();
  }

  public Response post(Object data, List<Object> path, Map<String, Object> params) {
    WebTarget targetPost = applyPath(target, path);
    targetPost = applyParams(targetPost, params);
    return targetPost.request(MediaType.APPLICATION_JSON).post(Entity.json(data));
  }

  public Response put(Object data, List<Object> path, Map<String, Object> params) {
    WebTarget targetPost = applyPath(target, path);
    targetPost = applyParams(targetPost, params);
    return targetPost.request(MediaType.APPLICATION_JSON).put(Entity.json(data));
  }

  public Response delete(List<Object> path, Map<String, Object> params) {
    WebTarget targetPost = applyPath(target, path);
    targetPost = applyParams(targetPost, params);
    return targetPost.request(MediaType.APPLICATION_JSON).delete();
  }

  private WebTarget applyParams(WebTarget target, Map<String, Object> params) {
    if (params != null) {
      for (String key : params.keySet()) {
        target = target.queryParam(key, params.get(key));
      }
    }
    return target;
  }

  private WebTarget applyPath(WebTarget target, List<Object> path) {
    if (path != null) {
      for (Object p : path) {
        target = target.path(p.toString());
      }
    }
    return target;
  }
}
