import React, { useState, useRef, useEffect } from 'react';

function handleDieClick(dieIndex) {
  console.log("clicked die " + dieIndex);
}

function Die({value, handleClick}) {
  return (
    <button 
     className="die"
    onClick={handleClick}
    >
      {value}
    </button>
  );
}

/* in dire need of refactor */
export default function Board({dice}) {
  if ( dice.length == 16 ) {
    return (
      <div className="board-container">
        <div className="board">
          <div className="board-row">
            <Die value={dice[0]} handleClick={() => handleDieClick(0)} />
            <Die value={dice[1]} handleClick={() => handleDieClick(1)} />
            <Die value={dice[2]} handleClick={() => handleDieClick(2)} />
            <Die value={dice[3]} handleClick={() => handleDieClick(3)} />
          </div>
          <div className="board-row">
            <Die value={dice[4]} handleClick={() => handleDieClick(4)} />
            <Die value={dice[5]} handleClick={() => handleDieClick(5)} />
            <Die value={dice[6]} handleClick={() => handleDieClick(6)} />
            <Die value={dice[7]} handleClick={() => handleDieClick(7)} />
          </div>
          <div className="board-row">
            <Die value={dice[8]} handleClick={() => handleDieClick(8)} />
            <Die value={dice[9]} handleClick={() => handleDieClick(9)} />
            <Die value={dice[10]} handleClick={() => handleDieClick(10)} />
            <Die value={dice[11]} handleClick={() => handleDieClick(11)} />
          </div>
          <div className="board-row">
            <Die value={dice[12]} handleClick={() => handleDieClick(12)} />
            <Die value={dice[13]} handleClick={() => handleDieClick(13)} />
            <Die value={dice[14]} handleClick={() => handleDieClick(14)} />
            <Die value={dice[15]} handleClick={() => handleDieClick(15)} />
          </div>
        </div>
      </div>
    );
  } else if ( dice.length == 25 ) {
    return (
      <div className="board-container">
        <div className="board">
          <div className="board-row">
            <Die value={dice[0]} handleClick={() => handleDieClick(0)} />
            <Die value={dice[1]} handleClick={() => handleDieClick(1)} />
            <Die value={dice[2]} handleClick={() => handleDieClick(2)} />
            <Die value={dice[3]} handleClick={() => handleDieClick(3)} />
            <Die value={dice[4]} handleClick={() => handleDieClick(4)} />
          </div>
          <div className="board-row">
            <Die value={dice[5]} handleClick={() => handleDieClick(5)} />
            <Die value={dice[6]} handleClick={() => handleDieClick(6)} />
            <Die value={dice[7]} handleClick={() => handleDieClick(7)} />
            <Die value={dice[8]} handleClick={() => handleDieClick(8)} />
            <Die value={dice[9]} handleClick={() => handleDieClick(9)} />
          </div>
          <div className="board-row">
            <Die value={dice[10]} handleClick={() => handleDieClick(10)} />
            <Die value={dice[11]} handleClick={() => handleDieClick(11)} />
            <Die value={dice[12]} handleClick={() => handleDieClick(12)} />
            <Die value={dice[13]} handleClick={() => handleDieClick(13)} />
            <Die value={dice[14]} handleClick={() => handleDieClick(14)} />
          </div>
          <div className="board-row">
            <Die value={dice[15]} handleClick={() => handleDieClick(15)} />
            <Die value={dice[16]} handleClick={() => handleDieClick(16)} />
            <Die value={dice[17]} handleClick={() => handleDieClick(17)} />
            <Die value={dice[18]} handleClick={() => handleDieClick(18)} />
            <Die value={dice[19]} handleClick={() => handleDieClick(19)} />
          </div>
          <div className="board-row">
            <Die value={dice[20]} handleClick={() => handleDieClick(20)} />
            <Die value={dice[21]} handleClick={() => handleDieClick(21)} />
            <Die value={dice[22]} handleClick={() => handleDieClick(22)} />
            <Die value={dice[23]} handleClick={() => handleDieClick(23)} />
            <Die value={dice[24]} handleClick={() => handleDieClick(24)} />
          </div>
        </div>
      </div>
    );
  } else if ( dice.length == 36 ) {
    return (
      <div className="board-container">
        <div className="board">
          <div className="board-row">
            <Die value={dice[0]} handleClick={() => handleDieClick(0)} />
            <Die value={dice[1]} handleClick={() => handleDieClick(1)} />
            <Die value={dice[2]} handleClick={() => handleDieClick(2)} />
            <Die value={dice[3]} handleClick={() => handleDieClick(3)} />
            <Die value={dice[4]} handleClick={() => handleDieClick(4)} />
            <Die value={dice[5]} handleClick={() => handleDieClick(5)} />
          </div>
          <div className="board-row">
            <Die value={dice[6]} handleClick={() => handleDieClick(6)} />
            <Die value={dice[7]} handleClick={() => handleDieClick(7)} />
            <Die value={dice[8]} handleClick={() => handleDieClick(8)} />
            <Die value={dice[9]} handleClick={() => handleDieClick(9)} />
            <Die value={dice[10]} handleClick={() => handleDieClick(10)} />
            <Die value={dice[11]} handleClick={() => handleDieClick(11)} />
          </div>
          <div className="board-row">
            <Die value={dice[12]} handleClick={() => handleDieClick(12)} />
            <Die value={dice[13]} handleClick={() => handleDieClick(13)} />
            <Die value={dice[14]} handleClick={() => handleDieClick(14)} />
            <Die value={dice[15]} handleClick={() => handleDieClick(15)} />
            <Die value={dice[16]} handleClick={() => handleDieClick(16)} />
            <Die value={dice[17]} handleClick={() => handleDieClick(17)} />
          </div>
          <div className="board-row">
            <Die value={dice[18]} handleClick={() => handleDieClick(18)} />
            <Die value={dice[19]} handleClick={() => handleDieClick(19)} />
            <Die value={dice[20]} handleClick={() => handleDieClick(20)} />
            <Die value={dice[21]} handleClick={() => handleDieClick(21)} />
            <Die value={dice[22]} handleClick={() => handleDieClick(22)} />
            <Die value={dice[23]} handleClick={() => handleDieClick(23)} />
          </div>
          <div className="board-row">
            <Die value={dice[24]} handleClick={() => handleDieClick(24)} />
            <Die value={dice[25]} handleClick={() => handleDieClick(25)} />
            <Die value={dice[26]} handleClick={() => handleDieClick(26)} />
            <Die value={dice[27]} handleClick={() => handleDieClick(27)} />
            <Die value={dice[28]} handleClick={() => handleDieClick(28)} />
            <Die value={dice[29]} handleClick={() => handleDieClick(29)} />
          </div>
          <div className="board-row">
            <Die value={dice[30]} handleClick={() => handleDieClick(30)} />
            <Die value={dice[31]} handleClick={() => handleDieClick(31)} />
            <Die value={dice[32]} handleClick={() => handleDieClick(32)} />
            <Die value={dice[33]} handleClick={() => handleDieClick(33)} />
            <Die value={dice[34]} handleClick={() => handleDieClick(34)} />
            <Die value={dice[35]} handleClick={() => handleDieClick(35)} />
          </div>
        </div>
      </div>
    );
  }

}
