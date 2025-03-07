import React, { useState, useEffect } from 'react';
import { useRouteLoaderData } from 'react-router-dom';
import { Stack, Button } from '@mantine/core';
import { IconArrowBackUp } from "@tabler/icons-react";

import Board from "/src/main/js/components/game/Board.jsx";
import WordSummaryDisplay from "/src/main/js/components/game/postgame/WordSummaryDisplay.jsx";

import { GetPostGame } from "/src/main/js/services/flummox/GameService.ts";

export default function PostGame({lobby,onReturnToLobby}) {

  const { userInfo } = useRouteLoaderData("root");
  const [postGame,setPostGame] = useState(null)
  const [highlightPath,setHighlightPath] = useState(null)

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

  let onHighlightPath = (path) => {
    console.log("onHighlight path hit")
    console.log(path)
    setHighlightPath(path)
  }

  return (

    <Stack align="center" justify="center" mt="2%"> 
      <Board dice={postGame.tiles} highlightPath={highlightPath}/>
      <Button color="yellow" onClick={() => onReturnToLobby()}>
        <IconArrowBackUp/>
      </Button>
      <WordSummaryDisplay summaries={postGame.wordSummaries} onHighlightPath={onHighlightPath}/>
    </Stack>

  );

}
