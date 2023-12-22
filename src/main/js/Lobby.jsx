import React, { useState, useRef, useEffect } from 'react';
import { Outlet } from "react-router-dom";

export async function loader({params}) {
  const lobbyId = params.lobbyId
  return  { lobbyId };
}

export default function PreGame() {

  return (
  <>
      <Outlet />
  </>
  )

}
