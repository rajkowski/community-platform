package com.concursive.connect.web.rest;

import org.jboss.resteasy.core.Dispatcher;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {
  private Set<Object> singletons = new HashSet<Object>();

  // GET test

  public RestApplication(@Context Dispatcher dispatcher) {
    singletons.add(new TestService());
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }
}
