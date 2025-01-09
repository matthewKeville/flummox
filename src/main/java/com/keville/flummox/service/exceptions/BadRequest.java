package com.keville.flummox.service.exceptions;


public class BadRequest extends RuntimeException {
  public BadRequest(String msg) {
    super(msg);
  }
}
