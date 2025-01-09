package com.keville.flummox.service.gameService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/* 
 * Map Tile code points to character sequences.
 */
@Component
public class TileCodeStringMap {

  private Map<Integer,String> codeMap = new HashMap<Integer,String>();
  public static Logger LOG = LoggerFactory.getLogger(TileCodeStringMap.class);

  public TileCodeStringMap () {

    //use ascii as base
    IntStream.range(0,255).forEach( x -> { codeMap.put(x,""+(char) x); } ); 

    //overwrite specific control tiles

    codeMap.put(0,"");
    codeMap.put(1,"Qu");
    codeMap.put(2,"In");
    codeMap.put(3,"Th");
    codeMap.put(4,"Er");
    codeMap.put(5,"He");
    codeMap.put(6,"An");

  }

  public String getString(Integer code) {
    if ( !codeMap.containsKey(code) ) {
      LOG.warn(String.format("code %d is not mapped to, defaulting to code point 0's value",code));
      return codeMap.get(0);
    }
    return codeMap.get(code);
  }

  public Integer getCode(String string) {
    if ( !codeMap.containsValue(string) ) {
      LOG.warn(String.format("value %s is not mapped to, defaulting to code point 0",string));
      return 0;
    }
    Optional<Entry<Integer,String>> entry = codeMap.entrySet().stream().filter( x -> x.getValue().equals(string) ).findFirst();
    if ( !entry.isPresent() ) {
      LOG.error(String.format("codeMap contains value %s, but unable to find matching entry in stream..",string));
      return 0;
    }
    return entry.get().getKey();
  }


}
