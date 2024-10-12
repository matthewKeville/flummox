
interface UserInfo {
  id: number,
  username: string,
  isGuest: boolean
}

export async function GetUserInfo() : Promise<ServiceResponse<UserInfo>> {

  const userInfoResponse = await fetch("/api/user/info");

  if (userInfoResponse.status == 200 || userInfoResponse != null) {
    console.log('UserInfo loaded')
    return {
      data: await userInfoResponse.json() as UserInfo,
      success:  true,
      errorMessage: undefined
    }
  }

  else {
    console.log('UserInfo failed to load')
    return {
      data: undefined,
      success:  false,
      errorMessage: "UserService : GetUserInfo failed"
    }
  }


}
