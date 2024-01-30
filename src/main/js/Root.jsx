import React from 'react';
import { Outlet } from "react-router-dom";
import { ToastContainer } from 'react-toastify';

import Header from "/src/main/js/Header.jsx"

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
