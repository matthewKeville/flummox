import config from "config" 
import { ServiceResponse, ServiceRequest, HttpMethod } from "/src/main/js/services/ServiceResponse.ts"

export async function GetStats() : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.GET,
    `${config.origin}/api/stats`
  );
}
