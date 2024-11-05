import config from "config" 
import { ServiceResponse, ServiceRequest, HttpMethod } from "/src/main/js/services/ServiceResponse.ts"

interface GameAnswer {
  answer : string
}

export async function GetPostGame(gameId: number,userId: number) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.GET,
    `${config.origin}/api/game/${gameId}/post-game/${userId}`
  );
}

export async function PostGameAnswer(gameId: number, userId: number,answer: GameAnswer) : Promise<ServiceResponse> {
    return await ServiceRequest(
      HttpMethod.POST,
      `${config.origin}/api/game/${gameId}/answer/${userId}`,
      answer
    )
}

