package com.keville.ReBoggled.DTO;

import java.util.Optional;

public class RegisterUserResponseDTO {
  public boolean success;
  public Optional<String> errorEmail = Optional.empty();
  public Optional<String> errorUsername = Optional.empty();
  public Optional<String> errorPassword = Optional.empty();
  public Optional<String> errorGeneral = Optional.empty();

  public static RegisterUserResponseDTO OK() {
    var response = new RegisterUserResponseDTO();
    response.success = true;
    return response;
  }
}
