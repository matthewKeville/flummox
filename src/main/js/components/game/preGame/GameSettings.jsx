import React, { useState, useRef } from 'react';
import { useRouteLoaderData } from "react-router-dom";
import { toast } from 'react-toastify';
import { Stack, Group, Text, Button, NumberInput, TextInput, Switch, NativeSelect, Grid, ActionIcon } from "@mantine/core";
import { IconAdjustments } from "@tabler/icons-react";

import { UpdateLobby } from "/src/main/js/services/LobbyService.ts";

export default function GameSettings({lobby}) {

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

  function onChangeSettings() {
    setEdit(!edit)
  }

  function onDiscardSettingsChanges() {
    setEdit(!edit)
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

    <>
      { edit ? 
        ( 
          <Stack>

            <Text >Name</Text>
            <TextInput ref={editNameRef} type="text" defaultValue={lobby.name}/>

            <Text >Capacity</Text>
            <NumberInput ref={editCapacityRef} type="number" name="capacity" min="1" max="12" defaultValue={lobby.capacity}/>

            <Text >Private</Text>
            <Switch ref={editIsPrivateRef} type="checkbox" defaultChecked={lobby.isPrivate}/>
          
            <Text >Size</Text>

            <NativeSelect ref={editBoardSizeRef} checked={lobby.gameSettings.boardSize}
              data={[
                { label: "4x4", value: "FOUR" },
                { label: "5x5", value: "FIVE" },
                { label: "6x6", value: "SIX"  }
              ]}
            />

            <Text >Topology</Text>
            <NativeSelect ref={editBoardTopologyRef} checked={lobby.gameSettings.boardTopology}
              data={[
                { label: "Plane", value: "PLANE" },
                { label: "Cylinder Horizontal", value: "CYLINDER" },
                { label: "Cylinder Vertical", value: "CYLINDER_ALT"  },
                { label: "Torus", value: "TORUS"  }
              ]}
            />

            <Text >Tile Rotation</Text>
            <Switch ref={editTileRotationRef} defaultChecked={lobby.gameSettings.tileRotation}/>
            
             
            <Text >Find Rule</Text>
            <NativeSelect ref={editFindRuleRef} checked={lobby.gameSettings.findRule}
              data={[
                { label: "Any", value: "ANY" },
                { label: "Unique", value: "UNIQUE" },
                { label: "First", value: "FIRST"  },
              ]}
            />

            <Text >Time Limit</Text>
            <NumberInput ref={editDurationRef} min={30} max={300} step={30} defaultValue={lobby.gameSettings.duration}
            />

            <Button onClick={onApplySettingsChanges}>Save</Button>
            <Button onClick={onDiscardSettingsChanges}>Discard</Button>

          </Stack>

        ) 
        : 
        (
          <>

            <Stack align="center">

              <Grid justify="center">

                <Grid.Col span={8}>
                  <Text>Capacity</Text>
                </Grid.Col>
                <Grid.Col span={4}>
                   <Text c="orange.6">{lobby.capacity}</Text>
                </Grid.Col>

                <Grid.Col span={8}>
                  <Text>Visibility</Text>
                </Grid.Col>
                <Grid.Col span={4}>
                  <Text c="orange.6">{lobby.isPrivate ? "private" : "public"}</Text>
                </Grid.Col>

                <Grid.Col span={8}>
                  <Text>Size</Text>
                </Grid.Col>
                <Grid.Col span={4}>
                  <Text c="orange.6">{lobby.gameSettings.boardSize.toLowerCase()}</Text>
                </Grid.Col>

                <Grid.Col span={8}>
                  <Text>Topology</Text>
                </Grid.Col>
                <Grid.Col span={4}>
                  <Text c="orange.6">{lobby.gameSettings.boardTopology}</Text>
                </Grid.Col>

                <Grid.Col span={8}>
                  <Text>Tile Rotation</Text>
                </Grid.Col>
                <Grid.Col span={4}>
                  <Text c="orange.6">{lobby.gameSettings.tileRotation ? "enabled" : "disabled"}</Text>
                </Grid.Col>

                <Grid.Col span={8}>
                  <Text>Find Rule</Text>
                </Grid.Col>
                <Grid.Col span={4}>
                  <Text c="orange.6">{lobby.gameSettings.findRule.toLowerCase()}</Text>
                </Grid.Col>

                <Grid.Col span={8}>
                  <Text>Time Limit</Text>
                </Grid.Col>
                <Grid.Col span={4}>
                  <Text c="orange.6">{lobby.gameSettings.duration}</Text>
                </Grid.Col>

                { isOwner &&
                  <Grid.Col span={4} offset={8}>
                    <ActionIcon onClick={onChangeSettings} variant="filled" color="grape" aria-label="Settings">
                      <IconAdjustments style={{ width: '70%', height: '70%' }} stroke={1.5} />
                    </ActionIcon>
                  </Grid.Col>
                }

              </Grid>


            </Stack>

          </>
        )
      }
    </>
  )

}
