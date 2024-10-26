export async function UpdateLobby(lobbyId: number,lobbyUpdate: any) : Promise<ServiceResponse<undefined>> {

  const response = await fetch("/api/lobby/"+lobbyId+"/update", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(lobbyUpdate)
  });

  const content = await response.json()

  if ( response.ok ) {

    console.log("UpdateLobby success")
    return {
      data: content,
      success: true,
      errorMessage: undefined
    }

  } else {

    let errorMessage = "";

    switch(content.message) {
      case "CAPACITY_SHORTENING_CONFLICT":
        errorMessage = "Can't shorten lobby beyond current player count."
        break;
      case "NOT_AUTHORIZED":
        errorMessage = "This is not your lobby to update."
        break;
      case "INTERNAL_ERROR":
      default:
        errorMessage = "Unable to update lobby because server error"
    }

    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }

  }

}

export async function SendLobbyChat(lobbyId: number,message: any) : Promise<ServiceResponse<undefined>> {

  let messageDTO: any = {}
  messageDTO.message = message

  const response = await fetch("/api/lobby/"+lobbyId+"/messages", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(messageDTO)
  });

}

//TODO rename this entity to LobbyHeader
export async function GetLobbySummaries() : Promise<ServiceResponse<Array<any>>> {

  const response = await fetch("/api/lobby/summary")
  const content = await response.json()

  if ( response.ok ) {

    console.log("GetLobbySummaries success")
    return {
      data: content,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("GetLobbySummaries failed")
    let errorMessage = "Unable to get lobby summaries because server error";
    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}

export async function CreateLobby() : Promise<ServiceResponse<number>> {

  const response = await fetch("/api/lobby/create", {
    method: "POST",
    headers: {},
    body: null
  });

  const content  = await response.json();

  if ( response.ok ) {

    console.log("CreateLobby success")
    return {
      data: content.id,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("CreateLobby failed")
    let errorMessage = "Unable to create lobby because server error"
    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}

export async function DeleteLobby(lobbyId: number) : Promise<ServiceResponse<void>> {


  const response = await fetch("/api/lobby/"+lobbyId, {
    method: "DELETE",
    body: null
  });

  if ( response.ok ) {

    console.log("DeleteLobby success")
    return {
      data: undefined,
      success: true,
      errorMessage: undefined
    }

  } else {
    
    console.log("DeleteLobby failed")
    let errorMessage = "Unable to delete lobby because server error"
    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}

export async function KickPlayer(lobbyId: number,playerId: number) : Promise<ServiceResponse<void>> {

  const response = await fetch("/api/lobby/"+lobbyId+"/kick/"+playerId, {
      method: "POST",
      headers: {
      },
      body: null
    });

  const content  = await response.json();

  if ( response.ok  ) {

    console.log("KickPlayer success")
    return {
      data: undefined,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("KickPlayer failed")
    let errorMessage = ""
    switch (content.message) {
      case "NOT_IN_LOBBY":
        errorMessage = " Kick target is not in lobby "
        break;
      case "NOT_AUTHORIZED":
        errorMessage = "You are not the lobby owner "
        break;
      default:
      case "INTERNAL_ERROR":
        errorMessage = "Unable to kick player because server error"
    }

    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}
export async function PromotePlayer(lobbyId: number,playerId: number) : Promise<ServiceResponse<void>> {

  const response = await fetch("/api/lobby/"+lobbyId+"/promote/"+playerId, {
      method: "POST",
      headers: {
      },
      body: null
    });

  const content  = await response.json();

  if ( response.ok ) {

    console.log("PromotePlayer success")
    return {
      data: undefined,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("PromotePlayer failed")
    let errorMessage = ""
    switch (content.message) {
      case "NOT_IN_LOBBY":
        errorMessage = " Kick target is not in lobby "
        break;
      case "NOT_AUTHORIZED":
        errorMessage = "You are not the lobby owner "
        break;
      default:
      case "INTERNAL_ERROR":
        errorMessage = "Unable to promote player because server error"
    }

    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}

export async function JoinLobby(lobbyId: number,token?: string) : Promise<ServiceResponse<void>> {

  let request = "/api/lobby/" + lobbyId + "/join"
  if ( typeof token !== "undefined" ) {
    request += "?token="+token;
  }

  const response = await fetch(request, {
    method: "POST",
    headers: {},
    body: null
  });

  const content  = await response.json();

  if ( response.ok) {

    console.log("JoinLobby success")
    return {
      data: undefined,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("JoinLobby failed")

    let errorMessage = ""
    switch (content.message) {
      case "LOBBY_IS_FULL":
        errorMessage = "Unable to join lobby because it is full"
        break;
      case "LOBBY_IS_PRIVATE":
        errorMessage = "Unable to join lobby because it is private"
        break;
      case "ALREADY_IN_LOBBY":
        //silent fail
        break;
      case "INTERNAL_ERROR":
      default:
        errorMessage = "Unable to join lobby because server error"
    }

    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}

export async function LeaveLobby(lobbyId: number) : Promise<ServiceResponse<void>> {

  const response = await fetch("/api/lobby/"+lobbyId+"/leave", {
    method: "POST",
    headers: {
    },
    body: null
  });

  if ( response.ok ) {

    console.log("LeaveLobby success")
    return {
      data: undefined,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("LeaveLobby failed")

    let errorMessage = "Unable to leave lobby because server error"

    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}

export async function GetInviteLink() : Promise<ServiceResponse<string>> {

  const response = await fetch("/api/lobby/invite", {
    method: "GET",
    headers: {},
    body: null
  });

  let content = await response.text()

  if ( response.ok) {

    console.log("GetInviteLink success")
    return {
      data: content,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("GetInviteLink failed")

    let errorMessage = "Unable to get invite link because server error"

    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}

export async function StartLobby(lobbyId: number) : Promise<ServiceResponse<void>> {

  const response = await fetch("/api/lobby/"+lobbyId+"/start", {
    method: "POST",
    headers: {
    },
    body: null
  });

  if ( response.ok ) {

    console.log("StartLobby success")
    return {
      data: undefined,
      success: true,
      errorMessage: undefined
    }

  } else {
    console.log("StartLobby failed")
    let errorMessage = "Unable to start lobby because server error"

    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }
  }

}
