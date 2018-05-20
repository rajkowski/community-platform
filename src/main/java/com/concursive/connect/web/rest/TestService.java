package com.concursive.connect.web.rest;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")
public class TestService {

  protected static final Log LOG = LogFactory.getLog(TestService.class);

  @GET
  @Path("/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response contentByUniqueId(@PathParam("name") String name) {
//  public Response contentByUniqueId(@QueryParam("name") String name) {

    // Access the user information
    HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
    User user = (User) request.getAttribute(Constants.REST_USER);
    if (user != null) {
      LOG.debug("User is: " + user.getNameFirstLast());
    }

    // @note this is just an example of using a handler to return subset of properties
    String someObject = new String(name);
    if (someObject == null) {
      LOG.debug("Object was not found");
      return Response.status(404).build();
    }
    LOG.debug("Returning the object...");
    TestHandler testHandler = new TestHandler(someObject);
    return Response.status(200).entity(testHandler).build();
  }
}
