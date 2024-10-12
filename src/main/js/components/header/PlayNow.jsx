import React from 'react';
import { useNavigate, useLoaderData } from 'react-router-dom';
import { CreateLobby } from '/src/main/js/services/LobbyService.ts'

import { toast } from 'react-toastify';

export default function PlayNow() {

  const navigate = useNavigate();
  const { userInfo } = useLoaderData();

  async function createOrReturnToLobby() { 

    console.log("lobbyId " + userInfo.lobbyId)

    if ( userInfo.lobbyId != -1 ) {
      navigate("/lobby/" + userInfo.lobbyId);
      return;
    }

    let serviceResponse = await CreateLobby()
    if ( !serviceResponse.success ) {
      toast.error("unable to create lobby");
      return
    }
    
    navigate("/lobby/" + serviceResponse.data)

  }

  return (
    <>
    {/*
      I want this button to display differently if the user is already in a lobby, as this information is
      tied up in the loader I need a to force a refresh for all cases where this can occur, Leave/Delete/This Button.
      Needs more thought...
      <button onClick={createOrReturnToLobby}>{userInfo.lobbyId == -1 ? "Play Now" : "Return To Lobby"}</button>
    */}
    <button onClick={createOrReturnToLobby}>Play Now</button>
    </>
  )


}
