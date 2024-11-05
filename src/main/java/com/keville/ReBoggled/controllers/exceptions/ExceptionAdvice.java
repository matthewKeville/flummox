package com.keville.ReBoggled.controllers.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.keville.ReBoggled.service.exceptions.BadRequest;
import com.keville.ReBoggled.service.exceptions.EntityNotFound;
import com.keville.ReBoggled.service.exceptions.NotAuthorized;
import com.keville.ReBoggled.service.gameService.board.BoardGenerationException;

@ControllerAdvice
public class ExceptionAdvice {

  public static final Logger LOG = LoggerFactory.getLogger(ExceptionAdvice.class);
  public ExceptionAdvice() {
    LOG.info("exception advice bean created");
  }


  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(BoardGenerationException.class)
  public void handleBoardGeneration(BoardGenerationException e) {
    LOG.warn("Exception",e);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({NotAuthorized.class})
  public void handleNotAuthorized(NotAuthorized e) {
    LOG.warn("Exception",e);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({EntityNotFound.class})
  public void handleNotFound(EntityNotFound e) {
    LOG.warn("Exception",e);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({BadRequest.class})
  public void handleNotFound(BadRequest e) {
    LOG.warn("Exception",e);
  }

  /*
  @Bean
  public ExceptionAdvice injectAdvice() {
    return new ExceptionAdvice();
  }
  */

}
