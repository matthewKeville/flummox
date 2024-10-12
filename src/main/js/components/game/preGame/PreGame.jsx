import React from 'react';
import {  useRouteLoaderData, useNavigate, useRevalidator } from "react-router-dom";
import { toast } from 'react-toastify';

import PlayerList from "/src/main/js/components/game/preGame/PlayerList.jsx";
import GameSettings from "/src/main/js/components/game/preGame/GameSettings.jsx";
import LobbyChat from "/src/main/js/components/game/preGame/LobbyChat.jsx";

import { GetInviteLink, StartLobby, LeaveLobby, DeleteLobby } from "/src/main/js/services/LobbyService.ts";
import { CopyToClipboardInsecure } from "/src/main/js/services/ClipboardService.ts";

export default function PreGame({lobby,playedPrev,onReturnToPostGame}) {

  const navigate = useNavigate();
  const revalidator = useRevalidator();
  const { userInfo } = useRouteLoaderData("root");

  const isOwner = (lobby.owner.id == userInfo.id);

  let leaveLobby = async function(lobbyId) {

    let serviceResponse = await LeaveLobby(lobbyId);

    if ( serviceResponse.success ) {
      revalidator.revalidate()
      navigate("/lobby");
      return
    } 

    toast.error(serviceResponse.errorMessage);

  }

  let copyInviteLink = async function() {

    let serviceResponse = await GetInviteLink()

    if ( !serviceResponse.success ) {

      toast.error(serviceResponse.errorMessage);
      return;

    } else {

      var lobbyInviteLink = serviceResponse.data
      CopyToClipboardInsecure(lobbyInviteLink)
      toast.info("Invite Copied To Clipboard")

    }
  }

  let deleteLobby = async function(lobbyId) {

    let serviceResponse = await DeleteLobby(lobbyId)

    if ( !serviceResponse.success ) {

      toast.error(serviceResponse.errorMessage);
      return;

    } else {

      revalidator.revalidate()
      navigate("/lobby")

    }
  }

  let startLobby = async function(lobbyId) {

    let serviceResponse = await StartLobby(lobbyId)

    if ( !serviceResponse.success ) {
      toast.error(serviceResponse.errorMessage);
      return;
    } 
  }

  if ( !lobby ) {
    return (<></>)
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
          { isOwner &&
            <button className="basic-button" onClick={() => startLobby(lobby.id)}>Start</button> 
          }
          {
            playedPrev &&
              <button className="tertiary-button" onClick={onReturnToPostGame}>Last</button>
          }
          { <button className="basic-button" onClick={() => copyInviteLink()}>Invite</button> }
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
