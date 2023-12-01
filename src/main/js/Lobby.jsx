import React from 'react';
import { useLoaderData } from "react-router-dom";

export async function loader({params}) {
  const lobbyResponse = await fetch("/api/lobby/"+params.lobbyId);
  const lobby= await lobbyResponse.json();
  return { lobby };
}

export default function Lobby() {

  const { lobby } = useLoaderData();

  if ( !lobby ) {
    return (<><div>no lobby data</div></>)
  }

  return (
    <>

      <h3 id="lobby-welcome-header">
      Welcome to the lobby {lobby.name}
      </h3>

      <div id="lobby-flex">

        <div id="users-flex-container">
          <div id="users-flex">
            <table>
              <tbody>
              {
                lobby.users.map( (player) => {
                  return (
                    <tr key={player.id}>
                      <td>{player.username}{player.id == lobby.owner.id ? "*" : "" }</td>
                    </tr>
                  )
                })
              }
              </tbody>
            </table>
          </div>
        </div>

        <div id="chat-flex-container">
          <div id="chat-flex">
          </div>
        </div>

        <div id="settings-flex-container">
          <div id="settings-flex">
            <table>
              <tbody>
              <tr>
                <td>Name</td><td>{lobby.name}</td>
              </tr>
              <tr>
                <td>Capacity</td><td>{lobby.capacity}</td>
              </tr>
              <tr>
                <td>Visibility</td><td>{lobby.isPrivate ? "public" : "private"}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </>
  );

}
