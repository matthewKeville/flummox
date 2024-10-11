import React from 'react';
import { useLoaderData, useRouteLoaderData, useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import styles from '/src/main/js/lobbies/Lobbies.module.css';
import { Table, Container, Text, Button, Center, Flex } from '@mantine/core';

export async function loader({ params }) {
  const lobbiesResponse = await fetch("/api/lobby/view/lobby");
  const lobbies = await lobbiesResponse.json()
  console.log('loaded lobbies', lobbies)
  return { lobbies };
}

export default function Lobbies() {
  const { lobbies } = useLoaderData();
  const { userInfo } = useRouteLoaderData("root");
  const navigate = useNavigate();

  let joinLobby = async function (lobbyId) {

    const response = await fetch("/api/lobby/" + lobbyId + "/join", {
      method: "POST",
      headers: {},
      body: null
    });
    console.log('received lobby info: ', await response.json())

    if (response.status == 200) {
      navigate("/lobby/" + lobbyId + "/");
    } else {
      const content = response.json()
      console.log(`unable to join lobby because : ${content.message}`)
      let notice = content.status + " : Unknown error"

      switch (content.message) {
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

  let createLobby = async function () {

    console.log("creating lobby")

    const response = await fetch("/api/lobby/create", {
      method: "POST",
      headers: {
      },
      body: null
    });

    const content = await response.json();

    if (response.status == 201) {

      console.log(content)

      navigate("/lobby/" + content.id);

    } else {

      console.log(`unable to create lobby because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch (content.message) {
        case "INTERNAL_ERROR":
        default:
        //pass
      }

      toast.error(notice);

    }


  }

  if (!lobbies) {
    return (<><div>no lobbies</div></>)
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

      <Table miw={700} className={styles.table}>
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
