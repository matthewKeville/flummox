package com.keville.ReBoggled.controllers.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.keville.ReBoggled.DTO.RegisterUserDTO;
import com.keville.ReBoggled.service.registrationService.RegistrationService;
import com.keville.ReBoggled.service.registrationService.RegistrationServiceException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class UserRegistrationController {

  private static final Logger LOG = LoggerFactory.getLogger(UserRegistrationController.class);
  private RegistrationService registrationService;


  public UserRegistrationController(@Autowired RegistrationService registrationService) {
    this.registrationService = registrationService;
  }


  @GetMapping(value = { "/signup" })
  public String showRegistrationForm(@Autowired Model model) {
    RegisterUserDTO registerUserDTO = new RegisterUserDTO();
    model.addAttribute("registration",registerUserDTO);
    return "signup";
  }

  @PostMapping(value = { "/register" })
  public ModelAndView register(
      @ModelAttribute("registration") RegisterUserDTO registerUserDTO
    ) {

    ModelMap modelMap = new ModelMap();

    try {

      registrationService.registerUser(registerUserDTO);

      //TODO :  we should log the user in now as if they authenticated ...
      //I can inject something into the default (authentication success) page
      //that has react or the JS engine display an account creation success toast.
      modelMap.addAttribute("registered",true);
      return new ModelAndView("login",modelMap);

    } catch (RegistrationServiceException ex) {

      switch ( ex.error ) {

        case EMPTY_EMAIL:
          modelMap.addAttribute("error_email",true);
          modelMap.addAttribute("error","Email cannot be empty.");
          break;
        case EMAIL_TOO_LONG:
          modelMap.addAttribute("error_email",true);
          modelMap.addAttribute("error","Invalid email.");
          break;
        case EMAIL_IN_USE:
          // Is it okay to release this information?
          // I don't think we should leak data about what emails are signed up.
          modelMap.addAttribute("error_email",true);
          modelMap.addAttribute("error","Invalid email.");
          break;

        case EMTPY_USERNAME:
          modelMap.addAttribute("error_username",true);
          modelMap.addAttribute("error","Username cannot be empty.");
          break;
        case USERNAME_TOO_LONG:
          modelMap.addAttribute("error_username",true);
          //TODO : I should let the user know what that limit is
          modelMap.addAttribute("error","Username exceeds character limit.");
          break;
        case USERNAME_IN_USE:
          modelMap.addAttribute("error_username",true);
          modelMap.addAttribute("error","Username taken.");
          break;

        case EMTPY_PASSWORD:
          modelMap.addAttribute("error_password",true);
          modelMap.addAttribute("error","Password cannot be empty.");
          break;
        case PASSWORD_TOO_SHORT:
          //TODO : show min
          modelMap.addAttribute("error_password",true);
          modelMap.addAttribute("error","Password is too short.");
          break;
        case PASSWORD_TOO_LONG:
          //TODO : show max
          modelMap.addAttribute("error_password",true);
          modelMap.addAttribute("error","Password is too long.");
          break;
        case PASSWORD_UNEQUAL:
          modelMap.addAttribute("error_password",true);
          modelMap.addAttribute("error","The passwords do not match.");
          break;

        default:
          modelMap.addAttribute("error_unknown",true);
          modelMap.addAttribute("error","Unable to fulfill registration.");
      }

    }

    return new ModelAndView("signup",modelMap);

  }

}
