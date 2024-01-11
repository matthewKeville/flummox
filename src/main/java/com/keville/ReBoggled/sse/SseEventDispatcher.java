package com.keville.ReBoggled.sse;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Component
public abstract class SseEventDispatcher {

  private static final Logger LOG = LoggerFactory.getLogger(SseEventDispatcher.class);
  
  protected void emitEvent(SseEmitter sseEmitter,SseEventBuilder sseEvent) {

    try {

      LOG.info("dispatching event " + sseEvent.toString() + " to emitter " + sseEmitter.toString());
      sseEmitter.send(sseEvent);

    } catch (Exception e) {

      LOG.error(String.format("unexpected error dispatching events for emitter " + sseEmitter.toString()));
      LOG.error(e.getMessage());
      sseEmitter.completeWithError(e);

    }
  }

  protected void tryEmitEvent(SseEmitter sseEmitter,SseEventBuilder sseEvent,String pulseFailMessage) {
    if (EventDispatchUtil.ssePulseCheck(sseEmitter,pulseFailMessage)) {
      emitEvent(sseEmitter,sseEvent);
    }
  }

  protected void tryEmitEvents(Collection<SseEmitter> sseEmitters,SseEventBuilder sseEvent,String pulseFailMessage) {
    sseEmitters.forEach( emitter -> {
      tryEmitEvent(emitter, sseEvent,pulseFailMessage);
    });
  }

}
