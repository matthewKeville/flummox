package com.keville.ReBoggled.DTO;

import java.util.Optional;

public class GameAnswerResponseDTO {
    public boolean success;
    public Optional<Rejection> reason;

    public static GameAnswerResponseDTO Accepted() {
      var response = new GameAnswerResponseDTO();
      response.success = true;
      return response;
    }

    public static GameAnswerResponseDTO Rejected(Rejection reason) {
      var response = new GameAnswerResponseDTO();
      response.success = false;
      response.reason = Optional.of(reason);
      return response;
    }

    public enum Rejection {
      ALREADY_FOUND,
      NOT_FOUND
    }

}
