//webpack alias resolves to config/local.json or  config/prod.json
import config from "config" 
import { ServiceResponse, ServiceRequest, HttpMethod } from "/src/main/js/services/ServiceResponse.ts"

interface UserInfo {
  id: number,
  username: string,
  isGuest: boolean
}

export async function GetUserInfo() : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.GET,
    `${config.origin}/api/user/info`);
}

