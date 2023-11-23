import React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import Header from "./Header";

function joinLobby(lobby) {
  window.location.href = `/lobby/${lobby.id}`
  console.log(`joining lobby ${lobby.name}`)
}

export default function LobbyPage() {
  return (
    <>
      <Header/>
      <LobbyTable></LobbyTable>
    </>
  )
}

function LobbyTable() {

  const [lobbies,setLobbies] = useState();

  useEffect( () => {

    async function getLobbies() {
      const lobbiesResponse = await fetch("/api/lobby");
      const data = await lobbiesResponse.json()
      console.log(data)
      setLobbies(data);
    };
    
    if (!lobbies) {
      getLobbies()
    }

    if ( lobbies ) {
      console.log(lobbies)
      lobbies.forEach( (l) => { console.log(l)} );
    }


  } , [] );

  if ( !lobbies ) {
    return (<><div>no lobbies</div></>)
  }

  return (
    <>

    <div className="lobbyTableDiv">
      <table className="lobbyTable">
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
                <td>{lobby.private ? "locked" : "open"}</td>
                <td><button onClick={() => joinLobby(lobby)}>Join</button></td>
              </tr>
            )
          })
        }
        </tbody>
      </table>
    </div>
    </>
  );

  /*
  return (
    <>
    <div className="lobbyTableDiv">
      <table className="lobbyTable">
        <thead>
          <tr>
            <th>Lobby Name</th>
            <th>Player Count</th>
            <th>Game Mode</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>General 1</td>
            <td>0</td>
            <td>Classic 4x4</td>
          </tr>
          <tr>
            <td>General 2</td>
            <td>3</td>
            <td>Classic 5x5</td>
          </tr>
          <tr>
            <td>General 3</td>
            <td>3</td>
            <td>Classic 5x5</td>
          </tr>
        </tbody>
      </table>
    </div>
    </>
  );
  */

}
