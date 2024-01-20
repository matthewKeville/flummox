import React, { useState, useEffect, } from 'react';
import { Outlet , useLoaderData, useRouteLoaderData, useNavigate } from "react-router-dom";

import PreGame from "../game/PreGame.jsx";
import Game from "../game/Game.jsx";
import PostGame from "../game/PostGame.jsx";

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

  async function fetchLobbyData() {
    console.log("loading lobby " + lobbyId + " data")
    const response = await fetch("/api/lobby/"+lobbyId+"/view/lobby");
    let lobbyData = await response.json()
    console.log(lobbyData)
    return lobbyData
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

    let loadInitialLobby = async () => {
      let data = await fetchLobbyData()
      console.log("response in loadInitialLobby " + JSON.stringify(data))
      computeLobbyState(data)
      setLobby(data)
    }
    loadInitialLobby()

    console.log("setting up lobby source")
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
    console.log("lobby : " + lobby + " lobbyState : " + lobbyState)
    return null
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

    return (
      <>
        <div style={{color: "red"}}> Oops </div>
      </>
    )

  }

}
