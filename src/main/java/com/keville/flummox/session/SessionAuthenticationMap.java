package com.keville.flummox.session;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

//hold a mapping of Sessions and Authentications
@Component
public class SessionAuthenticationMap {

    private static final Logger LOG = LoggerFactory.getLogger(SessionAuthenticationMap.class);
    private static Map<Integer,String> UserSessionIds = new HashMap<Integer,String>();
    private static Map<String,HttpSession> Sessions = new HashMap<String,HttpSession>();
    

    public static Integer GetSessionUserId(HttpSession session) {

      if (!UserSessionIds.containsValue(session.getId())) {
        return null;
      }

      Integer entry  = UserSessionIds.entrySet()
        .stream()
        .filter( e -> e.getValue().equals(session.getId()))
        .findFirst()
        .get()
        .getKey();

      return entry;

    }

    public static HttpSession GetUserSession(Integer userId) {

      if (!UserSessionIds.containsKey(userId)) {
        return null;
      }

      return Sessions.get(UserSessionIds.get(userId));

    }

    public static void addUserSession(Integer userId,HttpSession session) {
      UserSessionIds.put(userId,session.getId());
      Sessions.put(session.getId(),session);
      LOG.info("added user session : " + userId + " " + session.getId() + "session count " + UserSessionIds.size());
    }

    public static void removeUserSession(Integer userId) {
      String sessionId = UserSessionIds.remove(userId);
      Sessions.remove(sessionId);
      LOG.info("removed user session for : " + userId + " " + sessionId + "session count " + UserSessionIds.size());
    }

    public static boolean hasSession(HttpSession session) {
      return Sessions.containsKey(session.getId());
    }

    public static int onlineCount() {
      return UserSessionIds.size();
    }

}
