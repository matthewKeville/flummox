import React, { useState, useRef } from 'react';
import { useRouteLoaderData } from "react-router-dom";
import { toast } from 'react-toastify';
import { Stack, Group, Text, Button, NumberInput, TextInput, Switch, NativeSelect, Grid, ActionIcon } from "@mantine/core";

import { UpdateLobby, DeleteLobby } from "/src/main/js/services/LobbyService.ts";

export default function GameSettings({lobby, onFinish}) {

  const { userInfo } = useRouteLoaderData("root");
  const [edit, setEdit]    = useState(false)
  const editNameRef = useRef(null)
  const editCapacityRef = useRef(null)
  const editIsPrivateRef = useRef(null)
  const editBoardSizeRef = useRef(null)
  const editBoardTopologyRef = useRef(null)
  const editTileRotationRef = useRef(null)
  const editFindRuleRef = useRef(null)
  const editDurationRef = useRef(null)

  const isOwner = (lobby.owner.id == userInfo.id);

  let deleteLobby = async function(lobbyId) {

    let serviceResponse = await DeleteLobby(lobbyId)

    if ( !serviceResponse.success ) {

      toast.error(serviceResponse.errorMessage);
      return;

    } else {

      revalidator.revalidate()
      navigate("/lobby")

    }
  }

  async function onApplySettingsChanges() {

    //Lobby Settings

    let lobbyUpdate = {}

    if ( editNameRef.current.value  != lobby.name ) {
      lobbyUpdate.name = editNameRef.current.value 
    }

    if ( editCapacityRef.current.value  != lobby.capacity ) {
      lobbyUpdate.capacity = editCapacityRef.current.value 
    }

    if ( editIsPrivateRef.current.checked != lobby.isPrivate ) {
      lobbyUpdate.isPrivate = editIsPrivateRef.current.checked
    }

    let newGameSettings = {}

    //Board Settings

    if ( editBoardSizeRef.current.value != lobby.gameSettings.boardSize ) {
      newGameSettings.boardSize = editBoardSizeRef.current.value;
    }

    if ( editBoardTopologyRef.current.value != lobby.gameSettings.boardTopology ) {
      newGameSettings.boardTopology = editBoardTopologyRef.current.value;
    }

    if ( editTileRotationRef.current.checked != lobby.gameSettings.tileRotation ) {
      newGameSettings.tileRotation = editTileRotationRef.current.checked
    }

    //Game Settings

    if ( editFindRuleRef.current.value != lobby.gameSettings.findRule ) {
      newGameSettings.findRule = editFindRuleRef.current.value;
    }

    if ( editDurationRef.current.value != lobby.gameSettings.duration ) {
      newGameSettings.duration = editDurationRef.current.value;
    }

    if (newGameSettings != {}) {
      lobbyUpdate.gameSettings = newGameSettings
    }

    let serviceResponse = await UpdateLobby(lobby.id,lobbyUpdate);

    if ( !serviceResponse.success ) {
      toast.error(serviceResponse.errorMessage);
      return
    }

    setEdit(!edit)
    toast.success("Updated");

  }

  if (lobby == null) {
    return
  }

  return (
    <Stack>

      <Text >Name</Text>
      <TextInput disabled={!isOwner} ref={editNameRef} type="text" defaultValue={lobby.name}/>

      <Text >Capacity</Text>
      <NumberInput disabled={!isOwner} ref={editCapacityRef} type="number" name="capacity" min="1" max="12" defaultValue={lobby.capacity}/>

      <Text >Private</Text>
      <Switch disabled={!isOwner} ref={editIsPrivateRef} type="checkbox" defaultChecked={lobby.isPrivate}/>
    
      <Text >Size</Text>

      <NativeSelect disabled={!isOwner} ref={editBoardSizeRef} checked={lobby.gameSettings.boardSize}
        data={[
          { label: "4x4", value: "FOUR" },
          { label: "5x5", value: "FIVE" },
          { label: "6x6", value: "SIX"  }
        ]}
      />

      <Text >Topology</Text>
      <NativeSelect disabled={!isOwner} ref={editBoardTopologyRef} checked={lobby.gameSettings.boardTopology}
        data={[
          { label: "Plane", value: "PLANE" },
          { label: "Cylinder Horizontal", value: "CYLINDER" },
          { label: "Cylinder Vertical", value: "CYLINDER_ALT"  },
          { label: "Torus", value: "TORUS"  }
        ]}
      />

      <Text >Tile Rotation</Text>
      <Switch disabled={!isOwner} ref={editTileRotationRef} defaultChecked={lobby.gameSettings.tileRotation}/>
      
       
      <Text >Find Rule</Text>
      <NativeSelect disabled={!isOwner} ref={editFindRuleRef} checked={lobby.gameSettings.findRule}
        data={[
          { label: "Any", value: "ANY" },
          { label: "Unique", value: "UNIQUE" },
          { label: "First", value: "FIRST"  },
        ]}
      />

      <Text >Time Limit</Text>
      <NumberInput disabled={!isOwner} ref={editDurationRef} min={30} max={300} step={30} defaultValue={lobby.gameSettings.duration}
      />

      {isOwner
        ?   <> 
              <Button color="green" onClick={() => { onApplySettingsChanges(); onFinish(); }}>Save</Button>
              <Button color="green" variant="outline" onClick={() => { onFinish() }}>Return</Button>
              <Button color="red" onClick={() => { deleteLobby() }}>Delete</Button>
            </>
        :   <> 
              <Button onClick={() => { onFinish() }}>Return</Button>
            </>
      }

    </Stack>
  )

}
