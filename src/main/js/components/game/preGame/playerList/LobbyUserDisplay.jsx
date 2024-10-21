import React from 'react';
import { useRevalidator } from "react-router-dom";
import { toast } from 'react-toastify';

import { Menu,Text } from '@mantine/core';

import { KickPlayer, PromotePlayer } from "/src/main/js/services/LobbyService.ts";

export default function LobbyUserDisplay(props) {

  const revalidator                       = useRevalidator(null)

  const kickPlayer = async function() {

    let serviceResponse = await KickPlayer(props.lobby.id,props.player.id)

    if ( serviceResponse.success ) {
      revalidator.revalidate();
      return
    }

    toast.error(serviceResponse.errorMessage);

  }

  const promotePlayer = async function() {

    let serviceResponse = await PromotePlayer(props.lobby.id,props.player.id)

    if ( serviceResponse.success ) {
      revalidator.revalidate();
      return
    }

    toast.error(serviceResponse.errorMessage);

  }

  const getActions = function(isOwner) {

    const userActions = 
      <>
        <Menu.Item>Placeholder</Menu.Item>
      </>

    const ownerActions = 
      <>
        {userActions}
        <Menu.Item onClick={promotePlayer}>Promote</Menu.Item>
        <Menu.Item onClick={kickPlayer}>Kick</Menu.Item>
      </>

    return isOwner ? ownerActions : userActions

  }

  return (

    <>
      <Menu trigger="hover" openDelay={100} closeDelay={50} style={{borderStyle: "solid", borderWidth: "1px", borderColor: "orange"}}>

        <Menu.Target>
          <Text size="lg"> {`${props.player.username} ${props.contextBadge}`} </Text>
        </Menu.Target>

        <Menu.Dropdown>
          {getActions(props.isOwner)}
        </Menu.Dropdown>

      </Menu>
    </>

  )

}

