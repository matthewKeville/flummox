import React from 'react';
import Header from "./Header";
import { Outlet } from "react-router-dom";
import { ToastContainer } from 'react-toastify';

export default function Root() {
  return (
    <>
      <Header/>
      <Outlet />
      <ToastContainer
        autoClose={1000}
        theme="light"
      />
    </>
  )
}
