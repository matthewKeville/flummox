import React, { useState, useRef, useEffect } from 'react';

// import { useLoaderData, useRouteLoaderData, useNavigate } from "react-router-dom";
// import { toast } from 'react-toastify';

export async function loader({params}) {
  const lobbyId = params.lobbyId
  return  { lobbyId };
}

export default function Game({params}) {
  return ( 
    <>
      <div style={{color: "red"}}> Under Construction </div>
      <div> Game for lobby</div>
    </>
  )
}
