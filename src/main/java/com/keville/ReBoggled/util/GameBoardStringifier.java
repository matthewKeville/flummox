package com.keville.ReBoggled.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.BoardSize;
import com.keville.ReBoggled.model.game.BoardTopology;
import com.keville.ReBoggled.model.game.Tile;
import com.keville.ReBoggled.model.game.TileCodeStringMap;

@Component
public class GameBoardStringifier {

  private static Logger LOG = LoggerFactory.getLogger(GameBoardStringifier.class);

  @Autowired
  private TileCodeStringMap tileCodeStringMap;

  static int vGap = 3;
  static int hGap = 3;
  static int tileWidth = 5;
  static int vMargin = 4;

  public String stringify(List<Tile> tiles, BoardSize boardSize, BoardTopology boardTopology) {

    int width = 4;
    switch ( boardSize ) {
      case FOUR:
        width = 4;
        break;
      case FIVE:
        width = 5;
        break;
      default:
        LOG.warn("board size " + boardSize + " not supported , printed will be wrong ");
    }

    String result = StringUtils.repeat("\n",vMargin);

    //assumes square boards

    for ( int i = 0; i < width; i++ ) {

      if ( i != 0 && i != width ) {
        result+=StringUtils.repeat("\n",vGap);
      }

      for ( int j = 0; j < width; j++ ) {

        if ( j != 0 && j != width ) {
          result+=StringUtils.repeat(" ",hGap);
        }

        result += tileCodeStringMap.getString( tiles.get(i*width + j).code );
        
      }

    }

    result += StringUtils.repeat("\n",vMargin);

    return result;
  }


}
