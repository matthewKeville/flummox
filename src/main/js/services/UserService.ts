
interface UserInfo {
  id: number,
  username: string,
  isGuest: boolean
}

export async function GetUserInfo() : Promise<UserInfo> {

  const userInfoResponse = await fetch("/api/user/info");
  var userInfo : UserInfo = { 
    id: -1,
    username: "error",
    isGuest: true
  }

  if (userInfoResponse.status != 200 || userInfo == null) {
    console.log('UserInfo failed to load')
  }

  if (userInfoResponse.status == 200 || userInfo != null) {
    userInfo = await userInfoResponse.json()
    console.log('UserInfo loaded')

  }

  return userInfo

}
