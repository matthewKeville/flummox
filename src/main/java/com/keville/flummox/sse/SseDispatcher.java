package com.keville.flummox.sse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.keville.flummox.sse.context.SseContext;

public abstract class SseDispatcher<T extends SseContext> {

  private final Logger LOG = LoggerFactory.getLogger(getClass());
  protected abstract void sendInitialPayload(SseEmitter emitter,T context);
  public ConcurrentMap<T,SseEmitter> sseMap = new ConcurrentHashMap<>();

  public SseEmitter register(T context) {

    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    Runnable cleanup = () -> {
      unregister(context);
    };

    emitter.onError( (ex) -> {
      cleanup.run();
    });

    emitter.onCompletion( () -> {
      cleanup.run();
    });

    if ( sseMap.containsKey(context) ) {
      LOG.info("the SseContext : " + context.toString() + " already exists, invalidating the extant emitter");
      sseMap.remove(context);
    }

    sseMap.put(context,emitter);
    sendInitialPayload(emitter,context);

    LOG.info("registered emitter : context " + context.toString());
    LOG.info(sseMap.size() + " emitters registered");

    return emitter;

  }

  protected void tryEmitEvent(SseEmitter sseEmitter,SseEventBuilder sseEvent) {
    if (checkHealth(sseEmitter)) {
      emitEvent(sseEmitter,sseEvent);
    }
  }

  private void emitEvent(SseEmitter sseEmitter,SseEventBuilder sseEvent) {

    try {

      LOG.debug("dispatching event " + sseEvent.toString() + " to emitter " + sseEmitter.toString());
      sseEmitter.send(sseEvent);

    } catch (Exception e) {

      LOG.error(String.format("unexpected error dispatching events for emitter " + sseEmitter.toString()));
      LOG.error(e.getMessage());
      sseEmitter.completeWithError(e);

    }
  }

  private void unregister(T context) {

    if ( !sseMap.containsKey(context) ) {
      LOG.warn("failed to remove SseEntry, because no matching context");
      return;
    }

    sseMap.remove(context);
    LOG.info("unregistered emitter : context " + context.toString());

  }

  private boolean checkHealth(SseEmitter emitter) {

    SseEventBuilder event = SseEmitter.event()
      .id("1111")
      .name("pulse_check");

    try {
      emitter.send(event);
      return true;
    } catch (Exception e) {
      emitter.completeWithError(e);
    }
    return false;

  }

  @Scheduled(fixedRate = 60000)
  private void removeExpiredEmitters() {

    int expireCount = 0;

    for ( var entry : sseMap.entrySet() ) {
      if ( checkHealth(entry.getValue()) ) {
        expireCount++;
      }
    }

    if ( expireCount != 0 ) {
      LOG.info(String.format("Removed %d expired emitters. %d remain",expireCount,sseMap.size()));
    }

  }

}
