package com.keville.ReBoggled.service.view;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.DTO.GameUserViewDTO;
import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.game.GameAnswer;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.exceptions.GameViewServiceException;
import com.keville.ReBoggled.service.exceptions.GameViewServiceException.GameViewServiceError;

@Component
public class GameViewService {

    private static final Logger LOG = LoggerFactory.getLogger(GameViewService.class);

    private UserRepository users;
    private GameRepository games;

    public GameViewService(@Autowired UserRepository users, @Autowired GameRepository games) {
      this.users = users;
      this.games = games;
    }

    public GameUserViewDTO getGameUserViewDTO(Integer gameId,Integer userId) throws GameViewServiceException {

      Optional<Game> optGame = games.findById(gameId);
      if (optGame.isEmpty()) {
        throw new GameViewServiceException(GameViewServiceError.GAME_NOT_FOUND);
      }

      Optional<User> optUser = users.findById(userId);
      if (optUser.isEmpty()) {
        throw new GameViewServiceException(GameViewServiceError.USER_NOT_FOUND);
      }

      //return createGameUserViewDTO(optGame.get(),optUser.get());
      //extract users answers 
      Set<GameAnswer> userAnswers = optGame.get().answers.stream().filter( ans -> {
        return ans.user.getId().equals(userId);
      }).collect(Collectors.toSet());

      return new GameUserViewDTO(optGame.get(),userAnswers);
    }


}
