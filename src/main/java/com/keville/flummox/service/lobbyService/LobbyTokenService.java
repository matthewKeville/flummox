package com.keville.flummox.service.lobbyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class LobbyTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(LobbyTokenService.class);
    private Map<Integer,String> tokens = new HashMap<Integer,String>();

    public LobbyTokenService(){}

    public String getLobbyToken(int lobbyId) {

      if ( tokens.containsKey(lobbyId) ) {
        return tokens.get(lobbyId);
      }
    
      // TODO : more robust obfuscation
      Integer tokenNum = lobbyId*7919;
      byte[] bytes = ByteBuffer.allocate(4).putInt(tokenNum).array();
      String lobbyToken = Base64.getUrlEncoder().encodeToString(bytes);

      tokens.put(lobbyId,lobbyToken);

      return lobbyToken;
    }

}
