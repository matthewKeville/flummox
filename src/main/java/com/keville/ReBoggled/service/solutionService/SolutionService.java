package com.keville.ReBoggled.service.solutionService;

import java.util.List;
import com.keville.ReBoggled.model.game.GameSeed;

public interface SolutionService {

  public List<String> solve(GameSeed seed);

}
