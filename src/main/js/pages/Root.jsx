import React, { useState } from 'react';
import { Outlet } from "react-router-dom";
import { ToastContainer } from 'react-toastify';
import { AppShell, NavLink } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";

import Header from "/src/main/js/components/header/Header.jsx"
import { GetUserInfo } from "/src/main/js/services/flummox/UserService.ts"

export async function loader({ params }) {
  var UserInfoResponse = await GetUserInfo()
  var userInfo = UserInfoResponse.data
  console.log(userInfo)
  return { userInfo }
}

export default function Root() {

  const [hideNavBar, setHideNavBar] = useState(true)
  const [opened, { toggle }] = useDisclosure(!hideNavBar,
    {
      onOpen: () => { setHideNavBar(false) },
      onClose: () => { setHideNavBar(true) }
    }
  );

  return ( 
    <AppShell
      padding="md"
      header={{ height: { base: 48/*rem*/, sm: 60 }}}
      navbar={{
        width: "144", /*rem*/
        breakpoint: 'sm',
        collapsed: { mobile: hideNavBar, desktop: hideNavBar },
        padding:"md"
      }}
    >

      <AppShell.Header>
        <Header navbarIsOpen={!hideNavBar} navbarOnClick={toggle}/>
      </AppShell.Header>

      <AppShell.Navbar p="md">
        <NavLink
          label="Home"
          href="#/"
          onClick={ toggle }
        />
        <NavLink
          label="Lobbies"
          href="#/lobby"
          onClick={ toggle }
        />
        <NavLink
          label="Analytics"
          disabled
          onClick={ toggle }
        />
      </AppShell.Navbar>

      {/*https://mantine.dev/core/app-shell/*/}
      <AppShell.Main style={{height: "calc(100vh - var(--app-shell-header-height))"}}>
        <Outlet/>
      </AppShell.Main>

      <ToastContainer
        autoClose={1000}
        theme="light"
      />

    </AppShell>  
  )
}
