import React, { useState } from 'react';
import {  useRouteLoaderData, useNavigate, useRevalidator } from "react-router-dom";
import { toast } from 'react-toastify';
import { Stack , Button, Group, Text, Tooltip } from '@mantine/core';
import { IconTrash, IconUserShare, IconDoorExit, IconPlayerPlay, 
  IconAdjustments, IconChartHistogram  } from '@tabler/icons-react';

import Chat from "/src/main/js/components/game/preGame/Chat.jsx";
import GameSettings from "/src/main/js/components/game/preGame/GameSettings.jsx";
import config from "config"

import { GetInviteLink, StartLobby, LeaveLobby } from "/src/main/js/services/flummox/LobbyService.ts";
import { CopyToClipboardInsecure } from "/src/main/js/services/ClipboardService.ts";

export default function PreGame({lobby,onReturnToPostGame,playedPrevious}) {

  const navigate = useNavigate();
  const revalidator = useRevalidator();
  const { userInfo } = useRouteLoaderData("root");
  const [showSettings, setShowSettings]  = useState(false)

  const isOwner = (lobby.owner.id == userInfo.id);

  let onFinishSettingsView = function() {
    setShowSettings(false);
  }

  let leaveLobby = async function(lobbyId) {

    let serviceResponse = await LeaveLobby(lobbyId);

    if ( serviceResponse.success ) {
      revalidator.revalidate()
      navigate("/lobby");
      return
    } 

    toast.error(serviceResponse.errorMessage);

  }

  let copyInviteLink = async function() {

    let serviceResponse = await GetInviteLink(lobby.id)

    if ( !serviceResponse.success ) {

      toast.error(serviceResponse.errorMessage);
      return;

    } else {

      var lobbyInviteLink = serviceResponse.data
      CopyToClipboardInsecure(config.origin+lobbyInviteLink)
      toast.info("Invite Copied To Clipboard")

    }
  }


  let startLobby = async function(lobbyId) {

    let serviceResponse = await StartLobby(lobbyId)

    if ( !serviceResponse.success ) {
      toast.error(serviceResponse.errorMessage);
      return;
    } 
  }

  let getLobbyButtons = function() {

    let buttons = []

    buttons.push(
      <Tooltip label="Copy Invite" openDelay={300}>
        <Button color="pink" onClick={() => copyInviteLink()}>
          <IconUserShare/>
        </Button>
      </Tooltip>)

    buttons.push(
      <Tooltip label="Settings" openDelay={300}>
        <Button color="orange" onClick={() => {setShowSettings(true)}}>
          <IconAdjustments/>
        </Button>
      </Tooltip>)

    if (isOwner) {

      buttons.push(
      <Tooltip label="Start" openDelay={300}>
        <Button color="green" onClick={() => startLobby(lobby.id)}>
          <IconPlayerPlay/>
        </Button>
      </Tooltip>)

    } 

    console.log("playedPrevious is " + playedPrevious)

    if (playedPrevious) {

      buttons.push(
      <Tooltip label="Game Review" openDelay={300}>
        <Button color="grape" onClick={onReturnToPostGame}>
          <IconChartHistogram/>
        </Button>
      </Tooltip>)

    }

    buttons.push(
    <Tooltip label="Leave Lobby" openDelay={300}>
      <Button color="red" onClick={() => leaveLobby(lobby.id)} >
        <IconDoorExit/>
      </Button>
    </Tooltip>)

    return ( <Button.Group>{buttons}</Button.Group> )
  }

  if ( !lobby ) {
    return (<></>)
  }

  return (
    <>

      <Stack align="center" justify="flex-start">

        <Text style={{ textAlign: "center" }}>Welcome to {lobby.name}</Text>

        {!showSettings
          ? <Chat lobby={lobby}/> 
          : <GameSettings lobby={lobby} onFinish={onFinishSettingsView}/>
        }

        <Group justify="center">
          {getLobbyButtons()}
        </Group>

      </Stack>

    </>
  )

}
