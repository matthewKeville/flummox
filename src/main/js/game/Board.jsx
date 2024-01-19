import React, { useState, useRef, useEffect } from 'react';

function handleDieClick(dieIndex) {
  console.log("clicked die " + dieIndex);
}

function getTileString(tile) {
  switch(tile.code) {
    case 0:
      return ""
    case 1:
      return "Qu"
    case 2:
      return "In"
    case 3:
      return "Th"
    case 4:
      return "Er"
    case 5:
      return "He"
    case 6:
      return "An"
    default:
      return String.fromCharCode(tile.code)
  }
}

function Die({tile, handleClick}) {
  return (
    <button 
     className="die"
    onClick={handleClick}
    >
    {getTileString(tile)}
    </button>
  );
}

/* in dire need of refactor */
export default function Board({dice}) {
  if ( dice.length == 16 ) {
    return (
      <>
        <div className="board">
          <div className="board-row">
            <Die tile={dice[0]} handleClick={() => handleDieClick(0)} />
            <Die tile={dice[1]} handleClick={() => handleDieClick(1)} />
            <Die tile={dice[2]} handleClick={() => handleDieClick(2)} />
            <Die tile={dice[3]} handleClick={() => handleDieClick(3)} />
          </div>
          <div className="board-row">
            <Die tile={dice[4]} handleClick={() => handleDieClick(4)} />
            <Die tile={dice[5]} handleClick={() => handleDieClick(5)} />
            <Die tile={dice[6]} handleClick={() => handleDieClick(6)} />
            <Die tile={dice[7]} handleClick={() => handleDieClick(7)} />
          </div>
          <div className="board-row">
            <Die tile={dice[8]} handleClick={() => handleDieClick(8)} />
            <Die tile={dice[9]} handleClick={() => handleDieClick(9)} />
            <Die tile={dice[10]} handleClick={() => handleDieClick(10)} />
            <Die tile={dice[11]} handleClick={() => handleDieClick(11)} />
          </div>
          <div className="board-row">
            <Die tile={dice[12]} handleClick={() => handleDieClick(12)} />
            <Die tile={dice[13]} handleClick={() => handleDieClick(13)} />
            <Die tile={dice[14]} handleClick={() => handleDieClick(14)} />
            <Die tile={dice[15]} handleClick={() => handleDieClick(15)} />
          </div>
        </div>
      </>
    );
  } else if ( dice.length == 25 ) {
    return (
      <>
        <div className="board">
          <div className="board-row">
            <Die tile={dice[0]} handleClick={() => handleDieClick(0)} />
            <Die tile={dice[1]} handleClick={() => handleDieClick(1)} />
            <Die tile={dice[2]} handleClick={() => handleDieClick(2)} />
            <Die tile={dice[3]} handleClick={() => handleDieClick(3)} />
            <Die tile={dice[4]} handleClick={() => handleDieClick(4)} />
          </div>
          <div className="board-row">
            <Die tile={dice[5]} handleClick={() => handleDieClick(5)} />
            <Die tile={dice[6]} handleClick={() => handleDieClick(6)} />
            <Die tile={dice[7]} handleClick={() => handleDieClick(7)} />
            <Die tile={dice[8]} handleClick={() => handleDieClick(8)} />
            <Die tile={dice[9]} handleClick={() => handleDieClick(9)} />
          </div>
          <div className="board-row">
            <Die tile={dice[10]} handleClick={() => handleDieClick(10)} />
            <Die tile={dice[11]} handleClick={() => handleDieClick(11)} />
            <Die tile={dice[12]} handleClick={() => handleDieClick(12)} />
            <Die tile={dice[13]} handleClick={() => handleDieClick(13)} />
            <Die tile={dice[14]} handleClick={() => handleDieClick(14)} />
          </div>
          <div className="board-row">
            <Die tile={dice[15]} handleClick={() => handleDieClick(15)} />
            <Die tile={dice[16]} handleClick={() => handleDieClick(16)} />
            <Die tile={dice[17]} handleClick={() => handleDieClick(17)} />
            <Die tile={dice[18]} handleClick={() => handleDieClick(18)} />
            <Die tile={dice[19]} handleClick={() => handleDieClick(19)} />
          </div>
          <div className="board-row">
            <Die tile={dice[20]} handleClick={() => handleDieClick(20)} />
            <Die tile={dice[21]} handleClick={() => handleDieClick(21)} />
            <Die tile={dice[22]} handleClick={() => handleDieClick(22)} />
            <Die tile={dice[23]} handleClick={() => handleDieClick(23)} />
            <Die tile={dice[24]} handleClick={() => handleDieClick(24)} />
          </div>
        </div>
      </>
    );
  } else if ( dice.length == 36 ) {
    return (
      <>
        <div className="board">
          <div className="board-row">
            <Die tile={dice[0]} handleClick={() => handleDieClick(0)} />
            <Die tile={dice[1]} handleClick={() => handleDieClick(1)} />
            <Die tile={dice[2]} handleClick={() => handleDieClick(2)} />
            <Die tile={dice[3]} handleClick={() => handleDieClick(3)} />
            <Die tile={dice[4]} handleClick={() => handleDieClick(4)} />
            <Die tile={dice[5]} handleClick={() => handleDieClick(5)} />
          </div>
          <div className="board-row">
            <Die tile={dice[6]} handleClick={() => handleDieClick(6)} />
            <Die tile={dice[7]} handleClick={() => handleDieClick(7)} />
            <Die tile={dice[8]} handleClick={() => handleDieClick(8)} />
            <Die tile={dice[9]} handleClick={() => handleDieClick(9)} />
            <Die tile={dice[10]} handleClick={() => handleDieClick(10)} />
            <Die tile={dice[11]} handleClick={() => handleDieClick(11)} />
          </div>
          <div className="board-row">
            <Die tile={dice[12]} handleClick={() => handleDieClick(12)} />
            <Die tile={dice[13]} handleClick={() => handleDieClick(13)} />
            <Die tile={dice[14]} handleClick={() => handleDieClick(14)} />
            <Die tile={dice[15]} handleClick={() => handleDieClick(15)} />
            <Die tile={dice[16]} handleClick={() => handleDieClick(16)} />
            <Die tile={dice[17]} handleClick={() => handleDieClick(17)} />
          </div>
          <div className="board-row">
            <Die tile={dice[18]} handleClick={() => handleDieClick(18)} />
            <Die tile={dice[19]} handleClick={() => handleDieClick(19)} />
            <Die tile={dice[20]} handleClick={() => handleDieClick(20)} />
            <Die tile={dice[21]} handleClick={() => handleDieClick(21)} />
            <Die tile={dice[22]} handleClick={() => handleDieClick(22)} />
            <Die tile={dice[23]} handleClick={() => handleDieClick(23)} />
          </div>
          <div className="board-row">
            <Die tile={dice[24]} handleClick={() => handleDieClick(24)} />
            <Die tile={dice[25]} handleClick={() => handleDieClick(25)} />
            <Die tile={dice[26]} handleClick={() => handleDieClick(26)} />
            <Die tile={dice[27]} handleClick={() => handleDieClick(27)} />
            <Die tile={dice[28]} handleClick={() => handleDieClick(28)} />
            <Die tile={dice[29]} handleClick={() => handleDieClick(29)} />
          </div>
          <div className="board-row">
            <Die tile={dice[30]} handleClick={() => handleDieClick(30)} />
            <Die tile={dice[31]} handleClick={() => handleDieClick(31)} />
            <Die tile={dice[32]} handleClick={() => handleDieClick(32)} />
            <Die tile={dice[33]} handleClick={() => handleDieClick(33)} />
            <Die tile={dice[34]} handleClick={() => handleDieClick(34)} />
            <Die tile={dice[35]} handleClick={() => handleDieClick(35)} />
          </div>
        </div>
      </>
    );
  }

}
