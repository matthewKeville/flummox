package com.keville.ReBoggled.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keville.ReBoggled.DTO.UpdateLobbyDTO;
import com.keville.ReBoggled.config.SecurityConfig;
import com.keville.ReBoggled.context.TestingContext;
import com.keville.ReBoggled.model.GameSettings;
import com.keville.ReBoggled.model.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.security.AuthenticationSuccessHandlerImpl;
import com.keville.ReBoggled.service.LobbyService;
import com.keville.ReBoggled.service.UserService;
import com.keville.ReBoggled.service.LobbyService.LobbyServiceResponse;


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

  @BeforeAll
  static void createUpdatePayloads() throws JsonProcessingException {

    //Will map to an UpdateLobbyDTO will nulls for all fields
    invalidUpdateDTOJson  = "{\"bad\" : \"data\"}";

    UpdateLobbyDTO valid = new UpdateLobbyDTO();
    valid.name = "test";
    valid.capacity = 6;
    valid.isPrivate = false;
    valid.gameSettings = new GameSettings();
    validUpdateDTOJson = mapper.writeValueAsString(valid);
  }

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
      .thenReturn(new LobbyServiceResponse(true));

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

}
