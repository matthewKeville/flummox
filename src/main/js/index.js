import React, { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { createTheme, MantineProvider } from "@mantine/core";

import "@mantine/core/styles.css"
import "/src/main/resources/static/css/style.css"
import "/src/main/resources/static/css/buttons.css"

import Root, {loader as rootLoader } from "/src/main/js/pages/Root.jsx";
import ErrorPage from "/src/main/js/pages/Error.jsx";
import LobbyBrowser, {loader as lobbyBrowserLoader } from '/src/main/js/pages/LobbyBrowser.jsx'
import Lobby, {loader as lobbyLoader } from "/src/main/js/pages/Lobby.jsx";
import LobbyInvite from "/src/main/js/pages/redirects/LobbyInvite.jsx";

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
        element: <LobbyBrowser />,
        loader: lobbyBrowserLoader,
        id:"lobbies"
      },
      {
        path: "lobby/:lobbyId",
        element: <Lobby />,
        loader: lobbyLoader,
        id:"lobby",
        children: []
      },
      {
        path: "join",
        element: <LobbyInvite />,
        id:"lobby-invite",
        children: []
      }
    ]
  },
]);

const theme = createTheme({
  defaultRadius: 'md',
});

const root = createRoot(document.getElementById("root"));

root.render(
  <StrictMode>
    <MantineProvider theme={theme} defaultColorScheme="dark">
      <RouterProvider router={router} />
    </MantineProvider>
  </StrictMode>
);
