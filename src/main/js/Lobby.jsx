import React, { useState, useRef } from 'react';
import { useLoaderData, useRouteLoaderData, useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import LobbyUserDisplay from "./LobbyUserDisplay.jsx";

export async function loader({params}) {
  console.log("Loading Lobby " + params.lobbyId)
  const lobbyResponse = await fetch("/api/lobby/"+params.lobbyId);
  const lobby= await lobbyResponse.json();
  return { lobby };
}

export default function Lobby() {

  const navigate = useNavigate();
  const { lobby } = useLoaderData();
  const { userInfo } = useRouteLoaderData("root");
  const isOwner = true; //(lobby.owner.id == userInfo.id);
  const [edit, setEdit]    = useState(false)

  /* this should probably be a seperate component */
  const editNameRef = useRef(null)
  const editCapacityRef = useRef(null)
  const editIsPrivateRef = useRef(null)
  const editBoardSizeRef = useRef(null)
  const editBoardTopologyRef = useRef(null)
  const editFindRuleRef = useRef(null)
  const editDurationRef = useRef(null)

  let leaveLobby = async function(lobbyId) {

    console.log("leaving lobby")

    const response = await fetch("/api/lobby/"+lobbyId+"/leave", {
      method: "POST",
      headers: {
      },
      body: null
    });

    if ( response.status == 200 ) {
      navigate("/lobby");
    } else {
    
      const content  = await response.json();

      console.log(`unable to leave lobby because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

    }


  }

  function onChangeSettings() {
    setEdit(!edit)
  }

  function onDiscardSettingsChanges() {
    console.log("discarding settings changes")
    setEdit(!edit)
  }

  async function onApplySettingsChanges() {

    console.log("submitting settings changes")

    let lobbyUpdateDTO = {
      "name":editNameRef.current.value,
      "capacity":editCapacityRef.current.value,
      "isPrivate":editIsPrivateRef.current.checked,
      "gameSettings":
      {
        "boardSize":editBoardSizeRef.current.value,
        "boardTopology":editBoardTopologyRef.current.value,
        "findRule":editFindRuleRef.current.value,
        "duration":editDurationRef.current.value
      }
    }

    const response = await fetch("/api/lobby/"+lobby.id+"/update", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(lobbyUpdateDTO)
    });

    const authenticateUrl = "http://localhost:8080/login"

    if ( response.status == 200 && response.url != authenticateUrl ) {

      setEdit(!edit)
      let url = "/lobby/" + lobby.id
      navigate(url); //reload this route
      toast.success("Updated");

    } else if ( response.url == authenticateUrl ) {

      toast.error("Authentication Error...");

    } else {
    
      const content  = await response.json();

      console.log(`unable to update lobby because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "CAPACITY_SHORTENING_CONFLICT":
          notice = "Can't shorten lobby beyond current player count."
          break;
        /* Anyone who is not the lobby owner should never see this page,
            is there any point handling this response? */
        case "NOT_AUTHORIZED":
          notice = "This is not your lobby to update."
          break;
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

    }

  }

  function onStartGame() {
    console.log("this is where I would start the game")
  }

  /* compute a badge based on the lobby context */
  function getContextBadge(player) {
     if ( player.id == lobby.owner.id ) {
       return "👑"
     } else {
       return ""
     }
  }

  if ( !lobby ) {
    return (<><div>no lobby data</div></>)
  }

  const settingsGrid = 
    <div id="settings-grid">
      <div className="settings-grid-label">Name</div><div className="settings-grid-value">{lobby.name}</div>
      <div className="settings-grid-label">Capacity</div><div className="settings-grid-value">{lobby.capacity}</div>
      <div className="settings-grid-label">Visibility</div><div className="settings-grid-value">{lobby.isPrivate ? "private" : "public"}</div>
      <div className="settings-grid-label">Size</div><div className="settings-grid-value">{lobby.gameSettings.boardSize}</div>
      <div className="settings-grid-label">Topology</div><div className="settings-grid-value">{lobby.gameSettings.boardTopology}</div>
      <div className="settings-grid-label">Find Rule</div><div className="settings-grid-value">{lobby.gameSettings.findRule}</div>
      <div className="settings-grid-label">Time Limit</div><div className="settings-grid-value">{lobby.gameSettings.duration}</div>
    </div>

  return (
    <>

      <h3 id="lobby-welcome-header">Welcome to <span id="lobby-name-span">{lobby.name}</span></h3>
      <div id="lobby-flex">

        <div id="players-flex-container">
          <div className="players-flex">
            {
              lobby.users.map( (player) => {
                return (
                  <LobbyUserDisplay 
                    player={player} 
                    lobby={lobby} 
                    contextBadge={getContextBadge(player)} 
                    isOwner={isOwner} 
                    isSelf={player.username == userInfo.username}
                  />
                )
              })
            }
          </div>
        </div>

        <div className="center-flex-container">

          <div className="chat-flex">
          </div>

          <div className="player-controls-flex">
            <button className="lobby-exit-button" onClick={() => leaveLobby(lobby.id)} >{isOwner ? "Abandon" : "Leave"}</button>
            <button className="lobby-nudge-button">Nugde</button>
            <div className="player-controls-spacer"></div> {/* padding */}
          </div>

        </div>

        <div id="settings-flex-container">
          { edit ? 
            ( 
              <div id="edit-settings-grid">

                <div className="settings-grid-label">Name</div>
                  <input ref={editNameRef} type="text" defaultValue={lobby.name}/>

                <div className="settings-grid-label">Capacity</div>
                  <input ref={editCapacityRef} type="number" name="capacity" min="1" max="12" defaultValue={lobby.capacity}/>

                <div className="settings-grid-label">Private</div>
                  <input ref={editIsPrivateRef} type="checkbox" defaultChecked={lobby.isPrivate}/>
              
                <div className="settings-grid-label">Size</div>
                  <select ref={editBoardSizeRef} name="size" defaultValue={lobby.gameSettings.boardSize}>
                    <option value="FOUR">4 x 4</option>
                    <option value="FIVE">5 x 5</option>
                  </select>

                <div className="settings-grid-label">Topology</div>
                  <select ref={editBoardTopologyRef} name="topology" defaultValue={lobby.gameSettings.boardTopology}>
                    <option value="PLANE">Plane</option>
                    <option value="CYLINDER">Cylinder</option>
                    <option value="TORUS">Torus</option>
                  </select>
                 
                <div className="settings-grid-label">Find Rule</div>
                  <select ref={editFindRuleRef} name="find" defaultValue={lobby.gameSettings.findRule}>
                    <option value="ANY">Any</option>
                    <option value="UNIQUE">Unique</option>
                    <option value="FIRST">First</option>
                  </select>

                <div className="settings-grid-label">Time Limit</div>
                  <input ref={editDurationRef} type="number" name="time" min="60" max="300" step="30" defaultValue={lobby.gameSettings.duration}/>

                <button id="save-settings-button" onClick={onApplySettingsChanges}>Save</button>
                <button id="discard-settings-button" onClick={onDiscardSettingsChanges}>Discard</button>

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
    </>
  );

}
