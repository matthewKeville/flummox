import React from 'react';
import { useLoaderData } from 'react-router-dom';

export default function Header() {

  const { userInfo } = useLoaderData();

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
