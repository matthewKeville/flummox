package com.keville.ReBoggled.DTO;

import java.util.List;
import java.util.Set;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.ScoreBoardEntry;
import com.keville.ReBoggled.model.game.UserGameBoardWord;

public class PostGameUserViewDTO {

  public GameViewDTO gameViewDTO;
  public Set<UserGameBoardWord> answers;
  public List<ScoreBoardEntry> scoreBoard;

  public PostGameUserViewDTO(Game game,Set<UserGameBoardWord> answers,List<ScoreBoardEntry> scoreBoard) {
    gameViewDTO = new GameViewDTO(game);
    this.answers = answers;
    this.scoreBoard = scoreBoard;
  }

}
