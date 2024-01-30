import React, { useState, useRef, useEffect } from 'react';
import { useRouteLoaderData } from 'react-router-dom';
import { toast } from 'react-toastify';

// import Board from "./Board.jsx";
// import AllAnswerDisplay from './AllAnswerDisplay.jsx';
// import Scoreboard from "./Scoreboard.jsx";

import ScoreboardUserDisplay from "/src/main/js/game/postgame/ScoreboardUserDisplay.jsx"

import Board from "/src/main/js/game/Board.jsx";
import AllAnswerDisplay from "/src/main/js/game/postgame/AllAnswerDisplay.jsx";
import Scoreboard from "/src/main/js/game/postgame/Scoreboard.jsx";
import styles from '/src/main/resources/static/css/button.module.css';

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

      <div className="post-game-grid-scoreboard">
        <Scoreboard lobby={lobby} scoreboard={gameSummary.scoreboard}/>
      </div>

      <div className="game-grid-board">
        <Board dice={gameSummary.gameViewDTO.tiles} />
      </div>

      <div className="post-game-grid-exit">
  -     <button className={styles["basic-button"]} onClick={onReturnToLobby}>Lobby</button>
      </div>

      <div className="game-grid-answer-display">
        <AllAnswerDisplay words={gameSummary.words}/>
      </div>

    </div>


  );

}
