import config from "config";

export function Login(formData) {
  console.log("formData is ")
  console.log(formData)
}

export async function Logout() : Promise<ServiceResponse<undefined>> {

  const response = await fetch(config.origin+"/logout", {
    method: "POST",
  });

  const content = await response.json()

  if ( response.ok ) {

    console.log("logout success")
    return {
      data: content,
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

  console.log('hit register')

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

    let errorMessage = "failed to logout";

    return {
      data: content,
      success: false,
      errorMessage: errorMessage
    }

  }

}
