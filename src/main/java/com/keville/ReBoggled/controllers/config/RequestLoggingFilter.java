package com.keville.ReBoggled.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class RequestLoggingFilter {

    //Seperate logging requests with empty log prints
    class PaddingLoggingFilter extends CommonsRequestLoggingFilter {

      @Override
      public void afterRequest(HttpServletRequest request, String message) {
        super.afterRequest(request, message);
        this.logger.debug("");
      }

      @Override
      public void beforeRequest(HttpServletRequest request, String message) {
        this.logger.debug("");
        super.beforeRequest(request, message);
      }
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {

      CommonsRequestLoggingFilter filter = new PaddingLoggingFilter();
      filter.setBeforeMessagePrefix("Before : ");
      filter.setAfterMessagePrefix("After : ");
      filter.setIncludeQueryString(true);
      filter.setIncludePayload(true);
      filter.setMaxPayloadLength(10000);
      filter.setIncludeHeaders(false);
      return filter;
    }

}
