package com.keville.ReBoggled.service.lobbyService;

    public class LobbyServiceException extends Exception {

      public LobbyServiceError error;

      public LobbyServiceException(LobbyServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

      public enum LobbyServiceError {
        SUCCESS,
        ERROR,              //internal
        LOBBY_NOT_FOUND,
        USER_NOT_FOUND,
        LOBBY_FULL,
        LOBBY_PRIVATE,
        LOBBY_ALREADY_OWNED,
        GUEST_NOT_ALLOWED,
        USER_NOT_IN_LOBBY,
        CAPACITY_SHORTENING,
        START_GAME_FAILURE
      }

    }
