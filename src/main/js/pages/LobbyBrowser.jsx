import React from 'react';
import { useLoaderData, useRouteLoaderData, useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Table, Container, Text, Button, Center, Flex } from '@mantine/core';

import { CreateLobby, JoinLobby, GetLobbySummaries } from "/src/main/js/services/LobbyService.ts";

import styles from './LobbyBrowser.module.css';

export async function loader({ params }) {
  let serviceResponse = await GetLobbySummaries();
  let lobbies = serviceResponse.data
  return { lobbies }
}

export default function Lobbies() {

  const { lobbies } = useLoaderData();
  const { userInfo } = useRouteLoaderData("root");
  const navigate = useNavigate();

  let joinLobby = async function (lobbyId) {
    let serviceResponse = await JoinLobby(lobbyId);
    if ( !serviceResponse.success ) {
      toast.error(serviceResponse.errorMessage)
      return
    }
    navigate("/lobby/"+lobbyId)
  }

  let createLobby = async function () {
    let serviceResponse = await CreateLobby()
    if ( !serviceResponse.success ) {
      toast.error("unable to create lobby");
      return
    }
    navigate("/lobby/" + serviceResponse.data)
  }

  if (!lobbies) {
    return (<></>)
  }

  const rows = lobbies.map((lobby) => (
    <Table.Tr key={lobby.id}>
      <Table.Td>{lobby.name}</Table.Td>
      <Table.Td>{lobby.users.length} / {lobby.capacity}</Table.Td>
      <Table.Td>{lobby.isPrivate ? "locked" : "open"}</Table.Td>
      <Table.Td>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-arrow-right-square-fill" viewBox="0 0 16 16" onClick={() => joinLobby(lobby.id)}>
          <path d="M0 14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H2a2 2 0 0 0-2 2zm4.5-6.5h5.793L8.146 5.354a.5.5 0 1 1 .708-.708l3 3a.5.5 0 0 1 0 .708l-3 3a.5.5 0 0 1-.708-.708L10.293 8.5H4.5a.5.5 0 0 1 0-1" />
        </svg>
      </Table.Td>
    </Table.Tr>
  ))

  return (
    <Container className={styles.container}>

      <Text className={styles.caption}>
        Lobbies
      </Text>

      <Table className={styles.table}>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Lobby</Table.Th>
            <Table.Th>Party</Table.Th>
            <Table.Th>Public</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {rows}
          {!userInfo.isGuest &&
            <Table.Tr>
              <Table.Td></Table.Td>
              <Table.Td></Table.Td>
              <Table.Td></Table.Td>
              <Table.Td>
                <Button className={styles.createbtn} onClick={createLobby}>Create</Button>
              </Table.Td>
            </Table.Tr>
          }
        </Table.Tbody>
      </Table>

    </Container>
  );

}
