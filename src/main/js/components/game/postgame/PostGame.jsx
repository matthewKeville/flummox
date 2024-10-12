import React, { useState, useRef, useEffect } from 'react';
import { useRouteLoaderData } from 'react-router-dom';

import Board from "/src/main/js/components/game/Board.jsx";
import AllAnswerDisplay from "/src/main/js/components/game/postgame/AllAnswerDisplay.jsx";
import Scoreboard from "/src/main/js/components/game/postgame/Scoreboard.jsx";

import { GetPostGameUserSummary } from "/src/main/js/services/GameService.ts";

export default function PostGame({lobby,onGameEnd,onReturnToLobby}) {

  const { userInfo } = useRouteLoaderData("root");
  const [gameSummary,setGameSummary] = useState(null)

  async function fetchGameSummary() {

    let serviceResponse = await GetPostGameUserSummary(lobby.gameId);

    if ( !serviceResponse.success ) {
      toast.error(serviceResponse.errorMessage);
      return;
    }

    setGameSummary(serviceResponse.data);
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

      <div className="post-game-grid-scoreboard">
        <Scoreboard lobby={lobby} scoreboard={gameSummary.scoreboard}/>
      </div>

      <div className="game-grid-board">
        <Board dice={gameSummary.gameViewDTO.tiles} />
      </div>

      <div className="post-game-grid-exit">
  -     <button className="basic-button" onClick={onReturnToLobby}>Lobby</button>
      </div>

      <div className="game-grid-answer-display">
        <AllAnswerDisplay words={gameSummary.words}/>
      </div>

    </div>


  );

}
