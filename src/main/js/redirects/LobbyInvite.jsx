import React, { useEffect } from 'react';
import { useLoaderData, useRouteLoaderData, useNavigate, useSearchParams } from "react-router-dom";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export default function LobbyInvite() {

  const { userInfo } = useRouteLoaderData("root");
  const navigate    = useNavigate();
  const [searchParams, setSearchParams ] = useSearchParams();

  //duplicate code (Lobbies.jsx)

  let joinLobby = async function(lobbyId,token) {

    const response = await fetch("/api/lobby/"+lobbyId+"/join?token="+token, {
      method: "POST",
      headers: {},
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
          navigate("/");
          break;
        case "LOBBY_IS_PRIVATE":
          notice = " Unable to join lobby because it is private"
          navigate("/");
          break;
        case "ALREADY_IN_LOBBY":
          navigate("/lobby/" + lobbyId);
          break;;
        case "INTERNAL_ERROR":
        default:
          //pass
      }

      toast.error(notice);

    }

  }

  useEffect(() => {
    console.log(searchParams)
    joinLobby(searchParams.get("id"),searchParams.get("token"))
  }, []);

  return (

    <>
    </>

  );

}
