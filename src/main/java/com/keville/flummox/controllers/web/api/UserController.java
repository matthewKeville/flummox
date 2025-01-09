package com.keville.flummox.controllers.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keville.flummox.DTO.RegisterUserRequestDTO;
import com.keville.flummox.DTO.RegisterUserResponseDTO;
import com.keville.flummox.DTO.UserInfoDTO;
import com.keville.flummox.DTO.UserVerifyRequestDTO;
import com.keville.flummox.service.registrationService.RegistrationService;
import com.keville.flummox.service.userService.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/info")
    public UserInfoDTO info() {
      return userService.getUserInfoDTO();
    }

    @PostMapping("/register")
    public RegisterUserResponseDTO register(
        @RequestBody RegisterUserRequestDTO registerUserRequestDTO
      ) {

      return registrationService.registerUser(registerUserRequestDTO);

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
        @RequestBody UserVerifyRequestDTO userVerifyRequestDTO
      ) {

      registrationService.verifyEmail(userVerifyRequestDTO);
      return ResponseEntity.ok().build();

    }


}
