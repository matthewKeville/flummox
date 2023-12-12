import React, { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider
} from "react-router-dom";

import Lobbies, {loader as lobbiesLoader }  from "./Lobbies.jsx";
import Lobby, {loader as lobbyLoader }      from "./Lobby.jsx";
import ErrorPage from "./Error.jsx";
import Root from "./Root.jsx";

async function rootLoader({params}) {
  console.log("loading user data")

  const userInfoResponse = await fetch("/api/user/info");
  var userInfo = await userInfoResponse.json()

  if ( userInfoResponse.status != 200 || userInfo == null) {
    console.log("there was an error getting user info")
    userInfo = { id:-1, username:"error", isGuest:true }
    return { userInfo }
  }

  return { userInfo };

}

const router = createBrowserRouter([
  {
    path: "/",
    element: <Root />,
    loader: rootLoader,
    id:"root",
    errorElement: <ErrorPage />,
    children: [
      {
        path: "lobby",
        element: <Lobbies />,
        loader: lobbiesLoader,
        id:"lobbies"
      },
      {
        path: "lobby/:lobbyId",
        element: <Lobby />,
        loader: lobbyLoader,
        id:"lobby"
      }
    ]
  },
]);

const root = createRoot(document.getElementById("root"));

root.render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
