import React from 'react';
import { useNavigate, useLoaderData } from 'react-router-dom';

import styles from './PlayNow.module.css'

export default function PlayNow() {

  const navigate = useNavigate();
  const { userInfo } = useLoaderData();

  async function createOrReturnToLobby() { 

    console.log("returning to lobby")

    if ( userInfo.lobbyId != -1 ) {
      navigate("/lobby/" + userInfo.lobbyId);
      return;
    }

    console.log("creating new lobby")

    const response = await fetch("/api/lobby/create", {
      method: "POST",
      headers: {
      },
      body: null
    });

    const content  = await response.json();

    if ( response.status == 201 ) {

      console.log(content)
      navigate("/lobby/" + content.id);

    } else {

      console.log(`unable to create lobby because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

    }


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
