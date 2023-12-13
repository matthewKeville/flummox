/*
package com.keville.ReBoggled.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.keville.ReBoggled.service.LobbyService.LobbyServiceException;

@Component
@ControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(LobbyServiceException.class)
  public <T>ResponseEntity<T> handleLobbyServiceException(LobbyServiceException e) {
    switch (e.error) {
      case LOBBY_FULL:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "LOBBY_IS_FULL");
      case LOBBY_PRIVATE:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "LOBBY_IS_PRIVATE");
      case GUEST_NOT_IMPLEMENT:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "GUEST_NOT_IMPLEMENT");
      case USER_NOT_IN_LOBBY:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "NOT_IN_LOBBY");
      case CAPACITY_SHORTENING:
        throw new ResponseStatusException(HttpStatus.CONFLICT, "CAPACITY_SHORTENING_CONFLICT");
      case ERROR:
      default:
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }
  }

}
*/
