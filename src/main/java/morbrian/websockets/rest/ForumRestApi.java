package morbrian.websockets.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.websockets.model.BaseResponse;
import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.Status;
import morbrian.websockets.persistence.Persistence;
import morbrian.websockets.persistence.Repository;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

// these authorization tags aren't working, not sure why
//@RolesAllowed({"admin"})
@Path("/forum") @RequestScoped public class ForumRestApi {

  @Inject private Persistence persistence;
  @Inject private Repository repository;
  @Inject private Principal principal;
  @Inject private Logger logger;

  @GET @Path("/") @Produces(MediaType.APPLICATION_JSON) public List<ForumEntity> forumList() {
    return new ArrayList<>();
  }

  @POST @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
  public ForumEntity modifyForum(@PathParam("id") String forumId, ForumEntity forum) {
    return null;
  }

  @PUT @Path("/") @Produces(MediaType.APPLICATION_JSON)
  public ForumEntity createForum(ForumEntity forum, @Context final HttpServletResponse response)
      throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    logger.info("RECEIVED: " + mapper.writeValueAsString(forum));
    try {
      persistence.createForum(forum);
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.flushBuffer();
    } catch (Exception exc) {
      logger.error("CREATE FAILED", exc);
      BaseResponse base = new BaseResponse(new Status(Status.Type.ERROR, "create failed"));
      Response error = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(base).build();
      throw new WebApplicationException(error);
    }
    return forum;
  }

  @DELETE @Path("/{id}") @Produces(MediaType.APPLICATION_JSON) public void deleteForum() {

  }

}
