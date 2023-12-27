import React from 'react';
import { useLoaderData, useRouteLoaderData, Link, useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export async function loader({params}) {

  console.log(`loading lobbies list`)

  const lobbiesResponse = await fetch("/api/lobby/view/lobby");
  const lobbies = await lobbiesResponse.json()

  return { lobbies };

}

export default function Lobbies() {

  const { lobbies } = useLoaderData();
  const { userInfo } = useRouteLoaderData("root");
  const navigate    = useNavigate();

  let joinLobby = async function(lobbyId) {

    const response = await fetch("/api/lobby/"+lobbyId+"/join", {
      method: "POST",
      headers: {
      },
      body: null
    });

    if (response.status == 200) {

      navigate("/lobby/" + lobbyId+"/pregame");

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
        case "ALREADY_IN_LOBBY":
          navigate("/lobby/" + lobbyId);
          return;
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

    }

  }

  let createLobby = async function() {

    console.log("creating lobby")

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

    { userInfo.isGuest ? <></> : 
      <div className="lobbies-controls">
        <button id="create-lobby-button" onClick={createLobby}>New Lobby</button>
      </div>
    }

    </>
  );

}
