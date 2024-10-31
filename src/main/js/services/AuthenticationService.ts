import config from "config";

export async function Logout() : Promise<ServiceResponse<undefined>> {

  const response = await fetch(config.origin+"/logout", {
    method: "POST",
  });

  if ( response.ok ) {

    console.log("logout success")
    return {
      data: undefined,
      success: true,
      errorMessage: undefined
    }

  } else {

    let errorMessage = "failed to logout";

    return {
      data: undefined,
      success: false,
      errorMessage: errorMessage
    }

  }

}

export async function Register(registerDTO: any) : Promise<ServiceResponse<undefined>> {

  const response = await fetch(config.origin+"/register", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(registerDTO)
  });

  const content = await response.json()

  if ( response.ok ) {

    console.log("register success")

    return {
      data: content,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("register failed")

    let errorMessage = "failed to register";

    return {
      data: content,
      success: false,
      errorMessage: errorMessage
    }

  }

}

export async function VerifyAccount(verifyAccountDTO:any) : Promise<ServiceResponse<string>> {

  console.log(verifyAccountDTO)

  const response = await fetch(config.origin+"/verify", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(verifyAccountDTO)
  });

  if ( response.ok ) {

    console.log("verify success")

    return {
      data: null,
      success: true,
      errorMessage: undefined
    }

  } else {

    console.log("verify failed")

    let errorMessage = "failed to verify account";

    return {
      data: null,
      success: false,
      errorMessage: errorMessage
    }

  }

}
