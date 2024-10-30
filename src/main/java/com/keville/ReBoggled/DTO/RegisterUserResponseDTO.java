package com.keville.ReBoggled.DTO;

import java.util.Optional;

import javax.swing.text.html.Option;

public class RegisterUserResponseDTO {
  public boolean success;
  public Optional<String> errorEmail = Optional.empty();
  public Optional<String> errorUsername = Optional.empty();
  public Optional<String> errorPassword = Optional.empty();
  public Optional<String> errorGeneral = Optional.empty();

  public RegisterUserResponseDTO(){};
  public RegisterUserResponseDTO checkSuccess() {
    success = errorEmail.isEmpty() 
      && errorUsername.isEmpty()
      && errorPassword.isEmpty()
      && errorGeneral.isEmpty();
    return this;
  }
}
