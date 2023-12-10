import React from 'react';
import { useLoaderData, Link, useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

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

    const response = await fetch("/api/lobby/"+lobbyId+"/join", {
      method: "POST",
      headers: {
      },
      body: null
    });


    if (response.status == 200) {

      navigate("/lobby/" + lobbyId);

    } else {

      const content = await response.json()

      console.log(`unable to join lobby because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "LOBBY_IS_FULL":
          notice = " Unable to join lobby because it is full"
          break;
        case "LOBBY_IS_PRIVATE":
          notice = " Unable to join lobby because it is private"
          break;
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

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
