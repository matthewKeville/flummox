package com.keville.ReBoggled.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.BoardSize;
import com.keville.ReBoggled.model.game.Tile;

@Component
public class ClassicBoardGenerator {

  static Random random = new Random();

  //4x4 boggle
  static List<String> originalBoggleChars = 
    Arrays.asList(
    "aocsph",
    "aeange",
    "afpksf",
    "attowo", 
    "evlrdy",
    "tlryte",
    "sieneu",
    "lxedri",
    "hwegen",
    "hrvtew",
    "oajbob",
    "ctuiom",
    "eitsos",
    "syidtt",
    "miqnuh",
    "znrnhl"
    );

  //5x5 boggle
  static List<String> bigBoggleChars = 
    Arrays.asList(
    "qbzjxk",
    "touoto",
    "ovwrgr",
    "aaafsr",
    "aumeeg",
    "hhlrdo",
    "nhdthc",
    "lhnrod",
    "afaisr",
    "yifasr",
    "telpci",
    "ssnseu",
    "riyprh",
    "dordln",
    "ccwnst",
    "ttotem",
    "sctiep",
    "eandnn",
    "mnneag",
    "uotown",
    "aeaeee",
    "yifpsr",
    "eeeema",
    "ititie",
    "etilic"
  );

  public static List<Tile> generate(BoardSize boardSize) throws BoardGenerationException {

    if ( boardSize.equals(BoardSize.FOUR)) {
      return generate(originalBoggleChars);
    }

    if ( boardSize.equals(BoardSize.FIVE)) {
      return generate(bigBoggleChars);
    }

    throw new BoardGenerationException("invalid board size for classic generator" + boardSize);
  }

  private static List<Tile> generate(List<String> chars) {
    List<Tile> tiles = new ArrayList<Tile>();
    chars.forEach( die -> {
      tiles.add(new Tile( (int) die.charAt(random.nextInt(6)) ));
    });

    return tiles;
  }

  public static class BoardGenerationException extends Exception {
    public BoardGenerationException(String msg) {
      super(msg);
    }
  }

}
