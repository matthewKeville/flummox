import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { Stack, Group } from "@mantine/core";
import { useRouteLoaderData } from "react-router-dom";

import GameTimer from "/src/main/js/components/game/game/GameTimer.jsx";
import Board from "/src/main/js/components/game/Board.jsx";
import AnswerDisplay from "/src/main/js/components/game/game/AnswerDisplay.jsx"
import WordInput from "/src/main/js/components/WordInput.jsx"

import {  PostGameAnswer } from "/src/main/js/services/flummox/GameService.ts"

export async function loader() {}

export default function Game({ gameId}) {

  const { userInfo } = useRouteLoaderData("root");
  const [muted,setMuted] = useState(true)
  const goodWordSFXAudio = new Audio("/audio/word-good.wav")
  const badWordSFXAudio = new Audio("/audio/word-bad.wav")
  goodWordSFXAudio.volume = 0.3
  badWordSFXAudio.volume = 0.2

  function toggleMuted() {
    setMuted(!muted)
  }

  async function onSubmitAnswer(word) {

    var serviceResponse = await PostGameAnswer(gameId,userInfo.id,{answer : word})
    if ( !serviceResponse.success ) {
      return;
    }

    var gameAnswerResult = serviceResponse.data

    if ( gameAnswerResult.success) {
      toast.success(gameAnswerResult.successMessage)
      !muted && goodWordSFXAudio.play();
    } else {
      toast.error(gameAnswerResult.failMessage);
      !muted && badWordSFXAudio.play();
    }

  }

  const [game, setGame] = useState(null)

  useEffect(() => {

    // SSE

    const evtSource = new EventSource("/api/game/" + gameId + "/sse/" + userInfo.id)

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
        <Board dice={game.tiles} tileRotationEnabled={game.tileRotation} muted={muted} onToggleMuted={() => toggleMuted()}/>
        <Group> 
          <WordInput onWordInput={onSubmitAnswer} />
        </Group>
        <AnswerDisplay answers={game.answers} />
      </Stack>

  );

}



