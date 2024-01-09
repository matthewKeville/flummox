package com.keville.ReBoggled.model.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassicBoardGenerator {

  @Autowired
  private TileCodeStringMap tileCodeStringMap;

  private static Random random = new Random();

  //For these chars q means Qu and should be properly handled.

  //4x4 boggle
  private static List<String> originalBoggleChars = 
    Arrays.asList(
    "AOCSPH",
    "AEANGE",
    "AFPKSF",
    "ATTOWO", 
    "EVLRDY",
    "TLRYTE",
    "SIENEU",
    "LXEDRI",
    "HWEGEN",
    "HRVTEW",
    "OAJBOB",
    "CTUIOM",
    "EITSOS",
    "SYIDTT",
    "MIQNUH",
    "ZNRNHL"
    );

  //5x5 boggle
  private static List<String> bigBoggleChars = 
    Arrays.asList(
    "QBZJXK",
    "TOUOTO",
    "OVWRGR",
    "AAAFSR",
    "AUMEEG",
    "HHLRDO",
    "NHDTHC",
    "LHNROD",
    "AFAISR",
    "YIFASR",
    "TELPCI",
    "SSNSEU",
    "RIYPRH",
    "DORDLN",
    "CCWNST",
    "TTOTEM",
    "SCTIEP",
    "EANDNN",
    "MNNEAG",
    "UOTOWN",
    "AEAEEE",
    "YIFPSR",
    "EEEEMA",
    "ITITIE",
    "ETILIC"
  );

  public ClassicBoardGenerator(@Autowired TileCodeStringMap tileCodeStringMap) {
    this.tileCodeStringMap = tileCodeStringMap;
  }


  public List<Tile> generate(BoardSize boardSize) throws BoardGenerationException {

    if ( boardSize.equals(BoardSize.FOUR)) {
      return generate(originalBoggleChars);
    }

    if ( boardSize.equals(BoardSize.FIVE)) {
      return generate(bigBoggleChars);
    }

    throw new BoardGenerationException("invalid board size for classic generator" + boardSize);
  }

  public List<Tile> generateFromTileString(String tileString) throws BoardGenerationException {
    if ( tileString.length() == 16 || tileString.length() == 25 ) {
      return generate(tileString);
    }
    throw new BoardGenerationException("generateFromTileString only works for 16 or 25 characters not " + tileString.length());
  }

  private List<Tile> generate(List<String> chars) {
    List<Tile> tiles = new ArrayList<Tile>();
    chars.forEach( die -> {
      char c = die.charAt(random.nextInt(6));
      Integer code = c == 'Q' ? tileCodeStringMap.getCode("Qu") : (int) c;
      tiles.add(new Tile( code ));
    });

    return tiles;
  }

  private List<Tile> generate(String tileString) {
    List<Tile> tiles = new ArrayList<Tile>();
 
    for ( int i = 0; i < tileString.length(); i++ ) {
      char c = tileString.charAt(i);
      Integer code = c == 'Q' ? tileCodeStringMap.getCode("Qu") : (int) c;
      tiles.add(new Tile( code ));
    }

    return tiles;
  }

}
