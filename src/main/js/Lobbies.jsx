import React from 'react';
import { useLoaderData, Link } from "react-router-dom";

export async function loader({params}) {
  const lobbiesResponse = await fetch("/api/lobby");
  const lobbies = await lobbiesResponse.json()
  console.log(`loading lobby data ${lobbies.toString()}`)
  return { lobbies };
}

export default function Lobbies() {

  const { lobbies } = useLoaderData();

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
                <td><Link id="join-lobby-link" to={"/lobby/"+lobby.id}>Join Lobby</Link></td>
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
