import React, { useState, useEffect } from 'react';
import { useRouteLoaderData } from 'react-router-dom';
import { Stack, Button } from '@mantine/core';
import { IconArrowBackUp } from "@tabler/icons-react";

import Board from "/src/main/js/components/game/Board.jsx";
import AllAnswerDisplay from "/src/main/js/components/game/postgame/AllAnswerDisplay.jsx";

import { GetPostGame } from "/src/main/js/services/flummox/GameService.ts";

export default function PostGame({lobby,onReturnToLobby}) {

  const { userInfo } = useRouteLoaderData("root");
  const [postGame,setPostGame] = useState(null)

  useEffect(() => {

    const fetchGameSummary = async function() {

      let serviceResponse = await GetPostGame(lobby.gameId,userInfo.id);

      if ( !serviceResponse.success ) {
        toast.error(serviceResponse.errorMessage);
        return;
      }
      
      setPostGame(serviceResponse.data);

    }
    fetchGameSummary()

  }, []);

  if (postGame == null) {
    return
  }

  return (

    <Stack align="center" justify="center" mt="2%"> 
      <Board dice={postGame.tiles} />
      <Button color="yellow" onClick={() => onReturnToLobby()}>
        <IconArrowBackUp/>
      </Button>
      <AllAnswerDisplay words={postGame.words}/>
    </Stack>

  );

}
