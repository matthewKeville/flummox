package com.keville.ReBoggled.unit.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.security.AuthenticationSuccessHandlerImpl;
import com.keville.ReBoggled.service.userService.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  //SecurityConfig Depends On One
  @MockBean
  AuthenticationSuccessHandlerImpl authSuccessHandler;

  @MockBean
  UserService userService;

  @Autowired
  private MockMvc mockMvc;

  /**
  @Test
  @WithMockUser(username="bob@email.com", authorities = {"read"} )
  void getInfoReturnsUserInfo() throws Exception {

    //arrange
    User user = User.createUser("bob@email.com","bob42");
    user.id = 1234;
    ObjectMapper mapper = new ObjectMapper();
    when(userService.getUser(any(Integer.class))).thenReturn(user);

    //act & assert
    mockMvc.perform(
        MockMvcRequestBuilders.get("/api/user/info")
        .sessionAttr("userId", user.id)
        )
      .andDo( (mvcResult) -> System.out.println(MockMvcResultMatchers.content()))
      .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(new UserInfo(user.id,"bob42",false))))
      .andExpect(MockMvcResultMatchers.status().isOk());

  }
  */

}
