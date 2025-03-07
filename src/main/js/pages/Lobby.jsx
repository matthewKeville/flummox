import React, { useState, useEffect, } from 'react';
import {  useLoaderData, useRouteLoaderData, useNavigate } from "react-router-dom";

import PreGame from "/src/main/js/components/game/preGame/PreGame.jsx";
import Game from "/src/main/js/components/game/game/Game.jsx";
import PostGame from "/src/main/js/components/game/postgame/PostGame.jsx";
import { GetLobby } from "/src/main/js/services/flummox/LobbyService.ts";

export async function loader({params}) {
  console.log("loading lobby " + params.lobbyId)
  return params.lobbyId
}

export default function Lobby() {

  const navigate = useNavigate()
  const { userInfo } = useRouteLoaderData("root");
  const lobbyId = useLoaderData();
  const [lobby,setLobby] = useState(null)
  const [showPostGame,setShowPostGame] = useState(false)

  function isUserInCurrentGame() {
    return (lobby?.users?.find( (u) => u.id == userInfo.id ))?.inGame ?? false;
  }

  const fetchLobby = () => {
    GetLobby(lobbyId).then(
      (result) => {
        setLobby(result.data)
      },
      () => { 
        console.log("failed to get lobby")
      }
    )
  }

  useEffect(() => {

    if ( userInfo.lobbyId != lobbyId ) {
      navigate("/lobby");
      return
    }

    const evtSource = new EventSource("/api/lobby/"+lobbyId+"/summary/sse")

    evtSource.addEventListener("update", () => {
      console.log("update event recieved")
      fetchLobby()
    });

    evtSource.addEventListener("game_start", () => {
      console.log("game_start recieved")
      fetchLobby()
      setShowPostGame(true) // we always want the game to transition to the
                            // postgame, but the user can override this when 
                            // the game is inActive
    });

    evtSource.addEventListener("game_end", () => {
      console.log("game_end recieved")
      fetchLobby()
    });

    fetchLobby()

    return () => {
      console.log("closing the event source") 
      evtSource.close()
    }

  },[]);



  if (lobby == null) {
    return (<></>)
  }

  if ( 
    (!lobby.gameActive && !showPostGame && isUserInCurrentGame)
    || (!isUserInCurrentGame()) ) {
    return ( <PreGame lobby={lobby} onReturnToPostGame={() => {setShowPostGame(true)}} playedPrevious={isUserInCurrentGame()}/> )
  } 

  if ( (lobby.gameActive && isUserInCurrentGame()) ) {
    return ( <Game gameId={lobby.gameId}/> )
  }

  if ( (!lobby.gameActive && showPostGame && isUserInCurrentGame())) {
    return ( <PostGame lobby={lobby} onReturnToLobby={() => {setShowPostGame(false)}}/> )
  }

  return (<>"oops"</>)

}
