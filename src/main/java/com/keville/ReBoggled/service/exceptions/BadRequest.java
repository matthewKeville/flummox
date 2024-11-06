package com.keville.ReBoggled.service.exceptions;


public class BadRequest extends RuntimeException {
  public BadRequest(String msg) {
    super(msg);
  }
}
