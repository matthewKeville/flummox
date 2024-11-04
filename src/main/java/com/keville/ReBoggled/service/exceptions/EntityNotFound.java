package com.keville.ReBoggled.service.exceptions;

public class EntityNotFound extends RuntimeException {
  private String entityType = ""; 
  private int id = -1;
  public EntityNotFound(String entityType,int id) {
    this.entityType = entityType;
    this.id = id;
  }
}
