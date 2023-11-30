import React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';

export default function Header() {

  const [userInfo,setUserInfo] = useState();

  useEffect( () => {

    async function getUserInfo() {
      const userInfoResponse = await fetch("/api/user/info");
      const data = await userInfoResponse.json()
      setUserInfo(data);
    };
    
    if (!userInfo) {
      getUserInfo()
    }

  } , [] );

  return (
    <header id="header">
      <span id="title-span">Reboggled</span>
      <span id="user-info-span">
        <span id="username-span">{userInfo == null ? "GuestWithLongName" : userInfo.username}</span>
        <img id="profile-icon" src="/icons/user-profile-white-full-trans.png"/>
      </span>
    </header>
  )
}
