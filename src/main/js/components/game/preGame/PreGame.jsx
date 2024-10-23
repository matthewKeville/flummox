import React from 'react';
import {  useRouteLoaderData, useNavigate, useRevalidator } from "react-router-dom";
import { toast } from 'react-toastify';
import { Grid,Container,Button,Group,Text } from '@mantine/core';

import PlayerList from "/src/main/js/components/game/preGame/playerList/PlayerList.jsx";
import GameSettings from "/src/main/js/components/game/preGame/GameSettings.jsx";

import { GetInviteLink, StartLobby, LeaveLobby, DeleteLobby } from "/src/main/js/services/LobbyService.ts";
import { CopyToClipboardInsecure } from "/src/main/js/services/ClipboardService.ts";

export default function PreGame({lobby,playedPrev,onReturnToPostGame}) {

  const navigate = useNavigate();
  const revalidator = useRevalidator();
  const { userInfo } = useRouteLoaderData("root");

  const isOwner = (lobby.owner.id == userInfo.id);

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

    let serviceResponse = await GetInviteLink()

    if ( !serviceResponse.success ) {

      toast.error(serviceResponse.errorMessage);
      return;

    } else {

      var lobbyInviteLink = serviceResponse.data
      CopyToClipboardInsecure(lobbyInviteLink)
      toast.info("Invite Copied To Clipboard")

    }
  }

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

  let startLobby = async function(lobbyId) {

    let serviceResponse = await StartLobby(lobbyId)

    if ( !serviceResponse.success ) {
      toast.error(serviceResponse.errorMessage);
      return;
    } 
  }

  if ( !lobby ) {
    return (<></>)
  }

  return (
    <>

      <Container>

      <Text style={{ textAlign: "center" }}>Welcome to {lobby.name}</Text>

      <Grid justify="center">

        <Grid.Col span={2}>
          <Group justify="center">
            <PlayerList lobby={lobby}/>
          </Group>
        </Grid.Col>

        <Grid.Col span={8}>

          <Group justify="center">

            <Button.Group>
            { isOwner &&
              <Button color="green" onClick={() => startLobby(lobby.id)}>Start</Button> 
            }

            { playedPrev &&
                <Button color="grape" onClick={onReturnToPostGame}>Last</Button>
            }

            { <Button color="orange" onClick={() => copyInviteLink()}>Invite</Button> }

            { isOwner ? 
              <Button color="red" onClick={() => deleteLobby(lobby.id)} >Delete</Button>
              :
              <Button color="red" onClick={() => leaveLobby(lobby.id)} >Leave</Button>
            }
            </Button.Group>
        
          </Group>

        </Grid.Col>

        <Grid.Col span={2}>
          <Group justify="center">
            <GameSettings lobby={lobby}/>
          </Group>
        </Grid.Col>

      </Grid>

      </Container>
    </>
  )

}
