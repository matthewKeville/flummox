import React, { useState, useRef, useEffect } from 'react';
import { useRouteLoaderData } from 'react-router-dom';
import { Stack } from '@mantine/core';

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

    <Stack align="center" justify="center" mt="2%"> 
     {/*<Scoreboard lobby={lobby} scoreboard={gameSummary.scoreboard}/>*/}
      <Board dice={gameSummary.gameViewDTO.tiles} />
-     <button className="basic-button" onClick={onReturnToLobby}>Lobby</button>
      <AllAnswerDisplay words={gameSummary.words}/>
    </Stack>

  );

}
