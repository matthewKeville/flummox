package com.keville.ReBoggled.service.solutionService;

import java.util.Map;

import com.keville.ReBoggled.model.game.BoardWord;
import com.keville.ReBoggled.model.game.GameSeed;

public interface SolutionService {

  public Map<String,BoardWord> solve(GameSeed seed) throws SolutionServiceException;

}
