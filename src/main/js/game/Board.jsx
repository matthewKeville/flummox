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

export default function Board({dice}) {
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
}
