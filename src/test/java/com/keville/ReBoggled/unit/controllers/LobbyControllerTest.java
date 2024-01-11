package com.keville.ReBoggled.unit.controllers;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.keville.ReBoggled.sse.LobbySseEventDispatcher;
import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.DTO.LobbyViewDTO;
import com.keville.ReBoggled.config.SecurityConfig;
import com.keville.ReBoggled.controllers.LobbyController;
import com.keville.ReBoggled.model.game.GameSettings;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.lobby.LobbyUserReference;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.security.AuthenticationSuccessHandlerImpl;
import com.keville.ReBoggled.service.lobbyService.LobbyService;
import com.keville.ReBoggled.service.lobbyService.LobbyServiceException;
import com.keville.ReBoggled.service.lobbyService.LobbyServiceException.LobbyServiceError;
import com.keville.ReBoggled.service.userService.UserService;
import com.keville.ReBoggled.service.view.LobbyViewService;
import com.keville.ReBoggled.unit.context.TestingContext;

@ContextConfiguration(classes = { TestingContext.class , SecurityConfig.class, LobbyController.class, AuthenticationSuccessHandlerImpl.class })
@WebMvcTest(LobbyController.class)
@AutoConfigureTestDatabase
public class LobbyControllerTest {

  @MockBean
  private UserService userService;
  @MockBean
  private LobbyService lobbyService;
  @MockBean
  private LobbyViewService lobbyViewService;

  @MockBean
  private LobbyRepository lobbies;
  @MockBean
  private UserRepository users;
  @MockBean
  private LobbySseEventDispatcher LobbySseEventDispatcher;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getLobbyViewReturnsLobbyViews() throws Exception {

    Fixture fixture = new Fixture();

    when(lobbyViewService.getLobbyViewDTOs()).thenReturn(fixture.lobbyDTOs);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/lobby/view/lobby"))
      .andExpect(MockMvcResultMatchers.status().is(200))
      .andExpect(MockMvcResultMatchers.header().stringValues("Content-type", "application/json"))

      .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(0))
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("roomA"))
      //...

      .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(1))
      .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("roomB"));
      //...
  };

  @Test
  void getLobbyViewReturnsLobbyView() throws Exception {

    Fixture fixture = new Fixture();

    when(lobbyViewService.getLobbyViewDTO(0)).thenReturn(fixture.roomADto);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/lobby/0/view/lobby"))
      .andExpect(MockMvcResultMatchers.status().is(200))
      .andExpect(MockMvcResultMatchers.header().stringValues("Content-type", "application/json"))

      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("roomA"));
      //...
  };

  @Test
  @Disabled
  void getLobbySseReturnsSse() {};

  @Test
  void joinLobbyReturnsLobby() throws Exception {

    Fixture fixture = new Fixture();
    Integer userId = 1234;

    when(lobbyService.addUserToLobby(userId,0)).thenReturn(fixture.roomA);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/0/join")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().is(200))
      .andExpect(MockMvcResultMatchers.header().stringValues("Content-type", "application/json"))

      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("roomA"));
  };


  private static Stream<Arguments> provideErrorsForJoinLobby() {
    return Stream.of(
      Arguments.of(LobbyServiceError.LOBBY_PRIVATE,HttpStatus.CONFLICT,"LOBBY_IS_PRIVATE"),
      Arguments.of(LobbyServiceError.LOBBY_FULL,HttpStatus.CONFLICT,"LOBBY_IS_FULL")
    );
  }

   
    // Note : MockMvc's error response (what is generated from ResponseStatusException) is different
    // than Spring Boots (which generates a json response body).
   
  @ParameterizedTest
  @MethodSource("provideErrorsForJoinLobby")
  void joinLobbyReturnsReasonForFailure(LobbyServiceError error,HttpStatus status,String msg) throws Exception {

    LobbyServiceException exception = new LobbyServiceException(error);
    Integer userId = 1234;

    when(lobbyService.addUserToLobby(userId,0)).thenThrow(exception);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/0/join")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().is(status.value()))
      .andExpect(MockMvcResultMatchers.status().reason(msg));
  };

  @Test
  @WithMockUser(username = "user@email.com")
  void kickPlayerReturnsLobby() throws Exception {

    Fixture fixture = new Fixture();

    Integer userId = 1234;

    when(lobbyService.getLobbyOwnerId(0)).thenReturn(userId);
    when(lobbyService.removeUserFromLobby(1,0)).thenReturn(fixture.roomA);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/0/kick/1")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.header().stringValues("Content-type", "application/json"))

      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("roomA"));

  };

  @Test
  @WithMockUser(username = "user@email.com")
  void kickPlayerFailsWhenNotOwner() throws Exception {

    Integer userId = 1234;

    when(lobbyService.getLobbyOwnerId(0)).thenReturn(4321);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/0/kick/1")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().isForbidden());

  };

  @Test
  void leaveLobbyReturnsLobby() throws Exception {

    Fixture fixture = new Fixture();
    Integer userId = 1234;

    when(lobbyService.removeUserFromLobby(userId,0)).thenReturn(fixture.roomA);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/0/leave")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().is(200))
      .andExpect(MockMvcResultMatchers.header().stringValues("Content-type", "application/json"))

      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("roomA"));

  };

  @Test
  @WithMockUser(username = "user@email.com")
  void promotePlayerReturnsLobby() throws Exception {

    Fixture fixture = new Fixture();
    Integer userId = 1234;

    User user = User.createUser("user@email.com", "user");
    user.id = userId;

    when(userService.getUser(0)).thenReturn(user);
    when(lobbyService.getLobbyOwnerId(0)).thenReturn(userId);
    when(lobbyService.transferLobbyOwnership(0,1)).thenReturn(fixture.roomA);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/0/promote/1")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.header().stringValues("Content-type", "application/json"))

      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("roomA"));

  };

  @Test
  @WithMockUser(username = "user@email.com")
  void promotePlayerFailsWhenNotOwner() throws Exception {

    Fixture fixture = new Fixture();
    Integer userId = 1234;

    User user = User.createUser("user@email.com", "user");
    user.id = userId;

    when(userService.getUser(0)).thenReturn(user);
    when(lobbyService.getLobbyOwnerId(0)).thenReturn(4321);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/0/promote/1")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().isForbidden());

  };


  @Test
  @Disabled
  void updateLobbyReturnsLobby() {};
  @Test
  @Disabled
  void updateLobbyFailsWhenMalformedBody() {};
  @Test
  @Disabled
  void updateLobbyFailsWhenNotOwner() {};
  @Test
  @Disabled
  void updateLobbyReturnsReasonForFailure() {};

  @Test
  @WithMockUser(username = "user@email.com")
  void createLobbyReturnsLobby() throws Exception {

    Fixture fixture = new Fixture();
    Integer userId = 1234;

    User user = User.createUser("user@email.com","user");

    when(userService.getUser(userId)).thenReturn(user);
    when(lobbyService.createNew(userId)).thenReturn(fixture.roomA);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/create")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("roomA"));

  };

  @Test
  @WithMockUser(username = "guest@email.com")
  void createLobbyFailsIfUserIsGuest() throws Exception {

    Fixture fixture = new Fixture();
    Integer userId = 1234;

    User guest = User.createUser("guest@email.com","guest");
    guest.guest = true;

    when(userService.getUser(userId)).thenReturn(guest);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/lobby/create")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().isConflict())
      .andExpect(MockMvcResultMatchers.status().reason("GUEST_CANT_CREATE_LOBBY"));

  };

  @Test
  @WithMockUser(username = "user@email.com")
  void deleteLobbyReturnsOk() throws Exception {

    Fixture fixture = new Fixture();
    Integer userId = 1234;

    when(lobbyService.delete(0)).thenReturn(true);
    when(lobbyService.getLobbyOwnerId(0)).thenReturn(userId);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/lobby/0")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().is(200));

  };

  @Test
  @WithMockUser(username = "user@email.com")
  void deleteLobbyFailsWhenNotOwner() throws Exception {

    Fixture fixture = new Fixture();
    Integer userId = 1234;

    when(lobbyService.getLobbyOwnerId(0)).thenReturn(5678);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/lobby/0")
      .sessionAttr("userId",userId))
      .andExpect(MockMvcResultMatchers.status().is(403));

  };

  public class Fixture {

    public User userA;
    public User userB;
    public Lobby roomA;
    public Lobby roomB;
    public LobbyViewDTO roomADto;
    public LobbyViewDTO roomBDto;
    public List<LobbyViewDTO> lobbyDTOs;
    public String lobbyDTOsJson;

    public Fixture() {

      //USERS

      userA = new User();
      userA.id = 0;
      userA.email = "userA@email.com";
      userA.username = "userA";
      userA.verified = false;
      userA.guest = false;

      userB = new User();
      userB.id = 1;
      userB.email = "userB@email.com";
      userB.username = "userB";
      userB.verified = false;
      userB.guest = false;

      //LOBBY

      roomA = new Lobby();
      roomA.id = 0;
      roomA.name = "roomA";
      roomA.capacity = 6;
      roomA.isPrivate = false;
      roomA.gameSettings = new GameSettings();
      roomA.owner = AggregateReference.to(0);

      HashSet<LobbyUserReference> roomAUsers = new HashSet<LobbyUserReference>();
      roomAUsers.add(new LobbyUserReference(AggregateReference.to(0)));
      roomAUsers.add(new LobbyUserReference(AggregateReference.to(1)));
      roomA.users = roomAUsers;

      roomB = new Lobby();
      roomB.id = 1;
      roomB.name = "roomB";
      roomB.capacity = 6;
      roomB.isPrivate = false;
      roomB.gameSettings = new GameSettings();
      roomB.owner = AggregateReference.to(1);
      HashSet<LobbyUserReference> roomBUsers = new HashSet<LobbyUserReference>();
      roomB.users = roomBUsers;

      //DTOS

      roomADto = new LobbyViewDTO(roomA);
      roomADto.owner = new LobbyUserDTO(userA);
      List<LobbyUserDTO> roomADtoLobbyUsers = new ArrayList<LobbyUserDTO>();
      roomADtoLobbyUsers.add(new LobbyUserDTO(userA));
      roomADtoLobbyUsers.add(new LobbyUserDTO(userB));
      roomADto.users = roomADtoLobbyUsers;

      roomBDto = new LobbyViewDTO(roomB);
      roomBDto.owner = new LobbyUserDTO(userB);
      List<LobbyUserDTO> roomBDtoLobbyUsers = new ArrayList<LobbyUserDTO>();
      roomBDto.users = roomBDtoLobbyUsers;

      lobbyDTOs = new ArrayList<LobbyViewDTO>();
      lobbyDTOs.add(roomADto);
      lobbyDTOs.add(roomBDto);


      // Expected JSON response
      
    }
  }

}
