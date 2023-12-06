import React from 'react';
import { useLoaderData, Link, useNavigate } from "react-router-dom";

export async function loader({params}) {
  const lobbiesResponse = await fetch("/api/lobby");
  const lobbies = await lobbiesResponse.json()
  console.log(`loading lobby data ${lobbies.toString()}`)
  return { lobbies };
}

export default function Lobbies() {

  const { lobbies } = useLoaderData();
  const navigate    = useNavigate();

  let joinLobby = async function(lobbyId) {

    console.log(`this is where I would try to join lobby  ${lobbyId}`);
    const response = await fetch("/api/lobby/"+lobbyId+"/join", {
      method: "POST",
      headers: {
      },
      body: null
    });

    const result = await response.json();

    if ( result.success ) {
      navigate("/lobby/" + lobbyId);
    } else {
      console.log(result.response);
    }


  }

  if ( !lobbies ) {
    return (<><div>no lobbies</div></>)
  }

  return (
    <>

    <div className="lobbyTableDiv">
      <table id="lobby-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Lobby Name</th>
            <th>Players</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
        {
          lobbies.map( (lobby) => {
            return (
              <tr key={lobby.id}>
                <td>{lobby.id}</td>
                <td>{lobby.name}</td>
                <td>{lobby.users.length} / {lobby.capacity}</td>
                <td>{lobby.isPrivate ? "locked" : "open"}</td>
                {/*
                <td><Link id="join-lobby-link" to={"/lobby/"+lobby.id}>Join Lobby</Link></td>
                */}
                <td><button id="join-lobby-link" onClick={() => joinLobby(lobby.id)} >Join Lobby</button></td>
              </tr>
            )
          })
        }
        </tbody>
      </table>
    </div>
    </>
  );

}
