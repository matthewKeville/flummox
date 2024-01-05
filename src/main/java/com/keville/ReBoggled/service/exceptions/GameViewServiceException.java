package com.keville.ReBoggled.service.exceptions;

    public class GameViewServiceException extends Exception {

      public GameViewServiceError error;

      public GameViewServiceException(GameViewServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

      public enum GameViewServiceError {
        SUCCESS,
        ERROR,
        LOBBY_NOT_FOUND,
        USER_NOT_FOUND,
        GAME_NOT_FOUND
      }

    }
