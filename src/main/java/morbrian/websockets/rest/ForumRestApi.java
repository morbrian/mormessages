package morbrian.websockets.rest;

import morbrian.websockets.controller.Controller;
import morbrian.websockets.model.BaseEntity;
import morbrian.websockets.model.BaseResponse;
import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.MessageEntity;
import morbrian.websockets.model.Status;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Supplier;

@Path("/forum") @RequestScoped public class ForumRestApi {

  @Inject private Logger logger;
  @Inject private Controller controller;

  @GET @Path("/") @Produces(MediaType.APPLICATION_JSON)
  public List<ForumEntity> listForums(@QueryParam("offset") Integer offset,
      @QueryParam("resultSize") Integer resultSize) {
    return controller.listForums(offset, resultSize);
  }

  @GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
  public ForumEntity getForumById(@PathParam("id") Long forumId) {
    return controller.getForumById(forumId);
  }

  @POST @Path("/{id}") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public ForumEntity modifyForum(@PathParam("id") Long forumId, ForumEntity forum) {
    if (!forumId.equals(forum.getId())) {
      BaseResponse base = new BaseResponse(
          new Status(Status.Type.ERROR, "url path forumId does not match posted forum.id"));
      Response error = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(base).build();
      throw new WebApplicationException(error);
    }
    return controller.modifyForum(forum);
  }

  @PUT @Path("/") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public ForumEntity createForum(ForumEntity forum, @Context final HttpServletResponse response)
      throws Exception {
    // fail for title conflict
    if (controller.titleExists(forum.getTitle())) {
      BaseResponse base = new BaseResponse(
          new Status(Status.Type.ERROR, "forum already exists for title " + forum.getTitle()));
      Response error = Response.status(Response.Status.CONFLICT).entity(base).build();
      throw new WebApplicationException(error);
    }
    // fail for id already set
    if (forum.getId() != null) {
      BaseResponse base = new BaseResponse(new Status(Status.Type.ERROR,
          "cannot create forum with preset id(" + forum.getId() + "); try excluding id"));
      Response error = Response.status(Response.Status.CONFLICT).entity(base).build();
      throw new WebApplicationException(error);
    }
    return (ForumEntity) createEntity(() -> controller.createForum(forum), response);
  }

  @DELETE @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
  public void deleteForum(@PathParam("id") Long forumId) {
    controller.deleteForum(forumId);
  }

  @GET @Path("/{id}/message") @Produces(MediaType.APPLICATION_JSON)
  public List<MessageEntity> listMessages(@PathParam("id") Long forumId,
      @QueryParam("offset") Integer offset, @QueryParam("resultSize") Integer resultSize) {
    return controller.listMessagesInForum(forumId, offset, resultSize);
  }

  @PUT @Path("/{id}/message") @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public MessageEntity postMessageToForum(MessageEntity message, @PathParam("id") Long forumId,
      @Context final HttpServletResponse response) throws Exception {
    return (MessageEntity) createEntity(() -> controller.postMessageToForum(message, forumId),
        response);
  }

  private BaseEntity createEntity(Supplier<? extends BaseEntity> creator,
      HttpServletResponse response) {
    try {
      BaseEntity createdMessage = creator.get();
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.flushBuffer();
      return createdMessage;
    } catch (Exception exc) {
      BaseResponse base = new BaseResponse(new Status(Status.Type.ERROR, exc.getMessage()));
      Response error = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(base).build();
      throw new WebApplicationException(error);
    }
  }
}
