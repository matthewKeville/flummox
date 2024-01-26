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
      return new ModelAndView("/registrationSuccess",modelMap);

    } catch (RegistrationServiceException ex) {

      switch ( ex.error ) {

        case EMPTY_EMAIL:
        case EMAIL_TOO_LONG:
        case EMAIL_IN_USE:
          modelMap.addAttribute("error_email",true);
          break;

        case EMTPY_USERNAME:
        case USERNAME_TOO_LONG:
        case USERNAME_IN_USE:
          modelMap.addAttribute("error_username",true);
          break;

        case EMTPY_PASSWORD:
        case PASSWORD_TOO_SHORT:
        case PASSWORD_TOO_LONG:
        case PASSWORD_UNEQUAL:
          modelMap.addAttribute("error_password",true);
          break;
        default:
          modelMap.addAttribute("error_unknown",true);
      }

      modelMap.addAttribute("error",ex.error.name());

    }

    return new ModelAndView("signup",modelMap);

  }

}
