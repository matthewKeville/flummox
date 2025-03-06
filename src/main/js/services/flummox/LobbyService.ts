import config from "config" 
import { ServiceResponse, ServiceRequest, HttpMethod } from "/src/main/js/services/ServiceResponse.ts"

interface LobbyChat {
  message : string
}

export async function GetLobbySummaries() : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.GET,
    `${config.origin}/api/lobby/summary`
  );
}

export async function CreateLobby() : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/lobby/create`
  );
}

export async function UpdateLobby(lobbyId: number,lobbyUpdate: any) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/lobby/${lobbyId}/update`,
    lobbyUpdate
  );
}

export async function DeleteLobby(lobbyId: number) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.DELETE,
    `${config.origin}/api/lobby/${lobbyId}`
  );
}


export async function JoinLobby(lobbyId: number,token?: string) : Promise<ServiceResponse> {

  let request : string = `${config.origin}/api/lobby/${lobbyId}/join`
  if ( typeof token !== "undefined" ) {
    request += "?token="+token;
  }

  return await ServiceRequest(
    HttpMethod.POST,
    request
  );

}

export async function LeaveLobby(lobbyId: number) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/lobby/${lobbyId}/leave`
  );
}

export async function StartLobby(lobbyId: number) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/lobby/${lobbyId}/start`
  );
}

export async function KickPlayer(lobbyId: number,playerId: number) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/lobby/${lobbyId}/kick/${playerId}`
  );
}

export async function PromotePlayer(lobbyId: number,playerId: number) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/lobby/${lobbyId}/promote/${playerId}`
  );
}

export async function GetInviteLink(lobbyId: number) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.GET,
    `${config.origin}/api/lobby/${lobbyId}/invite`
  );
}

export async function SendLobbyChat(lobbyId: number,lobbyChat: LobbyChat) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/lobby/${lobbyId}/messages`,
    lobbyChat
  );
}

export async function GetLobbyMessages(lobbyId: number) : Promise<ServiceResponse> {
  console.log("GetLobbyMessages" + lobbyId);
  return await ServiceRequest(
    HttpMethod.GET,
    `${config.origin}/api/lobby/${lobbyId}/messages`
  );
}
