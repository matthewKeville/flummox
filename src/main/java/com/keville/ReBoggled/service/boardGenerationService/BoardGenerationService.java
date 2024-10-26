package com.keville.ReBoggled.service.boardGenerationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keville.ReBoggled.model.game.Board;
import com.keville.ReBoggled.model.game.BoardGenerationException;
import com.keville.ReBoggled.model.game.BoardSize;
import com.keville.ReBoggled.model.game.BoardTopology;
import com.keville.ReBoggled.model.game.ClassicTilesGenerator;
import com.keville.ReBoggled.model.game.Tile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BoardGenerationService {

  private static Logger LOG = LoggerFactory.getLogger(BoardGenerationService.class);

  private ClassicTilesGenerator classicTilesGenerator;

  public BoardGenerationService(@Autowired ClassicTilesGenerator classicTilesGenerator) {
    this.classicTilesGenerator = classicTilesGenerator;
  }

  public Board generate(BoardSize size,BoardTopology topology,boolean tileRotation) throws BoardGenerationException {

    //In the future we delegate (important for tileset and mutation)
    LOG.warn(" defaulting to classic tile generation ");
    List<Tile> tiles = classicTilesGenerator.generate(size,tileRotation);

    Board board = new Board(size,topology,tiles,tileRotation);
    return board;
    
  }

  public Board generateFromTileString(String tileString,BoardSize size,BoardTopology topology) throws BoardGenerationException {

    List<Tile> tiles = classicTilesGenerator.generateFromTileString(tileString);
    Board board = new Board(size,topology,tiles,false);

    return board;

  }

}
