import React, { useState, useEffect, } from 'react';
import {  useLoaderData, useRouteLoaderData } from "react-router-dom";

import PreGame from "/src/main/js/components/game/preGame/PreGame.jsx";
import Game from "/src/main/js/components/game/game/Game.jsx";
import PostGame from "/src/main/js/components/game/postgame/PostGame.jsx";

export async function loader({params}) {
  console.log("loading lobby " + params.lobbyId)
  return params.lobbyId
}

export default function Lobby() {

  const { userInfo } = useRouteLoaderData("root");
  const lobbyId = useLoaderData();

  const [lobby,setLobby] = useState(null)
  const [lobbyState,setLobbyState] = useState("pregame")

  /*  because useState (setters) are async i need to evaluate the data from
    * the SSE and can't rely on the stored state of the lobby. This feels
    * like bad design, perhaps a better approach would be to send the affected
    * userid's in the game_start and game_end events. These transitions are
    * the only space where this is important, because the other components
    * will rerender when the state changes so we can just use null coalescence
    * on a function that checks the stored state.
    */
  function isUserInCurrentGame() {
    return lobby?.gameUsers?.some( (x) =>  x.id == userInfo.id ) ?? false
  }
  function isUserInCurrentGameDTO(DTO) {
    return DTO.gameUsers.some( (x) =>  x.id == userInfo.id )
  }

  useEffect(() => {

    const evtSource = new EventSource("/api/lobby/"+lobbyId+"/summary/sse")

    evtSource.addEventListener("init", (e) => {
      let data = JSON.parse(e.data)
      console.log("init event recieved");
      console.log(data)
      setLobby(data)

      if ( isUserInCurrentGameDTO(data) && data.gameActive ) {
        setLobbyState("game")
      }

    });

    evtSource.addEventListener("update", (e) => {
      let data = JSON.parse(e.data)
      console.log("update event recieved");
      console.log(data)
      setLobby(data)
    });


    evtSource.addEventListener("game_start", (e) => {
      let data = JSON.parse(e.data)
      console.log("game_start recieved");
      console.log(data)
      setLobbyState("game")
      setLobby(data)
    });

    evtSource.addEventListener("game_end", (e) => {
      let data = JSON.parse(e.data)
      console.log("game_end recieved");
      console.log(data)
      setLobby(data)

      if ( isUserInCurrentGameDTO(data) ) {
        setLobbyState("postgame")
      }

    });


    return () => {
      console.log("closing the event source") 
      evtSource.close()
    }

  },[]);

  if (lobby == null) {
    return (<></>)
  }

  if ( lobbyState == "pregame" ) {
    return ( <PreGame lobby={lobby} onReturnToPostGame={() => {setLobbyState("postgame")}} playedPrevious={isUserInCurrentGame()}/> )
  } else if (lobbyState == "game") {
    return ( <Game gameId={lobby.gameId}/> )
  } else if (lobbyState == "postgame") {
    return ( <PostGame lobby={lobby} onReturnToLobby={() => {setLobbyState("pregame")}}/> )
  } else {
    return (<>oops</>)
  }

}
