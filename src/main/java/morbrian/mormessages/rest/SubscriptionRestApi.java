package morbrian.mormessages.rest;


import morbrian.mormessages.controller.Controller;
import morbrian.mormessages.controller.Subscription;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/subscription") @RequestScoped public class SubscriptionRestApi {

  @Inject private Logger logger;
  @Inject private Controller controller;

  @GET @Path("/") @Produces(MediaType.APPLICATION_JSON)
  public List<Subscription> listSubscriptions() {
    List<Subscription> subscriptions = controller.listSubscriptions();

    System.out.println("COUNT OF SUBS: " + ((subscriptions != null) ? subscriptions.size() : 0));
    return subscriptions;
  }

  @GET @Path("/subscription/{subscriptionId}") @Produces(MediaType.APPLICATION_JSON)
  public Subscription getSubscription(@PathParam("subscriptionId") String subscriptionId) {
    Subscription subscription = controller.getSubscription(subscriptionId);
    if (subscription != null) {
      return subscription;
    } else {
      Response error = Response.status(Response.Status.NOT_FOUND)
          .entity("no subscription for id " + subscriptionId).build();
      throw new WebApplicationException(error);
    }
  }



}
