import React, { useState, useEffect, } from 'react';
import {  useLoaderData, useRouteLoaderData } from "react-router-dom";

import PreGame from "/src/main/js/components/game/preGame/PreGame.jsx";
import Game from "/src/main/js/components/game/game/Game.jsx";
import PostGame from "/src/main/js/components/game/postgame/PostGame.jsx";

import { GetLobbyViewData } from "/src/main/js/services/LobbyService.ts";

export async function loader({params}) {
  console.log("loading lobby " + params.lobbyId)
  return params.lobbyId
}

export default function Lobby() {

  const { userInfo } = useRouteLoaderData("root");
  const lobbyId = useLoaderData();

  const [prevGameExists,setPrevGameExists] = useState(false)
  const [lobby,setLobby] = useState(null)
  const [lobbyState,setLobbyState] = useState(null)

  async function onGameEnd() {
    setPrevGameExists(true)
    setLobbyState("postgame");
  }

  function computeLobbyState(initialLobby) {

    let gameStart = Date.parse(initialLobby.gameStart)
    let gameEnd = Date.parse(initialLobby.gameEnd)

    if ( gameEnd > Date.now() && gameStart < Date.now() ) {
      if ( lobbyState != "game" ) {
        console.log("setting game state")
        setLobbyState("game")
      }
    } else {
      if ( lobbyState != "pregame" ) {
        console.log("setting pregame state")
        setLobbyState("pregame")
      }
    }

  }

  useEffect(() => {

    // Data Fetch

    // this really should be a dataloader ...
    let loadInitialLobby = async () => {
      
      let serviceResponse = await GetLobbyViewData(lobbyId)
      let lobbyData = serviceResponse.data

      console.log("lobby data")
      console.log(lobbyData)
      
      computeLobbyState(lobbyData)
      setLobby(lobbyData)

    }
    loadInitialLobby()

    // SSE

    const evtSource = new EventSource("/api/lobby/"+lobbyId+"/view/lobby/sse")

    evtSource.addEventListener("lobby_change", (e) => {
      console.log("lobby change recieved");
      let data = JSON.parse(e.data)
      computeLobbyState(data)
      setLobby(data)
      console.log(data)
    });

    return () => {
      console.log("closing the event source") 
      evtSource.close()
    }

  }, []);


  if (lobby == null || lobbyState == null) {
    return (<></>)
  }

  if ( lobbyState == "pregame" ) {

    return (
      <>
          <PreGame lobby={lobby} playedPrev={prevGameExists} onReturnToPostGame={() => {setLobbyState("postgame")} }/>
      </>
    )

  } else if (lobbyState == "game") {

    return (
      <>
          <Game lobby={lobby} onGameEnd={onGameEnd}/>
      </>
    )

  } else if (lobbyState == "postgame") {

    return (
      <>
          <PostGame lobby={lobby} onReturnToLobby={() => {setLobbyState("pregame")}}/>
      </>
    )

  } else {
    return (<></>)
  }

}
