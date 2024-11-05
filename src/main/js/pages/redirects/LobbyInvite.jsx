import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from "react-router-dom";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { JoinLobby } from "/src/main/js/services/flummox/LobbyService.ts";

export default function LobbyInvite() {

  const navigate    = useNavigate();
  const [searchParams, setSearchParams ] = useSearchParams();

  let joinLobby = async function(lobbyId,token) {
    let serviceResponse = await JoinLobby(lobbyId,token)
    if ( !serviceResponse.success ) {
      toast.error(serviceResponse.errorMessage)
      return
    }
    navigate("/lobby/"+lobbyId)
  }

  useEffect(() => {
    console.log(searchParams)
    joinLobby(searchParams.get("id"),searchParams.get("token"))
  }, []);

  return (<></>);

}
