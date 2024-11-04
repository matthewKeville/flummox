package com.keville.ReBoggled.service.utils;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.Game;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.GameRepository;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.exceptions.EntityNotFound;

@Component
public class ServiceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceUtils.class);

    public static User getPrincipal() {
      return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Lobby findLobbyById(LobbyRepository lobbies,int lobbyId) throws EntityNotFound {

      Optional<Lobby>  optLobby = lobbies.findById(lobbyId);
      if ( optLobby.isEmpty() ) {
        LOG.warn(String.format("No such lobby %d",lobbyId));
        throw new EntityNotFound("lobby",lobbyId);
      }
      return optLobby.get();
    }

    public static void ensureExists(LobbyRepository lobbies,int lobbyId) throws EntityNotFound {
      if ( !lobbies.existsById(lobbyId) ) {
        throw new EntityNotFound("lobby", lobbyId);
      }
    }

    public static Game findGameById(GameRepository games,int gameId) throws EntityNotFound {

      Optional<Game>  optGame = games.findById(gameId);
      if ( optGame.isEmpty() ) {
        LOG.warn(String.format("No such game %d",gameId));
        throw new EntityNotFound("game",gameId);
      }
      return optGame.get();
    }

    public static void ensureExists(GameRepository games,int gameId) throws EntityNotFound {
      if ( !games.existsById(gameId) ) {
        throw new EntityNotFound("game", gameId);
      }
    }

    public static User findUserById(UserRepository users,int userId) throws EntityNotFound {
      Optional<User> optUser = users.findById(userId);
      if ( !optUser.isPresent() ) {
        LOG.error(String.format("No such user %d",userId));
        throw new EntityNotFound("user",userId);
      }
      return optUser.get();
    }

}
