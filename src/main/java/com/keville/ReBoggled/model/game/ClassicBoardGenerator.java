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

  private List<Tile> generate(List<String> chars) {
    List<Tile> tiles = new ArrayList<Tile>();
    chars.forEach( die -> {
      char c = die.charAt(random.nextInt(6));
      Integer code = c == 'Q' ? tileCodeStringMap.getCode("Qu") : (int) c;
      tiles.add(new Tile( code ));
    });

    return tiles;
  }

}
