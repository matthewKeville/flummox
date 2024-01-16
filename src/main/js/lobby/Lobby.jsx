import React, { useState, useEffect, } from 'react';
import { Outlet , useLoaderData, useRouteLoaderData, useNavigate } from "react-router-dom";

import PreGame from "../game/PreGame.jsx";
import Game from "../game/Game.jsx";
import PostGame from "../game/PostGame.jsx";

export async function loader({params}) {
  console.log("loading lobby " + params.lobbyId)
  return params.lobbyId
}

async function loadLobbyData(lobbyId) {
  console.log("loading lobby " + lobbyId + " data")
  const lobbyResponse = await fetch("/api/lobby/"+lobbyId+"/view/lobby");
  return lobbyResponse.json();
}

async function loadUserGameSummary(gameId,userId) {
  console.log("loading user game summary for " + gameId + " for user " + userId )
  const userGameSummaryResponse = await fetch("/api/game/"+gameId+"/view/user/summary");
  console.log(userGameSummaryResponse.json())
  return userGameSummaryResponse.json();
}

export default function Lobby() {

  const { userInfo } = useRouteLoaderData("root");
  const lobbyId = useLoaderData();
  const [lobby,setLobby] = useState(null)
  const [lobbyState,setLobbyState] = useState(null)

  function computeLobbyState() {

    if ( lobby == null ) {
      return null
    }

    let gameStart = Date.parse(lobby.gameStart)
    let gameEnd = Date.parse(lobby.gameEnd)

    if ( gameEnd > Date.now() && gameStart < Date.now() ) {
      if ( lobbyState != "game" ) {
        setLobbyState("game")
      }
    } else {
      if ( lobbyState != "pregame" ) {
        setLobbyState("pregame")
      }
    }

    //This can be confusing to see in the logs as it appears up
    //to 4 times when loading the lobby.
    console.log("lobby is state is :" + lobbyState)

  }

  useEffect(() => {

    const fetchInitialLobby = async () => {
      const newLobby = await loadLobbyData(lobbyId)
      setLobby(newLobby)
    }
    fetchInitialLobby()

    console.log("setting up lobby source")
    const evtSource = new EventSource("/api/lobby/"+lobbyId+"/view/lobby/sse")

    evtSource.addEventListener("lobby_change", (e) => {
      console.log("lobby change recieved");
      let newLobbyData = JSON.parse(e.data)
      setLobby(newLobbyData)
    });

    //cleanup (when unmount)
    return () => {
      console.log("closing the event source") 
      evtSource.close()
    }

  }, []);

  if (lobby == null) {
    return
  }
  computeLobbyState()

  if ( lobbyState == "pregame" ) {
    return (
      <>
          <PreGame lobby={lobby} />
      </>
    )
    //PostGame & PreGame are the same thing but, PostGame implies participation in the previous game..
    /*
    return (
      <>
          <PostGame lobby={lobby} onReturnToLobby={() => {setLobbyState("pregame")}}/>
      </>
    )
    */
  } else if (lobbyState == "game") {
    return (
      <>
          <Game lobby={lobby} onGameEnd={() => { setLobbyState("pregame"); console.log("triggered game end"); loadUserGameSummary(lobby.gameId,userInfo.id) }}/>
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
