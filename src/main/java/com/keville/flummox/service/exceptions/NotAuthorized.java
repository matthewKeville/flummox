package com.keville.flummox.service.exceptions;

public class NotAuthorized extends RuntimeException {
  public NotAuthorized(String msg) {
    super(msg);
  }
}
