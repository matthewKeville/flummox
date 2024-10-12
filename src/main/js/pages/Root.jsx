import React from 'react';
import { Outlet } from "react-router-dom";
import { ToastContainer } from 'react-toastify';

import Header from "/src/main/js/components/header/Header.jsx"
import { GetUserInfo } from "/src/main/js/services/UserService.ts"

export async function loader({ params }) {
  var UserInfoResponse = await GetUserInfo()
  var userInfo = UserInfoResponse.data
  return { userInfo }
}

export default function Root() {
  return (
    <>
      <Header />
      <Outlet />
      <ToastContainer
        autoClose={1000}
        theme="light"
      />
    </>
  )
}
