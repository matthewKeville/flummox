import React from 'react';
import Header from "./Header";
import NavBar from "./NavBar.jsx";
import { Outlet } from "react-router-dom";
import { ToastContainer } from 'react-toastify';

export default function Root() {
  return (
    <>
      <Header/>
      <NavBar/>
      <Outlet />
      <ToastContainer
        autoClose={3000}
        theme="light"
      />
    </>
  )
}
