import React, { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createHashRouter, RouterProvider } from "react-router-dom";
import { createTheme, MantineProvider } from "@mantine/core";

import "@mantine/core/styles.css"
// import "/src/main/resources/static/css/style.css"
// import "/src/main/resources/static/css/buttons.css"

import Root, {loader as rootLoader } from "/src/main/js/pages/Root.jsx";
import Home from "/src/main/js/pages/Home.jsx";
import ErrorPage from "/src/main/js/pages/Error.jsx";
import LobbyBrowser, {loader as lobbyBrowserLoader } from '/src/main/js/pages/LobbyBrowser.jsx'
import Lobby, {loader as lobbyLoader } from "/src/main/js/pages/Lobby.jsx";
import Login from "/src/main/js/pages/Login.jsx";
import Register from "/src/main/js/pages/Register.jsx";
import LobbyInvite from "/src/main/js/pages/redirects/LobbyInvite.jsx";
import AccountVerify from "/src/main/js/pages/redirects/AccountVerify.jsx";

const theme = createTheme({
  /** theme overrides */
});

const root = createRoot(document.getElementById("root"));

const router = createHashRouter([
  {
    path: "/",
    element: <Root />,
    loader: rootLoader,
    id:"root",
    errorElement: <ErrorPage />,
    children: [
      {
        path: "/",
        element: <Home />,
        id:"home"
      },
      {
        path: "lobby",
        element: <LobbyBrowser />,
        loader: lobbyBrowserLoader,
        id:"lobbies"
      },
      {
        path: "lobby/:lobbyId",
        element: <Lobby />,
        loader: lobbyLoader,
        id:"lobby",
      },
      {
        path: "join",
        element: <LobbyInvite />,
        id:"lobby-invite",
      },
      {
        path: "login",
        element: <Login />,
        id:"login"
      },
      {
        path: "register",
        element: <Register />,
        id:"register"
      },
      {
        path: "verify",
        element: <AccountVerify />,
        id:"verify"
      }
    ]
  },
]);

root.render(
  <StrictMode>
    <MantineProvider theme={theme} defaultColorScheme="light">
      <RouterProvider router={router} />
    </MantineProvider>
  </StrictMode>
);
