package morbrian.websockets.rest;

import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;

// these authorization tags aren't working, not sure why
//@RolesAllowed({"admin"})
@Path("/world") @RequestScoped public class WorldRestApi {

  @Inject private Principal principal;
  @Inject private Logger logger;

  @GET @Path("/data") @Produces(MediaType.APPLICATION_JSON) public Response getData() {
    return Response.ok("hello " + ((principal != null) ? principal.getName() : null)).build();
  }

}
