import React, { useState, useRef, useEffect } from 'react';
import { useRouteLoaderData } from 'react-router-dom';
import { toast } from 'react-toastify';

import Board from "./Board.jsx";
import AllAnswerDisplay from './AllAnswerDisplay.jsx';

export default function PostGame({lobby,onGameEnd,onReturnToLobby}) {

  const { userInfo } = useRouteLoaderData("root");
  const [gameSummary,setGameSummary] = useState(null)

  async function fetchGameSummary() {
    console.log("loading user game summary for game " + lobby.gameId + " for user " + userInfo.id )
    const userGameSummaryResponse = await fetch("/api/game/"+lobby.gameId+"/view/user/summary");
    let gameSummaryJson = await userGameSummaryResponse.json();
    setGameSummary(gameSummaryJson);
    console.log(gameSummaryJson)
  }

  useEffect(() => {

    const fetchInitialGame = async () => {
      fetchGameSummary()
    }
    fetchInitialGame()

  }, []);

  if (gameSummary == null) {
    return
  }

  return (

    <div className="lobby-grid post-game-grid-template">

      <div className="game-grid-board">
        <Board dice={gameSummary.gameViewDTO.tiles.map( tile => String.fromCharCode(tile.code) )} />
      </div>

      <div className="post-game-grid-exit">
  -     <button className="basic-button" onClick={onReturnToLobby}>Lobby</button>
      </div>

      <div className="game-grid-answer-display">
        <AllAnswerDisplay words={gameSummary.answers}/>
      </div>

    </div>


  );

}
