package morbrian.mormessages.rest;


import morbrian.mormessages.controller.Subscription;
import morbrian.mormessages.controller.SubscriptionManager;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Path("/subscription") @RequestScoped public class SubscriptionRestApi {

  @Inject private Logger logger;
  @Inject private SubscriptionManager subscriptionManager;
  @Inject private Principal principal;

  @GET @Path("/") @Produces(MediaType.APPLICATION_JSON)
  public List<Subscription> listSubscriptions() {
    return subscriptionManager.listSubscriptions(principal.getName());
  }

  @GET @Path("/{subscriptionId}") @Produces(MediaType.APPLICATION_JSON)
  public Subscription getSubscription(@PathParam("subscriptionId") String subscriptionId) {
    Subscription subscription = subscriptionManager.getSubscription(subscriptionId);
    if (subscription != null) {
      return subscription;
    } else {
      Response error = Response.status(Response.Status.NOT_FOUND)
          .entity("no subscription for id " + subscriptionId).build();
      throw new WebApplicationException(error);
    }
  }

  @PUT @Path("/") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public Subscription createSubscription(Subscription subscription,
      @Context final HttpServletResponse response) throws IOException {
    String userIdentity = (principal != null) ? principal.getName() : "";
    String topicId = subscription.getTopicId();
    if (userIdentity == null || !userIdentity.equals(subscription.getUserIdentity())) {
      Response error = Response.status(Status.BAD_REQUEST)
          .entity("subscriptions can only be created for the user making the request").build();
      throw new WebApplicationException(error);
    }

    Subscription created = subscriptionManager.createSubscription(topicId, userIdentity);
    response.setStatus(HttpServletResponse.SC_CREATED);
    response.flushBuffer();
    logger.info("(create-subscription) user(" + userIdentity + ") on topic (" + topicId + ")");
    return created;
  }

  @POST @Path("/{subscriptionId}") @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Subscription modifySubscription(@PathParam("subscriptionId") String subscriptionId, Subscription subscription) {
    String userIdentity = (principal != null) ? principal.getName() : "";
    if (userIdentity == null || !userIdentity.equals(subscription.getUserIdentity())) {
      Response error = Response.status(Status.BAD_REQUEST)
          .entity("subscriptions can only be modified for the user making the request").build();
      throw new WebApplicationException(error);
    }
    if (subscriptionId == null || !subscriptionId.equals(subscription.getSubscriptionId())) {
      Response error = Response.status(Status.BAD_REQUEST)
          .entity("subscriptionId on request path does not match subscriptiom in request body").build();
      throw new WebApplicationException(error);
    }
    String topicId = subscription.getTopicId();
    Subscription storedSubscription =
        subscriptionManager.getSubscription(subscription.getSubscriptionId());
    if (topicId == null || !topicId.equals(storedSubscription.getTopicId())) {
      Response error = Response.status(Status.BAD_REQUEST).entity(
          "modification of topicId not allowd; delete the subscription and create a new one")
          .build();
      throw new WebApplicationException(error);
    }

    return subscriptionManager.renewSubscription(subscription);
  }

  @DELETE @Path("/{subscriptionId}") @Produces(MediaType.APPLICATION_JSON)
  public void deleteSubscription(@PathParam("subscriptionId") String subscriptionId) {
    Subscription subscription = subscriptionManager.getSubscription(subscriptionId);
    if (subscription == null) {
      // nothing to do, it's also possible the subscription belongs to somebody else
      return;
    }
    subscriptionManager.deleteSubscription(subscriptionId);
  }

}
