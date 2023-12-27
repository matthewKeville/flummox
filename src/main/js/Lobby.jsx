import React, { useState, useEffect, } from 'react';
import { Outlet , useLoaderData } from "react-router-dom";

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

    evtSource.addEventListener("lobby_start", (e) => {
      console.log("lobby start recieved");
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

  return (
  <>
      <Outlet context={[lobby]}/>
  </>
  )

}
