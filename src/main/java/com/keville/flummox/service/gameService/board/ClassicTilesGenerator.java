package com.keville.flummox.service.gameService.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.flummox.model.game.BoardSize;
import com.keville.flummox.model.game.Tile;
import com.keville.flummox.service.gameService.TileCodeStringMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ClassicTilesGenerator {

  @Autowired
  private TileCodeStringMap tileCodeStringMap;

  private static Random random = new Random();

  public static Logger LOG = LoggerFactory.getLogger(ClassicTilesGenerator.class);

  //For these chars q means Qu and should be properly handled.

  //4x4 boggle classic 
  private static List<String> originalBoggleChars = 
    Arrays.asList(
    "AACIOT",
    "ABILTY",
    "ABJMO1", 
    "ACDEMP",
    "ACELRS",
    "ADENVZ",
    "AHMORS",
    "BIFORX", 
    "DENOSW", 
    "DKNOTU", 
    "EEFHIY", 
    "EGKLUY", 
    "EGINTV", 
    "EHINPS", 
    "ELPSTU", 
    "GILRUW"
    );


  //5x5 big boggle original
  private static List<String> bigBoggleChars = 
    Arrays.asList(
    "AAAFRS",
    "AAEEEE",
    "AAFIRS",
    "ADENNN",
    "AEEEEM",
    "AEEGMU",
    "AEGMNN",
    "AFIRSY",
    "BJK1XZ",
    "CCENST",
    "CEIILT",
    "CEIPST",
    "DDHNOT",
    "DHHLOR",
    "DHHLOR",
    "DHLNOR",
    "EIIITT",
    "CEILPT",
    "EMOTTT",
    "ENSSSU",
    "FIPRSY",
    "GORRVW",
    "IPRRRY",
    "NOOTUW",
    "OOOTTU"
  );

  private static List<String> superBigBoggleChars = 
    /* #0 = Blank, 1 = Qu, 2 = In, 3 = Th, 4 = Er, 5 = He, 6 = An */
    Arrays.asList(
        "AAAFRS",
        "AAEEEE",
        "AAEEOO",
        "AAFIRS",
        "ABDEIO",
        "ADENNN",
        "AEEEEM",
        "AEEGMU",
        "AEGMNN",
        "AEILMN",
        "AEINOU",
        "AFIRSY",
        "123456", /*double letter combo tile */
        "BBJKXZ",
        "CCENST",
        "CDDLNN",
        "CEIITT",
        "CEIPST",
        "CFGNUY",
        "DDHNOT",
        "DHHLOR",
        "DHHNOW",
        "DHLNOR",
        "EHILRS",
        "EIILST",
        "EILPST",
        "EIO000", /* triple blank tile */
        "EMTTTO",
        "ENSSSU",
        "GORRVW",
        "HIRSTV",
        "HOPRST",
        "IPRSYY",
        "JK1WXZ",
        "NOOTUW",
        "OOOTTU" 
    );

/*
boggleDice_Classic = ['AACIOT', 'ABILTY', 'ABJMO1', 'ACDEMP', 'ACELRS', 'ADENVZ', 'AHMORS', 'BIFORX', 'DENOSW', 'DKNOTU', 'EEFHIY', 'EGKLUY', 'EGINTV', 'EHINPS', 'ELPSTU', 'GILRUW']
boggleDice_New = ['AAEEGN', 'ABBJOO', 'ACHOPS', 'AFFKPS', 'AOOTTW', 'CIMOTU', 'DEILRX', 'DELRVY', 'DISTTY', 'EEGHNW', 'EEINSU', 'EHRTVW', 'EIOSST', 'ELRTTY', 'HIMNU1', 'HLNNRZ']
boggleDice_Big_Original = ['AAAFRS', 'AAEEEE', 'AAFIRS', 'ADENNN', 'AEEEEM', 'AEEGMU', 'AEGMNN', 'AFIRSY', 'BJK1XZ', 'CCENST', 'CEIILT', 'CEIPST', 'DDHNOT', 'DHHLOR', 'DHHLOR', 'DHLNOR', 'EIIITT', 'CEILPT', 'EMOTTT', 'ENSSSU', 'FIPRSY', 'GORRVW', 'IPRRRY', 'NOOTUW', 'OOOTTU']
boggleDice_Big_Challenge = ['AAAFRS', 'AAEEEE', 'AAFIRS', 'ADENNN', 'AEEEEM', 'AEEGMU', 'AEGMNN', 'AFIRSY', 'BJK1XZ', 'CCENST', 'CEIILT', 'CEIPST', 'DDHNOT', 'DHHLOR', 'IKLM1U', 'DHLNOR', 'EIIITT', 'CEILPT', 'EMOTTT', 'ENSSSU', 'FIPRSY', 'GORRVW', 'IPRRRY', 'NOOTUW', 'OOOTTU']
boggleDice_Big_Deluxe = ['AAAFRS', 'AAEEEE', 'AAFIRS', 'ADENNN', 'AEEEEM', 'AEEGMU', 'AEGMNN', 'AFIRSY', 'BJK1XZ', 'CCNSTW', 'CEIILT', 'CEIPST', 'DDLNOR', 'DHHLOR', 'DHHNOT', 'DHLNOR', 'EIIITT', 'CEILPT', 'EMOTTT', 'ENSSSU', 'FIPRSY', 'GORRVW', 'HIPRRY', 'NOOTUW', 'OOOTTU']
boggleDice_Big_2012 = ['AAAFRS', 'AAEEEE', 'AAFIRS', 'ADENNN', 'AEEEEM', 'AEEGMU', 'AEGMNN', 'AFIRSY', 'BBJKXZ', 'CCENST', 'EIILST', 'CEIPST', 'DDHNOT', 'DHHLOR', 'DHHNOW', 'DHLNOR', 'EIIITT', 'EILPST', 'EMOTTT', 'ENSSSU', '123456', 'GORRVW', 'IPRSYY', 'NOOTUW', 'OOOTTU']
boggleDice_Super_Big = ['AAAFRS', 'AAEEEE', 'AAEEOO', 'AAFIRS', 'ABDEIO', 'ADENNN', 'AEEEEM', 'AEEGMU', 'AEGMNN', 'AEILMN', 'AEINOU', 'AFIRSY', '123456', 'BBJKXZ', 'CCENST', 'CDDLNN', 'CEIITT', 'CEIPST', 'CFGNUY', 'DDHNOT', 'DHHLOR', 'DHHNOW', 'DHLNOR', 'EHILRS', 'EIILST', 'EILPST', 'EIO000', 'EMTTTO', 'ENSSSU', 'GORRVW', 'HIRSTV', 'HOPRST', 'IPRSYY', 'JK1WXZ', 'NOOTUW', 'OOOTTU']
#0 = Blank, 1 = Qu, 2 = In, 3 = Th, 4 = Er, 5 = He, 6 = An
//https://boardgamegeek.com/thread/300883/letter-distribution
*/

  public ClassicTilesGenerator(@Autowired TileCodeStringMap tileCodeStringMap) {
    this.tileCodeStringMap = tileCodeStringMap;
  }

  public List<Tile> generate(BoardSize boardSize) throws BoardGenerationException {
    return generate(boardSize,false);
  }

  public List<Tile> generate(BoardSize boardSize,boolean tileRotation) throws BoardGenerationException {

    if ( boardSize.equals(BoardSize.FOUR)) {
      return generate(originalBoggleChars,tileRotation);
    }

    if ( boardSize.equals(BoardSize.FIVE)) {
      return generate(bigBoggleChars,tileRotation);
    }

    if ( boardSize.equals(BoardSize.SIX)) {
      return generate(superBigBoggleChars,tileRotation);
    }

    throw new BoardGenerationException("invalid board size for classic generator" + boardSize);
  }

  public List<Tile> generateFromTileString(String tileString) throws BoardGenerationException {
    if ( tileString.length() == 16 || tileString.length() == 25 ) {
      tileString = tileString.toUpperCase();
      return generate(tileString);
    }
    throw new BoardGenerationException("generateFromTileString only works for 16 or 25 characters not " + tileString.length());
  }

  private List<Tile> generate(List<String> chars,boolean tileRotation) {
    List<Tile> tiles = new ArrayList<Tile>();
    chars.forEach( die -> {
      char c = die.charAt(random.nextInt(6));
      /*
      Integer code = c == 'Q' ?  : (int) c;
      */
      Integer code; 
      switch ( c ) {

        case 'Q': 
          code = tileCodeStringMap.getCode("Qu");
          break;
        case '0': 
          code = tileCodeStringMap.getCode(""); //blank
          break;
        case '1': 
          code = tileCodeStringMap.getCode("Qu");
          break;
        case '2': 
          code = tileCodeStringMap.getCode("In");
          break;
        case '3': 
          code = tileCodeStringMap.getCode("Th");
          break;
        case '4': 
          code = tileCodeStringMap.getCode("Er");
          break;
        case '5': 
          code = tileCodeStringMap.getCode("He");
          break;
        case '6': 
          code = tileCodeStringMap.getCode("An");
          break;

        default:
          code = (int) c;
      }

      if ( tileRotation ) {
        int cardinal = 0;
        switch ( random.nextInt(4) ) {
          case 0:
            cardinal = Tile.RotateDefault;
            break;
          case 1:
            cardinal = Tile.RotateDown;
            break;
          case 2:
            cardinal = Tile.RotateLeft;
            break;
          case 3:
            cardinal = Tile.RotateRight;
            break;
        }
        tiles.add(new Tile( code , cardinal ));
      } else {
        tiles.add(new Tile( code ));
      }


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
