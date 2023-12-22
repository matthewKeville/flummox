package com.keville.ReBoggled.controllers.util;

import org.slf4j.Logger;

public class RequestLogger {

  private Logger LOG;
  private String baseRoute;

  public RequestLogger(String baseRoute, Logger log) {
    this.baseRoute = baseRoute;
    this.LOG = log;
  }

  public void log(String type,String route) {
    type = type.toUpperCase();
    LOG.info(type + "\t" + baseRoute + route);
  }

}
