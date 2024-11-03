package com.keville.ReBoggled.service.solutionService;

    public class SolutionServiceException extends Exception {

      public SolutionServiceError error;

      public SolutionServiceException(SolutionServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

      public enum SolutionServiceError {
        INVALID_BOARD_SIZE,
        INVALID_BOARD_TOPOLOGY,
        INCONSISTENT_BOARD_PARAMETERS
      }

    }
