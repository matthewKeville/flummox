import React, { useState, useRef, useEffect } from 'react';
import { useLoaderData, useRouteLoaderData, useNavigate, useOutletContext, useOutlet } from "react-router-dom";
import { toast } from 'react-toastify';

import PlayerList from "./PlayerList.jsx";
import GameSettings from "./GameSettings.jsx";
import LobbyChat from "../lobby/LobbyChat.jsx";

export default function PreGame({lobby}) {

  const navigate = useNavigate();
  const { userInfo } = useRouteLoaderData("root");

  // Don't render if API error
  if (lobby == null) {
    return
  }

  const isOwner = (lobby.owner.id == userInfo.id);

  let leaveLobby = async function(lobbyId) {

    console.log("leaving lobby")

    const response = await fetch("/api/lobby/"+lobbyId+"/leave", {
      method: "POST",
      headers: {
      },
      body: null
    });

    if ( response.status == 200 ) {

      navigate("/lobby");

    } else {
    
      const content  = await response.json();
      console.log(`unable to leave lobby because : ${content.message}`)
      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

    }
  }

  let deleteLobby = async function(lobbyId) {

      console.log("deleting lobby")

      const response = await fetch("/api/lobby/"+lobbyId, {
        method: "DELETE",
        body: null
      });

      if ( response.status == 200 ) {

        navigate("/lobby");
        toast.info(`${lobby.name} has been successfully deleted`);

      } else {
      
        const content  = await response.json();
        console.log(`unable to delete lobby because : ${content.message}`)
        let notice = content.status + " : Unknown error"

        switch(content.message) {
          case "INTERNAL_ERROR":
          default:
            //pass
        }

        toast.error(notice);

      }
    }

  async function onStartGame() {

    const response = await fetch("/api/lobby/"+lobby.id+"/start", {
      method: "POST",
      headers: {
      },
      body: null
    });

    if ( response.status == 200 ) {

    } else {
    
      const content  = await response.json();

      console.log(`unable to start game because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

    }
  }

  if ( !lobby ) {
    return (<><div>no lobby data</div></>)
  }

  return (
    <>


    <div className="pregame-grid">

      <div className="pregame-grid-banner">
        <h3 className="lobby-welcome-header">Welcome to <span id="lobby-name-span">{lobby.name}</span></h3>
      </div>

      <div className="pregame-grid-player-list thick-blue-border">
        <PlayerList lobby={lobby}/>
      </div>

      <div className="pregame-grid-lobby-chat thick-blue-border">
        <LobbyChat lobby={lobby}/>
      </div>

      <div className="pregame-grid-game-settings thick-blue-border">
        <GameSettings lobby={lobby}/>
      </div>

      <div className="pregame-grid-lobby-buttons">
        <div className="lobby-buttons thick-blue-border">
          <button id="start-game-button" onClick={onStartGame}>Start</button> 
          <button className="lobby-exit-button" onClick={isOwner ? () => deleteLobby(lobby.id) :  () => leaveLobby(lobby.id) } >{isOwner ? "Delete" : "Leave"}</button>
        </div>
      </div>

    </div>

    </>
  );

}
