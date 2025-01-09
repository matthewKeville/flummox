package com.keville.flummox.sse.context;

public class LobbyContext extends SseContext {

  public int lobbyId;

  public LobbyContext(int userId,int lobbyId) {
    super(userId);
    this.lobbyId = lobbyId;
  }

  @Override
  public String toString() {
    return String.format("userId : %d \t lobbyId : %d",userId,lobbyId);
  }

  @Override
  public int hashCode() {
    return (userId+1)*31 + (lobbyId+1)*37;
  }

  @Override
  public boolean equals(Object obj) {

    if ( obj.getClass() != this.getClass() ) {
      return false;
    }

    LobbyContext context = (LobbyContext) obj;

    return 
      context.userId == this.userId &&
      context.lobbyId == this.lobbyId;

  }

}
