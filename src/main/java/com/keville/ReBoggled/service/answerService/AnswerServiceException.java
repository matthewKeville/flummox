package com.keville.ReBoggled.service.answerService;

    public class AnswerServiceException extends Exception {

      public AnswerServiceError error;

      public AnswerServiceException(AnswerServiceError error) {
        this.error = error;
      }

      @Override
      public String getMessage() {
        return error.toString();
      }

      public enum AnswerServiceError {
        ERROR
      }

    }
