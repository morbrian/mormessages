package morbrian.mormessages.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import morbrian.mormessages.controller.Controller;
import morbrian.mormessages.dataformat.FormatConstants;
import morbrian.mormessages.model.BaseEntity;
import morbrian.mormessages.model.BaseResponse;
import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.MessageEntity;
import morbrian.mormessages.model.Status;
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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.function.Supplier;

@Path("/forum") @RequestScoped public class ForumRestApi {

  @Inject private Logger logger;
  @Inject private Controller controller;

  @GET @Path("/") @Produces(MediaType.APPLICATION_JSON)
  public List<ForumEntity> listForums(@QueryParam("offset") Integer offset,
      @QueryParam("resultSize") Integer resultSize, @QueryParam("greaterThan") String greaterThan) {
    return controller.listForums(offset, resultSize, parseDateString(greaterThan));
  }

  @GET @Path("/{uuid}") @Produces(MediaType.APPLICATION_JSON)
  public ForumEntity getForumByUuid(@PathParam("uuid") String forumUuid) {
    return controller.getForumByUuid(forumUuid);
  }

  @POST @Path("/{uuid}") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public ForumEntity modifyForum(@PathParam("uuid") String forumUuid, ForumEntity forum) {
    if (!forumUuid.equals(forum.getUuid())) {
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
    // fail for uuid conflict (such as when client incorrectly tries to re-use an already created uuid)
    if (controller.uuidExists(forum.getUuid())) {
      BaseResponse base = new BaseResponse(
          new Status(Status.Type.ERROR, "forum already exists for uuid " + forum.getUuid()));
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

  @DELETE @Path("/{uuid}") @Produces(MediaType.APPLICATION_JSON)
  public void deleteForum(@PathParam("uuid") String forumUuid) {
    controller.deleteForum(forumUuid);
  }

  @GET @Path("/{uuid}/message") @Produces(MediaType.APPLICATION_JSON)
  public List<MessageEntity> listMessages(@PathParam("uuid") String forumUuid,
      @QueryParam("offset") Integer offset, @QueryParam("resultSize") Integer resultSize,
      @QueryParam("greaterThan") String greaterThan) {
    return controller
        .listMessagesInForum(forumUuid, offset, resultSize, parseDateString(greaterThan));
  }

  @PUT @Path("/{uuid}/message") @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public MessageEntity postMessageToForum(MessageEntity message,
      @PathParam("uuid") String forumUuid, @Context final HttpServletResponse response)
      throws Exception {
    return (MessageEntity) createEntity(() -> controller.postMessageToForum(message, forumUuid),
        response);
  }

  private Calendar parseDateString(String dateString) {
    if (dateString == null) {
      return null;
    }
    try {
        SimpleDateFormat format = new SimpleDateFormat(FormatConstants.DEFAULT_DATE_FORMAT);
        return new Calendar.Builder().setInstant(format.parse(dateString)).build();
    } catch (ParseException e) {
      BaseResponse base = new BaseResponse(new Status(Status.Type.ERROR,
          "invalid date string; must format like " + FormatConstants.DEFAULT_DATE_FORMAT));
      Response error = Response.status(Response.Status.BAD_REQUEST).entity(base).build();
      throw new WebApplicationException(error);
    }
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
