package morbrian.websockets.rest;

import morbrian.websockets.model.BaseResponse;
import morbrian.websockets.model.Credentials;
import morbrian.websockets.model.Status;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Path("/auth") @RequestScoped public class AuthRestApi {

  public static final String ANONYMOUS = "anonymous";
  public static final String USERNAME = "username";

  @Context HttpServletRequest request;
  @Inject Principal principal;
  @Inject Logger logger;

  @GET @Path("/login") @Produces(MediaType.APPLICATION_JSON) public BaseResponse loginGet() {
    Map<String, Object> data = new HashMap<>();
    throw failedLogin("unsupported login method: GET");
  }

  @POST @Path("/login") @Produces(MediaType.APPLICATION_JSON)
  public BaseResponse loginPost(Credentials authInfo) {
    return login(authInfo.getUsername(), authInfo.getPassword());
  }

  @GET @Path("/whoami") @Produces(MediaType.APPLICATION_JSON) public BaseResponse whoamiGet() {
    return whoami();
  }

  @DELETE @Path("/logout") @Produces(MediaType.APPLICATION_JSON) public BaseResponse logoutPost() {
    return logout();
  }

  private BaseResponse whoami() {
    String username = userPrincipalName();
    BaseResponse base = new BaseResponse(new Status(Status.Type.SUCCESS, username));
    base.addData(USERNAME, username);
    return base;
  }

  private BaseResponse logout() {
    try {
      request.logout();
      return new BaseResponse(new Status(Status.Type.SUCCESS));
    } catch (ServletException e) {
      logger.info("Logout Failed", e);
      BaseResponse base = new BaseResponse(new Status(Status.Type.ERROR, "logout"));
      Response error = Response.status(Response.Status.BAD_REQUEST).entity(base).build();
      throw new WebApplicationException(error);
    }
  }

  private BaseResponse login(String username, String password) {
    try {
      if (!ANONYMOUS.equals(userPrincipalName())) {
        request.logout();
      }
      request.login(username, password);
      BaseResponse base = new BaseResponse(new Status(Status.Type.SUCCESS, username));
      return base;
    } catch (ServletException e) {
      throw failedLogin(username);
    }
  }

  private String userPrincipalName() {
    try {
      // when security enabled, principal may always be present
      // but in jboss returns 'anonymous' as username
      // and in tomcat calling getName() throws exception.
      return principal.getName();
    } catch(Throwable th) {
      return ANONYMOUS;
    }
  }
  private WebApplicationException failedLogin(String details) {
    BaseResponse base = new BaseResponse(new Status(Status.Type.UNAUTHORIZED, details));
    Response error =
        Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(base)
            .build();
    logger.error("FAILED AND RETURN ERROR: " + error);
    return new WebApplicationException(error);
  }

}
