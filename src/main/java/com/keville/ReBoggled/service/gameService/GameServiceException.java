package com.keville.ReBoggled.service.gameService;

    public class GameServiceException extends Exception {

      public GameServiceError error;

      public GameServiceException(GameServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

      public enum GameServiceError {
        ERROR,
        GAME_NOT_FOUND,
        USER_NOT_FOUND,
        INVALID_ANSWER,
        ANSWER_ALREADY_FOUND,
        GAME_OVER
      }

    }
