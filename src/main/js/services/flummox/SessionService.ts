//webpack alias resolves to config/local.json or  config/prod.json
import config from "config" 
import { ServiceResponse, ServiceRequest, HttpMethod } from "/src/main/js/services/ServiceResponse.ts"

export async function KeepAlive() : Promise<ServiceResponse> {
  return await ServiceRequest(
    HttpMethod.POST,
    `${config.origin}/api/session/active`
  );
}
