import React from 'react';
import { Outlet } from "react-router-dom";
import { ToastContainer } from 'react-toastify';

import Header from "/src/main/js/header/Header.jsx"
import { Paper } from '@mantine/core';

export async function loader({params}) {

  const userInfoResponse = await fetch("/api/user/info");
  var userInfo = await userInfoResponse.json()

  if ( userInfoResponse.status != 200 || userInfo == null) {
    console.log("there was an error getting user info")
    userInfo = { id:-1, username:"error", isGuest:true }
    return { userInfo }
  }

  console.log('loaded userInfo', userInfo)
  return { userInfo };

}

export default function Root() {
  return (
    <>
      <Header/>
      <Paper withBorder>
        <Outlet />
      </Paper>
      <ToastContainer
        autoClose={1000}
        theme="light"
      />
    </>
  )
}
