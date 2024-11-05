import config from "config";
import { ServiceResponse, ServiceRequest, HttpMethod } from "/src/main/js/services/ServiceResponse.ts"

export async function Logout() : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/logout`
  );
}

export async function Register(registerDTO: any) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/register`,
    registerDTO
  );
}

export async function VerifyAccount(verifyAccountDTO:any) : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/verify`,
    verifyAccountDTO
  );
}
