package com.keville.ReBoggled.sse;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Component
public class EventDispatchUtil {

    private static final Logger LOG = LoggerFactory.getLogger(EventDispatchUtil.class);

    public static boolean ssePulseCheck(SseEmitter emitter,String failMessage) {

      SseEventBuilder event = SseEmitter.event()
        .id("1234")
        .name("pulse_check");

      try {
        emitter.send(event);
        return true;
      } catch (Exception e) {
        LOG.info(failMessage);
        emitter.completeWithError(e);
      }
      return false;

    }

}
