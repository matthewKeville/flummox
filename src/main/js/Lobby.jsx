import React, { useState, useEffect, } from 'react';
import { Outlet , useLoaderData, useNavigate } from "react-router-dom";
import PreGame from "./PreGame.jsx";
import Game from "./Game.jsx";
import PostGame from "./PostGame.jsx";

export async function loader({params}) {
  console.log("loading lobby " + params.lobbyId)
  return params.lobbyId
}

async function loadLobbyData(lobbyId) {
  console.log("loading lobby " + lobbyId + " data")
  const lobbyResponse = await fetch("/api/lobby/"+lobbyId+"/view/lobby");
  return lobbyResponse.json();
}

export default function Lobby() {

  const lobbyId = useLoaderData();
  const [lobby,setLobby] = useState(null)
  const [lobbyState,setLobbyState] = useState(null)
  const navigate = useNavigate();

  useEffect(() => {

    const fetchInitialLobby = async () => {
      const newLobby = await loadLobbyData(lobbyId)
      setLobby(newLobby)

      let gameStart = Date.parse(newLobby.gameStart)
      let gameEnd = Date.parse(newLobby.gameEnd)

      if ( gameEnd > Date.now() && gameStart < Date.now() ) {
        setLobbyState("game")
      } else {
        setLobbyState("pregame")
      }

    }
    fetchInitialLobby()

    console.log("setting up lobby source")
    const evtSource = new EventSource("/api/lobby/"+lobbyId+"/view/lobby/sse")

    evtSource.addEventListener("lobby_change", (e) => {
      console.log("lobby change recieved");
      let newLobbyData = JSON.parse(e.data)
      setLobby(newLobbyData)
    });

    evtSource.addEventListener("lobby_start", (e) => {
      console.log("lobby start recieved");
      setLobbyState("game")
    });

    //cleanup (when unmount)
    return () => {
      console.log("closing the event source") 
      evtSource.close()
    }

  }, []);

  //  We need useEffect to load inital data ...
  if (lobby == null) {
    return
  }

  console.log("lobby state is " + lobbyState)

  if ( lobbyState == "pregame" ) {
    return (
      <>
          <PreGame lobby={lobby} />
      </>
    )
  } else if (lobbyState == "game") {
    return (
      <>
          <Game lobby={lobby} onGameEnd={() => { setLobbyState("postgame"); console.log("triggered game end")}}/>
      </>
    )
  } else if ( lobbyState == "postgame" ) {
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
