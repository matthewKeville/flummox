package com.keville.ReBoggled.service.registrationService;

    public class RegistrationServiceException extends Exception {

      public RegistrationServiceError error;

      public RegistrationServiceException(RegistrationServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

      public enum RegistrationServiceError {

        EMPTY_EMAIL,
        EMTPY_USERNAME,
        EMTPY_PASSWORD,

        EMAIL_TOO_LONG,
        USERNAME_TOO_LONG,
        USERNAME_TOO_SHORT,
        PASSWORD_TOO_SHORT,
        PASSWORD_TOO_LONG,

        PASSWORD_UNEQUAL,

        EMAIL_IN_USE,
        USERNAME_IN_USE,

        EMAIL_NOT_FOUND,
        BAD_VERIFICATION_TOKEN

        //TODO
        //MALFORMED_EMAIL,
        //USERNAME_FORBIDDEN (hate speech)


      }

    }
