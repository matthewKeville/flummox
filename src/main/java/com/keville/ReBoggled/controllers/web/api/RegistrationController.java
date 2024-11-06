package com.keville.ReBoggled.controllers.web.api;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.keville.ReBoggled.DTO.RegisterUserRequestDTO;
import com.keville.ReBoggled.DTO.RegisterUserResponseDTO;
import com.keville.ReBoggled.DTO.UserVerifyRequestDTO;
import com.keville.ReBoggled.service.registrationService.RegistrationService;
import com.keville.ReBoggled.service.registrationService.RegistrationServiceException;

@Controller
public class RegistrationController {

  private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);
  private RegistrationService registrationService;

  public RegistrationController(@Autowired RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping(value = { "/register" })
  public ResponseEntity<?> register(
      @RequestBody RegisterUserRequestDTO registerUserRequestDTO
    ) {

      RegisterUserResponseDTO response = new RegisterUserResponseDTO();

      try {

        registrationService.registerUser(registerUserRequestDTO);
        return new ResponseEntity<RegisterUserResponseDTO>(response.checkSuccess(),HttpStatus.OK);

      } catch (RegistrationServiceException ex) {

        switch ( ex.error ) {

          case EMPTY_EMAIL:
            response.errorEmail = Optional.of("Email can not be empty");
            break;
          case EMAIL_TOO_LONG:
            response.errorEmail = Optional.of("Max length " + RegistrationService.MAX_EMAIL_LENGTH + " characters");
            break;
          case EMAIL_IN_USE:
            response.errorEmail = Optional.of("Email already in use");
            break;

          case EMTPY_USERNAME:
            response.errorUsername = Optional.of("Username can not be empty");
            break;
          case USERNAME_TOO_LONG:
            response.errorUsername = Optional.of("Max length : " + RegistrationService.MAX_USERNAME_LENGTH);
            break;
          case USERNAME_TOO_SHORT:
            response.errorUsername = Optional.of("Min length : " + RegistrationService.MIN_USERNAME_LENGTH);
            break;
          case USERNAME_IN_USE:
            response.errorUsername = Optional.of("Username already in use");
            break;

          case EMTPY_PASSWORD:
            response.errorPassword = Optional.of("Password can not be empty");
            break;
          case PASSWORD_TOO_SHORT:
            response.errorPassword = Optional.of("Min length " + RegistrationService.MIN_PASSWORD_LENGTH);
            break;
          case PASSWORD_TOO_LONG:
            response.errorPassword = Optional.of("Max length " + RegistrationService.MAX_PASSWORD_LENGTH);
            break;
          case PASSWORD_UNEQUAL:
            response.errorPassword = Optional.of("Passwords do not match");
            break;

          default:
            response.errorGeneral = Optional.of(ex.error.toString());
            break;
        }

      }
    
      return new ResponseEntity<RegisterUserResponseDTO>(response.checkSuccess(),HttpStatus.BAD_REQUEST);

    }

  @PostMapping(value = { "/verify" })
  public ResponseEntity<?> verify(
      @RequestBody UserVerifyRequestDTO userVerifyRequestDTO
    ) {

      try {

        registrationService.verifyEmail(userVerifyRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);

      } catch (RegistrationServiceException ex) {
      }
    
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

}
