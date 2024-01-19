package com.keville.ReBoggled.service.solutionService;

import java.util.Map;

import com.keville.ReBoggled.model.game.Board;
import com.keville.ReBoggled.model.game.BoardWord;

public interface SolutionService {

  public Map<String,BoardWord> solve(Board board) throws SolutionServiceException;

}
