package com.keville.ReBoggled.service.exceptions;

public class NotAuthorized extends RuntimeException {
  public NotAuthorized(String msg) {
    super(msg);
  }
}
