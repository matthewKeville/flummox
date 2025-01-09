package com.keville.flummox.sse.context;

public class GameContext extends SseContext {

  public int gameId;
  
  public GameContext(int userId,int gameId) {
    super(userId);
    this.gameId = gameId;
  }

  @Override
  public String toString() {
    return String.format("userId : %d \t gameId : %d",userId,gameId);
  }

  @Override
  public int hashCode() {
    return (userId+1)*31 + (gameId+1)*37;
  }

  @Override
  public boolean equals(Object obj) {

    if ( obj.getClass() != this.getClass() ) {
      return false;
    }

    GameContext context = (GameContext) obj;

    return 
      context.userId == this.userId &&
      context.gameId == this.gameId;
  }

}
