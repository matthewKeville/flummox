import React, { useRef } from 'react';
import { useLoaderData } from 'react-router-dom';

import AccountDropdown from '/src/main/js/header/AccountDropdown.jsx'

export default function AccountControls() {

  const { userInfo } = useLoaderData();

  return (

    <>
    <div className="user-button-cluster-flex">
      { userInfo.isGuest &&
        <>
          <a className="alternate-button header-login-link" href="/login">Login</a>
          <a className="tertiary-button  header-signup-link" href="/signup">Sign Up</a>
        </>
      }
    </div>
    <AccountDropdown userInfo={userInfo}/>
    </>

  )

}
