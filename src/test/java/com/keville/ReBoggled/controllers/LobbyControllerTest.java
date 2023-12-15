package com.keville.ReBoggled.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keville.ReBoggled.DTO.LobbyDTO;
import com.keville.ReBoggled.DTO.LobbyUserDTO;
import com.keville.ReBoggled.DTO.UpdateLobbyDTO;
import com.keville.ReBoggled.config.SecurityConfig;
import com.keville.ReBoggled.context.TestingContext;
import com.keville.ReBoggled.model.GameSettings;
import com.keville.ReBoggled.model.Lobby;
import com.keville.ReBoggled.model.LobbyUserReference;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.security.AuthenticationSuccessHandlerImpl;
import com.keville.ReBoggled.service.LobbyService;
import com.keville.ReBoggled.service.UserService;
import com.keville.ReBoggled.util.Conversions;


@ContextConfiguration(classes = { TestingContext.class , SecurityConfig.class, LobbyController.class, AuthenticationSuccessHandlerImpl.class })
@WebMvcTest(LobbyController.class)
@AutoConfigureTestDatabase
public class LobbyControllerTest {

  @MockBean
  private UserService userService;
  @MockBean
  private LobbyService lobbyService;
  @MockBean
  private LobbyRepository lobbies;
  @MockBean
  private UserRepository users;

  @Autowired
  private MockMvc mockMvc;

  private static String validUpdateDTOJson;
  private static String invalidUpdateDTOJson;
  private static ObjectMapper mapper = new ObjectMapper();

  private static User userA;
  private static User userB;
  private static Lobby roomA;
  private static Lobby roomB;
  private static LobbyDTO roomADto;
  private static LobbyDTO roomBDto;
  private static List<LobbyDTO> lobbyDTOs;
  public static String lobbyDTOsJson;


  @BeforeAll
  static void createTestingData() throws JsonProcessingException {

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
    roomA.id = 1;
    roomA.name = "roomB";
    roomA.capacity = 6;
    roomA.isPrivate = false;
    roomA.gameSettings = new GameSettings();
    roomA.owner = AggregateReference.to(1);
    HashSet<LobbyUserReference> roomBUsers = new HashSet<LobbyUserReference>();
    roomA.users = roomBUsers;

    //DTOS

    roomADto = new LobbyDTO(roomA);
    roomADto.owner = new LobbyUserDTO(userA);
    List<LobbyUserDTO> roomADtoLobbyUsers = new ArrayList<LobbyUserDTO>();
    roomADtoLobbyUsers.add(new LobbyUserDTO(userA));
    roomADtoLobbyUsers.add(new LobbyUserDTO(userB));
    roomADto.users = roomADtoLobbyUsers;

    roomBDto = new LobbyDTO(roomB);
    roomBDto.owner = new LobbyUserDTO(userB);
    List<LobbyUserDTO> roomBDtoLobbyUsers = new ArrayList<LobbyUserDTO>();
    roomBDto.users = roomBDtoLobbyUsers;

    List<LobbyDTO> lobbyDTOs = new ArrayList<LobbyDTO>();
    lobbyDTOs.add(roomADto);
    lobbyDTOs.add(roomBDto);

    /*
    lobbyDTOsJson = """
    [
      {
        \"id\":1,\"name\":\"roomA\",\"capacity\":6,\"isPrivate\":false,
        \"gameSettings\":{\"boardSize\":\"FOUR\",\"boardTopology\":\"PLANE\",\"findRule\":\"ANY\",\"duration\":180},
        \"owner\":{\"id\":0,\"username\":\"userA\"},
        \"users\":[
          {\"id\":0,\"username\":\"userA\"},
          {\"id\":1,\"username\":\"userB\"}]
      },
      { 
        \"id\":2,\"name\":\"roomB\",\"capacity\":6,\"isPrivate\":false,
        \"gameSettings\":{\"boardSize\":\"FOUR\",\"boardTopology\":\"PLANE\",\"findRule\":\"ANY\",\"duration\":180},
        \"owner\":{\"id\":1,\"username\":\"userB\"},
        \"users\":[]
      }
    ]
    """;
    */
    lobbyDTOsJson = "{}";

    /*
    lobbyDTOsJson = """
    [
      {
        "id":1,"name":"roomA","capacity":6,"isPrivate":false,
        "gameSettings":{"boardSize":"FOUR","boardTopology":"PLANE","findRule":"ANY","duration":180},
        "owner":{"id":0,"username":"userA"},
        "users":[
          {"id":0,"username":"userA"},
          {"id":1,"username":"userB"}]
      },
      { 
        "id":2,"name":"roomB","capacity":6,"isPrivate":false,
        "gameSettings":{"boardSize":"FOUR","boardTopology":"PLANE","findRule":"ANY","duration":180},
        "owner":{"id":1,"username":"userB"},
        "users":[]
      }
    ]
    """;
    */

    /*
    lobbyDTOsJson = """
    [
      {
        \\"id\\":1,\\"name\\":\\"roomA\\",\\"capacity\\":6,\\"isPrivate\\":false,
        \\"gameSettings\\":{\\"boardSize\\":\\"FOUR\\",\\"boardTopology\\":\\"PLANE\\",\\"findRule\\":\\"ANY\\",\\"duration\\":180},
        \\"owner\\":{\\"id\\":0,\\"username\\":\\"userA\\"},
        \\"users\\":[
          {\\"id\\":0,\\"username\\":\\"userA\\"},
          {\\"id\\":1,\\"username\\":\\"userB\\"}]
      },
      { 
        \\"id\\":2,\\"name\\":\\"roomB\\",\\"capacity\\":6,\\"isPrivate\\":false,
        \\"gameSettings\\":{\\"boardSize\\":\\"FOUR\\",\\"boardTopology\\":\\"PLANE\\",\\"findRule\\":\\"ANY\\",\\"duration\\":180},
        \\"owner\\":{\\"id\\":1,\\"username\\":\\"userB\\"},
        \\"users\\":[]
      }
    ]
    """;
    */
    
  }

  @Test
  @WithMockUser(username="bob@email.com", authorities = {"read","write"} )
  void getLobbyReturnsLobbies() throws Exception {

    System.out.println("json raw");
    System.out.println(lobbyDTOsJson);
    System.out.println("json raw");

    System.out.println("json fake");
    System.out.println(mapper.writeValueAsString(lobbyDTOsJson));
    System.out.println("json fake");

    when(lobbyService.getLobbyDTOs()).thenReturn(lobbyDTOs);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/lobby"))
      .andExpect(MockMvcResultMatchers.status().is(200))
      .andExpect(MockMvcResultMatchers.header().stringValues("Content-type", "application/json"))
      //.andExpect(MockMvcResultMatchers.content().json(lobbyDTOsJson));
      //.andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(lobbyDTOsJson)));
      //.andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(lobbyDTOsJson))); //what in the fuck
      .andDo( x -> { System.out.println("hello"); System.out.println(x.getResponse().getContentAsString()); } );
      //.andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(userA))); //what in the fuck
  };

  /*
  @Test
  void joinLobbyReturnsLobby() {};

  @Test
  void kickPlayerReturnsLobby() {};

  @Test
  void promotePlayerReturnsLobby() {};

  @Test
  void leaveLobbyReturnsLobby() {};

  @Test
  void updateLobbyReturnsLobby() {};

  @Test
  void createLobbyReturnsLobby() {};

  @Test
  void deleteLobbyReturnsOk() {};
  */

  //
  /*
  @Test
  @WithMockUser(username="bob@email.com", authorities = {"read","write"} )
  void nonOwnerCantUpdateLobby() throws Exception {

    //arrange
    User user = User.createUser("bob@email.com","bob42");
    user.id = 1234;

    when(userService.getUser(any(Integer.class))).thenReturn(user);
    when(lobbyService.getLobbyOwnerId(any(Integer.class))).thenReturn(4321);

    //act & assert
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/lobby/1/update")
        .sessionAttr("userId", 1234)
        .header("Content-Type", "application/json")
        .content(validUpdateDTOJson)
        )
      .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username="bob@email.com", authorities = {"write"} )
  void ownerCanUpdateLobby() throws Exception {

    //arrange
    User user = User.createUser("bob@email.com","bob42");
    user.id = 1234;

    when(userService.getUser(any(Integer.class))).thenReturn(user);
    when(lobbyService.getLobbyOwnerId(any(Integer.class))).thenReturn(1234);
    when(lobbyService.update(any(Integer.class),any(UpdateLobbyDTO.class)))
      .thenReturn(null);

    //act & assert
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/lobby/1/update")
        .header("Content-Type", "application/json")
        .content(validUpdateDTOJson)
        .sessionAttr("userId", 1234)
        )
      .andExpect(MockMvcResultMatchers.status().isOk());

  }

  @Test
  @WithMockUser(username="bob@email.com", authorities = {"read","write"} )
  void invalidRequestBodyThrows415() throws Exception {

    //act & assert
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/lobby/1/update")
        .header("Content-Type", "application/json")
        .content(invalidUpdateDTOJson)
        .sessionAttr("userId", 1234)
        )
      .andExpect(MockMvcResultMatchers.status().is(400));

  }
  */

}
