import React from 'react';
import { useNavigate, useLoaderData, useRevalidator } from 'react-router-dom';
import { CreateLobby } from '/src/main/js/services/flummox/LobbyService.ts'
import { Button } from "@mantine/core";

import { toast } from 'react-toastify';

export default function PlayNow() {

  const revalidator = useRevalidator();
  const navigate = useNavigate();
  const { userInfo } = useLoaderData();

  async function createOrReturnToLobby() { 

    console.log("lobbyId " + userInfo.lobbyId)

    if ( userInfo.lobbyId != null ) {
      console.log("navigating to current lobby")
      navigate("/lobby/" + userInfo.lobbyId);
      return;
    }

    let serviceResponse = await CreateLobby()
    if ( !serviceResponse.success ) {
      console.log("error creating lobby")
      return
    }
    
    revalidator.revalidate()
    navigate("/lobby/" + serviceResponse.data)

  }

  return (
    <>
    <Button onClick={createOrReturnToLobby}>{ userInfo.lobbyId == null ? "Play Now" : "Lobby"}</Button>
    </>
  )


}
