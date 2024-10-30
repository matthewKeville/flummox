package com.keville.ReBoggled.controllers.web.pages;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.keville.ReBoggled.DTO.RegisterUserDTO;
import com.keville.ReBoggled.DTO.RegisterUserResponseDTO;
import com.keville.ReBoggled.service.registrationService.RegistrationService;
import com.keville.ReBoggled.service.registrationService.RegistrationServiceException;

@Controller
public class UserRegistrationController {

  private static final Logger LOG = LoggerFactory.getLogger(UserRegistrationController.class);
  private RegistrationService registrationService;


  public UserRegistrationController(@Autowired RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping(value = { "/register" })
  public ResponseEntity<?> register(
      @RequestBody RegisterUserDTO registerUserDTO
    ) {

      RegisterUserResponseDTO response = new RegisterUserResponseDTO();

      try {

        registrationService.registerUser(registerUserDTO);
        return new ResponseEntity<RegisterUserResponseDTO>(response.checkSuccess(),HttpStatus.OK);

      } catch (RegistrationServiceException ex) {

        switch ( ex.error ) {

          case EMPTY_EMAIL:
          case EMAIL_TOO_LONG:
          case EMAIL_IN_USE:
            response.errorEmail = Optional.of(ex.error.toString());
            break;

          case EMTPY_USERNAME:
          case USERNAME_TOO_LONG:
          case USERNAME_IN_USE:
            response.errorUsername = Optional.of(ex.error.toString());
            break;

          case EMTPY_PASSWORD:
          case PASSWORD_TOO_SHORT:
          case PASSWORD_TOO_LONG:
          case PASSWORD_UNEQUAL:
            response.errorPassword = Optional.of(ex.error.toString());
            break;

          default:
            response.errorGeneral = Optional.of(ex.error.toString());
            break;
        }

      }
    
      return new ResponseEntity<RegisterUserResponseDTO>(response.checkSuccess(),HttpStatus.BAD_REQUEST);

    }

}
