package com.keville.ReBoggled.sse.context;

public class LobbyMessageContext extends SseContext {

  public int lobbyId;

  public LobbyMessageContext(int userId,int lobbyId) {
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

    LobbyMessageContext context = (LobbyMessageContext) obj;

    return 
      context.userId == this.userId &&
      context.lobbyId == this.lobbyId;
  }
}
