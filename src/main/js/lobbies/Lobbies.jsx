import React from 'react';
import { useLoaderData, useRouteLoaderData, useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import styles from '/src/main/js/lobbies/Lobbies.module.css';

export async function loader({params}) {

  console.log(`loading lobbies list`)

  const lobbiesResponse = await fetch("/api/lobby/view/lobby");
  const lobbies = await lobbiesResponse.json()

  return { lobbies };

}

export default function Lobbies() {

  const { lobbies } = useLoaderData();
  const { userInfo } = useRouteLoaderData("root");
  const navigate    = useNavigate();

  let joinLobby = async function(lobbyId) {

    const response = await fetch("/api/lobby/"+lobbyId+"/join", {
      method: "POST",
      headers: {
      },
      body: null
    });

    if (response.status == 200) {

      navigate("/lobby/" + lobbyId+"/");

    } else {

      const content = await response.json()

      console.log(`unable to join lobby because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
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



  if ( !lobbies ) {
    return (<><div>no lobbies</div></>)
  }

  return (
    <>

    <div className={styles["container"]}>
      <table className={styles.table}>
        <caption className={styles.caption}>
          Lobbies
        </caption>

        <thead>
          <tr>
            <th className={styles["essential-field"]}>lobby</th>
            <th className={styles["essential-field"]}>party</th>
            <th className={styles["non-essential-field"]}>public</th>
            <th></th>
          </tr>
        </thead>

        <tbody>
        {
          lobbies.map( (lobby) => {
            return (
              <tr key={lobby.id}>
                <td className={styles["essential-field"]} >{lobby.name}</td>
                <td className={styles["essential-field"]} >{lobby.users.length} / {lobby.capacity}</td>
                <td className={styles["non-essential-field"]} >{lobby.isPrivate ? "locked" : "open"}</td>
                <td>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-right-square-fill" viewBox="0 0 16 16" onClick={() => joinLobby(lobby.id)}>
                    <path d="M0 14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H2a2 2 0 0 0-2 2zm4.5-6.5h5.793L8.146 5.354a.5.5 0 1 1 .708-.708l3 3a.5.5 0 0 1 0 .708l-3 3a.5.5 0 0 1-.708-.708L10.293 8.5H4.5a.5.5 0 0 1 0-1"/>
                  </svg>
                </td>
              </tr>
            )
          })
        }
        </tbody>
      </table>
    </div>

    </>
  );

}
