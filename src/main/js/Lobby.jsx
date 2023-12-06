import React, { useState, useRef } from 'react';
import { useLoaderData, useNavigate } from "react-router-dom";



export async function loader({params}) {
  const lobbyResponse = await fetch("/api/lobby/"+params.lobbyId);
  const lobby= await lobbyResponse.json();
  const isOwner = true;
  return { lobby , isOwner };
}

export default function Lobby() {

  const { lobby, isOwner } = useLoaderData();

  const [edit, setEdit]    = useState(false)
  const navigate            = useNavigate();

  let leaveLobby = async function(lobbyId) {

    console.log("where i would leave lobby")

    const response = await fetch("/api/lobby/"+lobbyId+"/leave", {
      method: "POST",
      headers: {
      },
      body: null
    });

    // const result = await response.json();
    navigate("/lobby");

  }


  function onChangeSettings() {
    setEdit(!edit)
  }

  function onStartGame() {
    console.log("this is where I would start the game")
  }

  if ( !lobby ) {
    return (<><div>no lobby data</div></>)
  }

  const settingsGrid = 
    <div id="settings-grid">
      <div className="settings-grid-label">Name</div><div className="settings-grid-value">{lobby.name}</div>
      <div className="settings-grid-label">Capacity</div><div className="settings-grid-value">{lobby.capacity}</div>
      <div className="settings-grid-label">Visibility</div><div className="settings-grid-value">{lobby.isPrivate ? "public" : "private"}</div>
      <div className="settings-grid-label">Size</div><div className="settings-grid-value">{lobby.gameSettings.boardSize}</div>
      <div className="settings-grid-label">Topology</div><div className="settings-grid-value">{lobby.gameSettings.boardTopology}</div>
      <div className="settings-grid-label">Find Rule</div><div className="settings-grid-value">{lobby.gameSettings.findRule}</div>
      <div className="settings-grid-label">Time Limit</div><div className="settings-grid-value">{lobby.gameSettings.duration}</div>
    </div>

  return (
    <>

      <h3 id="lobby-welcome-header">Welcome to <span id="lobby-name-span">{lobby.name}</span></h3>
      <div id="lobby-flex">

        <div id="users-flex-container">
          <div id="users-grid">
            {
              lobby.users.map( (player) => {
                return (
                  <>
                    <div className={player.id == lobby.owner.id ? "users-grid-owner" : "users-grid-user"}>
                      {player.username}{player.id == lobby.owner.id ? " * " : ""}
                    </div>
                    {
                    isOwner ? 
                      <>
                        <button className="promote-player-button">Promote</button>
                        <button className="kick-player-button">Kick</button>
                      </>
                    : 
                      <></>
                    }
                  </>
                )
              })
            }
          </div>
        </div>

        <div id="chat-flex-container">
          <div id="chat-flex">
          </div>
        </div>

        <div id="settings-flex-container">
          { edit ? 
            ( 
              <div id="edit-settings-grid">

                <div className="settings-grid-label">Name</div>
                  <input type="text" defaultValue={lobby.name}/>

                <div className="settings-grid-label">Capacity</div>
                  <input type="number" name="capacity" min="1" max="12" defaultValue={lobby.capacity}/>

                <div className="settings-grid-label">Private</div>
                  <input type="checkbox" defaultChecked={lobby.isPrivate}/>
              
                <div className="settings-grid-label">Size</div>
                  <select name="size" defaultValue={lobby.gameSettings.boardSize}>
                    <option value="FOUR">4 x 4</option>
                    <option value="FIVE">5 x 5</option>
                  </select>

                <div className="settings-grid-label">Topology</div>
                  <select name="topology" defaultValue={lobby.gameSettings.boardTopology}>
                    <option value="PLANE">Plane</option>
                    <option value="CYLINDER">Cylinder</option>
                    <option value="TORUS">Torus</option>
                  </select>
                 
                <div className="settings-grid-label">Find Rule</div>
                  <select name="find" defaultValue={lobby.gameSettings.findRule}>
                    <option value="ANY">Any</option>
                    <option value="UNIQUE">Unique</option>
                    <option value="FIRST">First</option>
                  </select>

                <div className="settings-grid-label">Time Limit</div>
                  <input type="number" name="time" min="60" max="300" step="30" defaultValue={lobby.gameSettings.duration}/>

                <button id="save-settings-button" onClick={onChangeSettings}>Save</button>
                <button id="discard-settings-button" onClick={onChangeSettings}>Discard</button>
              </div>

            ) 
            : 
            (
              <div id="settings-grid">
                <div className="settings-grid-label">Name</div><div className="settings-grid-value">{lobby.name}</div>
                <div className="settings-grid-label">Capacity</div><div className="settings-grid-value">{lobby.capacity}</div>
                <div className="settings-grid-label">Visibility</div><div className="settings-grid-value">{lobby.isPrivate ? "public" : "private"}</div>
                <div className="settings-grid-label">Size</div><div className="settings-grid-value">{lobby.gameSettings.boardSize}</div>
                <div className="settings-grid-label">Topology</div><div className="settings-grid-value">{lobby.gameSettings.boardTopology}</div>
                <div className="settings-grid-label">Find Rule</div><div className="settings-grid-value">{lobby.gameSettings.findRule}</div>
                <div className="settings-grid-label">Time Limit</div><div className="settings-grid-value">{lobby.gameSettings.duration}</div>
                { isOwner ?
                  (
                    <>
                    <button id="start-game-button" onClick={onStartGame}>Start</button> 
                    <button id="edit-settings-button" onClick={onChangeSettings}>Edit</button>
                    </>
                  ) 
                  :
                  (<></>)
                }
              </div>
            )
          }
        </div>
      </div>

      <button id="lobby-exit-button" onClick={() => leaveLobby(lobby.id)} >{isOwner ? "Abandon Lobby" : "Leave Lobby"}</button>

    </>
  );

}
