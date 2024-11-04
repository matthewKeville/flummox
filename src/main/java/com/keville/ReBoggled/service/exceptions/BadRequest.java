package com.keville.ReBoggled.service.exceptions;


public class BadRequest extends RuntimeException {
  public String reason = "";
  public BadRequest(String reason) {
    this.reason = reason;
  }
}
