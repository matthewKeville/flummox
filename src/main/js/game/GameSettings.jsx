import React, { useState, useRef } from 'react';
import { useRouteLoaderData, useNavigate} from "react-router-dom";
import { toast } from 'react-toastify';

export default function GameSettings({lobby}) {

  const navigate = useNavigate();
  const { userInfo } = useRouteLoaderData("root");

  const [edit, setEdit]    = useState(false)
  const editNameRef = useRef(null)
  const editCapacityRef = useRef(null)
  const editIsPrivateRef = useRef(null)
  const editBoardSizeRef = useRef(null)
  const editBoardTopologyRef = useRef(null)
  const editFindRuleRef = useRef(null)
  const editDurationRef = useRef(null)

  // Don't render if API error
  if (lobby == null) {
    return
  }

  const isOwner = (lobby.owner.id == userInfo.id);

  function onChangeSettings() {
    setEdit(!edit)
  }

  function onDiscardSettingsChanges() {
    console.log("discarding settings changes")
    setEdit(!edit)
  }

  async function onApplySettingsChanges() {

    console.log("submitting settings changes")

    let lobbyUpdate = {}

    let newName = editNameRef.current.value 
    if ( newName != lobby.name ) {
      lobbyUpdate.name = newName
    }

    let newCapacity = editCapacityRef.current.value 
    if ( newCapacity != lobby.capacity ) {
      lobbyUpdate.capacity = newCapacity
    }

    let newGameSettings = {}

    let newBoardSize = editBoardSizeRef.current.value;
    if ( newBoardSize != lobby.gameSettings.boardSize ) {
      newGameSettings.boardSize = newBoardSize
    }

    let newBoardTopology = editBoardTopologyRef.current.value;
    if ( newBoardTopology != lobby.gameSettings.boardTopology ) {
      newGameSettings.boardTopology = newBoardTopology
    }

    let newFindRule = editFindRuleRef.current.value;
    if ( newFindRule != lobby.gameSettings.findRule ) {
      newGameSettings.findRule = newFindRule
    }

    let newDuration = editDurationRef.current.value;
    if ( newDuration != lobby.gameSettings.duration ) {
      newGameSettings.duration = newDuration
    }

    if (newGameSettings != {}) {
      lobbyUpdate.gameSettings = newGameSettings
    }

    console.log(JSON.stringify(lobbyUpdate,null,2))

    const response = await fetch("/api/lobby/"+lobby.id+"/update", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(lobbyUpdate)
    });

    const authenticateUrl = "http://localhost:8080/login"

    if ( response.status == 200 && response.url != authenticateUrl ) {

      setEdit(!edit)
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


  return (

    <>
      { edit ? 
        ( 
          <div className="game-settings-grid thick-blue-border">

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
              <option value="SIX">6 x 6</option>
            </select>

            <div className="settings-grid-label">Topology</div>
            <select ref={editBoardTopologyRef} name="topology" defaultValue={lobby.gameSettings.boardTopology}>
              <option value="PLANE">Plane</option>
              <option value="CYLINDER">Cylinder (H)</option>
              <option value="CYLINDER_ALT">Cylinder (V)</option>
              <option value="TORUS">Torus</option>
            </select>
             
            <div className="settings-grid-label">Find Rule</div>
            <select ref={editFindRuleRef} name="find" defaultValue={lobby.gameSettings.findRule}>
              <option value="ANY">Any</option>
              <option value="UNIQUE">Unique</option>
              <option value="FIRST">First</option>
            </select>

            <div className="settings-grid-label">Time Limit</div>
            <input ref={editDurationRef} type="number" name="time" min="30" max="300" step="30" defaultValue={lobby.gameSettings.duration}/>

            <button id="save-settings-button" className="basic-button" onClick={onApplySettingsChanges}>Save</button>
            <button id="discard-settings-button" className="alternate-button" onClick={onDiscardSettingsChanges}>Discard</button>

          </div>

        ) 
        : 
        (
          <div className="game-settings-grid thick-blue-border">
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
                <button id="edit-settings-button" className="basic-button" onClick={onChangeSettings}>Edit</button>
                </>
              ) 
              :
              (<></>)
            }
          </div>
        )
      }
    </>
  )

}
