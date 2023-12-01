import React from 'react';
import Header from "./Header";
import NavBar from "./NavBar.jsx";
import { Outlet } from "react-router-dom";

export default function Root() {
  return (
    <>
      <Header/>
      <NavBar/>
      <Outlet />
    </>
  )
}
