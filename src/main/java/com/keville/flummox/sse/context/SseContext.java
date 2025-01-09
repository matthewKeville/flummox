package com.keville.flummox.sse.context;

public abstract class SseContext {

  public abstract String toString();
  public abstract int hashCode();
  public abstract boolean equals(Object obj);

  public int userId;

  public SseContext(int userId) {
    this.userId = userId;
  }

};
