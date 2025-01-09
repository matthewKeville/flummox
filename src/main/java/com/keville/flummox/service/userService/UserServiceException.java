package com.keville.flummox.service.userService;

    public class UserServiceException extends Exception {

      public UserServiceError error;

      public UserServiceException(UserServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

      public enum UserServiceError {
        EMAIL_IN_USE,
        USERNAME_IN_USE,
        MALFORMED_EMAIL,
        MALFORMED_USERNAME,
        MALFORMED_PASSWORD
      }

    }
