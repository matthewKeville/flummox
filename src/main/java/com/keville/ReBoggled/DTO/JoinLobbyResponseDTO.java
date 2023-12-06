package com.keville.ReBoggled.DTO;

public class JoinLobbyResponseDTO {

  public boolean  success   = false;
  public String   response  = "";

  public JoinLobbyResponseDTO(boolean success, String response) {
    this.success = success;
    this.response = response;
  }
}
