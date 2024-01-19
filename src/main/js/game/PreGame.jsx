import React, { useState, useRef, useEffect } from 'react';
import { useLoaderData, useRouteLoaderData, useNavigate, useOutletContext, useOutlet } from "react-router-dom";
import { toast } from 'react-toastify';

import PlayerList from "./PlayerList.jsx";
import GameSettings from "./GameSettings.jsx";
import LobbyChat from "../lobby/LobbyChat.jsx";

export default function PreGame({lobby,playedPrev,onReturnToPostGame}) {

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


    <div className="lobby-grid pre-game-grid-template">

      <div className="pre-game-grid-banner">
        <h3 className="lobby-welcome-header">Welcome to <span id="lobby-name-span">{lobby.name}</span></h3>
      </div>

      <div className="pre-game-grid-player-list ">
        <PlayerList lobby={lobby}/>
      </div>

      <div className="pre-game-grid-lobby-chat-and-user-actions-grid">
        <div className="pre-game-grid-chat">
          <LobbyChat lobby={lobby}/>
        </div>
        <div className="pre-game-grid-user-actions">
          <button className="basic-button" onClick={onStartGame}>Start</button> 
          { 
            playedPrev &&
            <button className="tertiary-button" onClick={onReturnToPostGame}>Last</button>
          }
          { isOwner ? 
            <button className="danger-button" onClick={() => deleteLobby(lobby.id)} >Delete</button>
            :
            <button className="alternate-button" onClick={() => leaveLobby(lobby.id)} >Leave</button>
          }
        </div>
      </div>

      <div className="pre-game-grid-game-settings ">
        <GameSettings lobby={lobby}/>
      </div>

    </div>

    </>
  );

}
