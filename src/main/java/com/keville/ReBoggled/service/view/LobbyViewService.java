package com.keville.ReBoggled.service.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.DTO.LobbyViewDTO;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.util.Conversions;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LobbyViewService {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyViewService.class);

    private LobbyRepository lobbies;
    private UserRepository users;

    public LobbyViewService(@Autowired LobbyRepository lobbies,
        @Autowired UserRepository users) {
      this.lobbies = lobbies;
      this.users = users;
    }

    public List<LobbyViewDTO> getLobbyViewDTOs() throws LobbyViewServiceException {

      Iterable<Lobby> allLobbies = lobbies.findAll();
      List<LobbyViewDTO> allLobbiesList = new LinkedList<LobbyViewDTO>();

      for ( Lobby lobby : allLobbies ) {
        allLobbiesList.add(createLobbyViewDTO(lobby));
      }
      return allLobbiesList;
    }

    public LobbyViewDTO getLobbyViewDTO(int id) throws LobbyViewServiceException {
      Optional<Lobby> optLobby = lobbies.findById(id);
      if (optLobby.isEmpty()) {
        throw new LobbyViewServiceException(LobbyViewServiceError.LOBBY_NOT_FOUND);
      }
      return createLobbyViewDTO(optLobby.get());
    }

    /* FIXME : To be replaced by a dedicated query method in LobbyRepository */
    private LobbyViewDTO createLobbyViewDTO(Lobby lobby) throws LobbyViewServiceException {

      LobbyViewDTO lobbyDto = new LobbyViewDTO(lobby);

      Optional<User> ownerOpt = users.findById(lobby.owner.getId());
      if ( ownerOpt.isEmpty() ) {
        throw new LobbyViewServiceException(LobbyViewServiceError.USER_NOT_FOUND);
      }
      User owner = ownerOpt.get();

      List<Integer> userIds = lobby.users.stream()
        .map( x -> x.user.getId() )
        .collect(Collectors.toList());

      Iterable<User> lobbyUsers =  users.findAllById(userIds);
      List<User> lobbyUsersList = Conversions.iterableToList(lobbyUsers);

      List<LobbyUserDTO> userDtos = lobbyUsersList.stream().
        map( x -> new LobbyUserDTO(x))
        .collect(Collectors.toList());

      lobbyDto.owner = new LobbyUserDTO(owner);
      lobbyDto.users = userDtos;

      return lobbyDto;

    }

    public enum LobbyViewServiceError {
      SUCCESS,
      ERROR,
      LOBBY_NOT_FOUND,
      USER_NOT_FOUND
    }

    public class LobbyViewServiceException extends Exception {

      public LobbyViewServiceError error;

      public LobbyViewServiceException(LobbyViewServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

    }

}
