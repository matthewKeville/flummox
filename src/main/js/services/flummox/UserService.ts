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

export async function Logout() : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/user/logout`
  );
}

export async function Register(registerDTO: any) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/user/register`,
    registerDTO
  );
}

export async function VerifyAccount(verifyAccountDTO:any) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/user/verify`,
    verifyAccountDTO
  );
}
