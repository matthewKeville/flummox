import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { Stack, Group } from "@mantine/core";

import GameTimer from "/src/main/js/components/game/game/GameTimer.jsx";
import Board from "/src/main/js/components/game/Board.jsx";
import UserAnswerDisplay from "/src/main/js/components/game/game/UserAnswerDisplay.jsx"
import WordInput from "/src/main/js/components/WordInput.jsx"

import {  PostGameAnswer } from "/src/main/js/services/GameService.ts"

export async function loader() {}

export default function Game({ gameId}) {

  async function onSubmitAnswer(word) {

    var serviceResponse = await PostGameAnswer(gameId,{ answerText : word })
    var gameAnswerResult = serviceResponse.data

    if ( gameAnswerResult.success) {
      toast.success(gameAnswerResult.successMessage)
    } else {
      toast.error(gameAnswerResult.failMessage);
    }

  }

  const [game, setGame] = useState(null)

  useEffect(() => {

    // SSE

    const evtSource = new EventSource("/api/game/" + gameId + "/sse")

    evtSource.addEventListener("update", (e) => {
      console.log("game change recieved");
      let newGameData = JSON.parse(e.data)
      setGame(newGameData)
      console.log(newGameData)
    });

    evtSource.addEventListener("init", (e) => {
      console.log("init game data recieved");
      let data = JSON.parse(e.data)
      setGame(data)
      console.log(data)
    });

    return () => {
      console.log("closing the game event source")
      evtSource.close()
    };

  }, []);

  if (game == null) {
    return <></>
  }

  return (
      <Stack align="center" justify="center" mt="2%"> 
        <GameTimer gameEnd={game.end}/>
        <Board dice={game.tiles} tileRotationEnabled={game.tileRotation} />
        <Group> 
          <WordInput onWordInput={onSubmitAnswer} />
        </Group>
        <UserAnswerDisplay words={game.answers} />
      </Stack>

  );

}



