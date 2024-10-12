import React, { useState, useRef, useEffect } from 'react';
import { toast } from 'react-toastify';

import GameTimer from "/src/main/js/components/game/game/GameTimer.jsx";
import Board from "/src/main/js/components/game/Board.jsx";
import UserAnswerDisplay from "/src/main/js/components/game/game/UserAnswerDisplay.jsx"
import WordInput from "/src/main/js/components/game/game/WordInput.jsx"

import { GetGameUserInfo, PostGameAnswer } from "/src/main/js/services/GameService.ts"


export async function loader({ params }) {
  const lobbyId = params.lobbyId
  return { lobbyId };
}

export default function Game({ lobby, onGameEnd }) {

  async function onSubmitAnswer(word) {

    var serviceResponse = await PostGameAnswer(lobby.gameId,{ answerText : word })
    var gameAnswerResult = serviceResponse.data

    if ( gameAnswerResult.success) {
      toast.success(gameAnswerResult.successMessage)
    } else {
      toast.error(gameAnswerResult.failMessage);
    }

  }

  const [game, setGame] = useState(null)

  useEffect(() => {

    // Data Fetch

    const fetchData = async () => {
      var serviceResponse = await GetGameUserInfo(lobby.gameId)
      setGame(serviceResponse.data)
    }
    fetchData()

    // SSE

    const evtSource = new EventSource("/api/game/" + lobby.gameId + "/view/user/sse")

    evtSource.addEventListener("game_change", (e) => {
      console.log("game change recieved");
      let newGameData = JSON.parse(e.data)
      setGame(newGameData)
      console.log(newGameData)
    });

    return () => {
      console.log("closing the game event source")
      evtSource.close()
    };

  }, []);

  if (lobby == null || game == null) {
    return <></>
  }

  return (

    <div className="lobby-grid game-grid-template">

      <div className="game-grid-timer">
        <GameTimer gameEnd={lobby.gameEnd} onGameEnd={onGameEnd} />
      </div>

      <div className="game-grid-board">
        <Board dice={game.gameViewDTO.tiles} />
      </div>

      <div className="game-grid-word-input">
        <WordInput onWordInput={onSubmitAnswer} />
      </div>

      <div className="game-grid-answer-display">
        <UserAnswerDisplay words={game.answers} />
      </div>

    </div>

  );

}



