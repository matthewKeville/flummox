interface ServiceResponse {
  data?: any,
  success: boolean,
}

enum HttpMethod {
  POST = "POST",
  PUT = "PUT",
  GET = "GET",
  DELETE = "DELETE"
}

async function ServiceRequest(method: HttpMethod,uri: string,data?: any) : Promise<ServiceResponse> {

  let response : Response;

  if ( data == undefined ) {
    response = await fetch(uri,
    {
      method: method
    })
  } else {
    response = await fetch(uri,
    {
      method: method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });
  }

  if (!response.ok || response.redirected == true) {
    console.warn(`ServiceRequest for ${method} : ${uri} failed`)
    console.warn(`Reason ${response.status}  :  ${response.statusText}`) 
    console.warn(`Redirected ${response.redirected} ${response.url}`)
    return { data: undefined, success: false }
  }

  let contentType : string  = response.headers.get("Content-Type")
  let responseData : any

  if ( contentType == "application/json" ) {
    responseData = await response.json()
  } else if (contentType == "text/plain;charset=UTF-8" ) {
    responseData = await response.text()
  } else {
    console.warn(`unexpected Content-Type ${contentType}`)
    console.warn("ServiceRequest data will be undefined")
  }

  return { data: responseData, success: true }

}

export {
  ServiceResponse,
  ServiceRequest,
  HttpMethod
}
