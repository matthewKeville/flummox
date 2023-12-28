import React, { useState, useRef, useEffect } from 'react';
import GameTimer from "./GameTimer.jsx";

export async function loader({params}) {
  const lobbyId = params.lobbyId
  return  { lobbyId };
}

export default function Game({lobby,onGameEnd}) {
  return ( 
    <>
      <div style={{color: "red"}}> Under Construction </div>
      <GameTimer gameEnd={lobby.gameEnd} onGameEnd={onGameEnd}/>
    </>
  )
}
