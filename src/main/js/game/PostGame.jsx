import React, { useState, useRef, useEffect } from 'react';

export default function PostGame({lobby,onReturnToLobby}) {
  return ( 
    <>
      <button style={{color: "green"}} onClick={onReturnToLobby}>Return To Lobby</button>
    </>
  )
}
