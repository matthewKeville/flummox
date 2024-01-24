package com.keville.ReBoggled.service.view;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.DTO.GameAnswerDTO;
import com.keville.ReBoggled.DTO.GameUserViewDTO;
import com.keville.ReBoggled.DTO.GameViewDTO;
import com.keville.ReBoggled.DTO.GameWordDTO;
import com.keville.ReBoggled.DTO.PostGameUserViewDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.gameSummary.GameSummary;
import com.keville.ReBoggled.model.gameSummary.WordFinder;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.service.gameService.GameService;
import com.keville.ReBoggled.service.gameService.GameServiceException;
import com.keville.ReBoggled.service.gameSummaryService.GameSummaryService;
import com.keville.ReBoggled.service.userService.UserService;

@Component
public class GameViewService {

    private static final Logger LOG = LoggerFactory.getLogger(GameViewService.class);

    private GameService gameService;
    private GameSummaryService gameSummaryService;
    private UserService userService;

    public GameViewService(
        @Autowired GameService gameService,
        @Autowired GameSummaryService gameSummaryService,
        @Autowired UserService userService) {
      this.gameService = gameService;
      this.gameSummaryService = gameSummaryService;
      this.userService = userService;
    }

    /* Return a view of a game to the perspective of a user, when the game is ongoing */
    public GameUserViewDTO getGameUserViewDTO(Integer gameId,Integer userId) throws GameViewServiceException,GameServiceException {

      Game game = gameService.getGame(gameId);
      User user = userService.getUser(userId);

      //extract users answers 
      Set<GameAnswerDTO> userAnswers = 
        game.answers.stream()
        .filter( ans -> {
          return ans.user.getId().equals(userId);
        })
        .map( uga ->  new GameAnswerDTO(uga.answer,uga.answerSubmissionTime) )
        .collect(Collectors.toSet());

      return new GameUserViewDTO(game,userAnswers);
    }

    /* Return a view of a game to the perspective of a user, when the game is complete */
    public PostGameUserViewDTO getPostGameUserViewDTO(Integer gameId,Integer userId) throws GameServiceException {

      Game game = gameService.getGame(gameId);
      User user = userService.getUser(userId);

      GameSummary gameSummary = gameSummaryService.getSummary(game);

      //transform gameSummary into set of 'GameWordDTOs' (flavor GameWord for user)
      Set<GameWordDTO> gameWordDTOs = new HashSet<GameWordDTO>();
      gameSummary.gameBoardWords().forEach( gbw -> {

        boolean found   = gbw.finders().stream().anyMatch( (finder) -> finder.id().equals(userId) );
        //some duplicate logic here with the calculation of GameSummary
        boolean counted = false;
        if ( found ) {

          switch ( game.findRule ) {
            case UNIQUE:
              counted = gbw.finders().size() == 1;
              break;
            case FIRST:
              Optional<WordFinder> firstFinder = gbw.finders().stream().sorted( (a,b) -> a.time().compareTo(b.time()) ).findFirst();
              counted = firstFinder.get().id().equals(userId);
              break;
            case ANY:
            default:
              counted = true;
          }

        }

        gameWordDTOs.add(new GameWordDTO(gbw.word(),gbw.paths(),gbw.finders(),gbw.points(),found,counted));

      });

      PostGameUserViewDTO view = new PostGameUserViewDTO(new GameViewDTO(game),gameSummary.scoreboard(),gameWordDTOs);

      return view;

    }

}
