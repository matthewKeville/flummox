package com.keville.ReBoggled.controllers;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keville.ReBoggled.context.TestingContext;
import com.keville.ReBoggled.controllers.UserController.UserInfo;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.service.UserService;

@ContextConfiguration(classes = TestingContext.class)
@WebMvcTest(UserController.class)
@Import(UserController.class)//throws 404 if I don't import...
public class UserControllerTest {

  @MockBean
  UserService userService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser(username="bob@email.com", authorities = {"read"} )
  void getInfoReturnsUserInfo() throws Exception {

    //arrange
    User user = User.createUser("bob@email.com","bob42");
    user.id = 1;
    ObjectMapper mapper = new ObjectMapper();
    when(userService.getUser(any(Integer.class))).thenReturn(user);

    //act & assert
    mockMvc.perform(
        MockMvcRequestBuilders.get("/api/user/info")
        .sessionAttr("userId", 1234)
        )
      .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(new UserInfo(1,"bob42",false))))
      .andExpect(MockMvcResultMatchers.status().isOk());

  }

  @Test
  @WithMockUser(username="bob@email.com", authorities = {"read"} )
  void getInfoThrowsWhenSessionIsEmpty() throws Exception {

    //act & assert
    mockMvc.perform(
        MockMvcRequestBuilders.get("/api/user/info")
        )
      .andExpect(MockMvcResultMatchers.status().isInternalServerError());

  }

  @Test
  @WithMockUser(username="bob@email.com", authorities = {"read"} )
  void getInfoThrowsWhenUserNotFound() throws Exception {

    //arrange
    User user = User.createUser("bob@email.com","bob42");
    when(userService.getUser(any(Integer.class))).thenReturn(null);

    //act & assert
    mockMvc.perform(
        MockMvcRequestBuilders.get("/api/user/info")
        .sessionAttr("userId", 1234)
        )
      .andExpect(MockMvcResultMatchers.status().isInternalServerError());

  }


}
