import React, { useRef } from 'react';
import { useLoaderData } from 'react-router-dom';

import AccountDropdown from '/src/main/js/header/AccountDropdown.jsx'
import styles from '/src/main/js/header/AccountControls.module.css'

export default function AccountControls() {

  const { userInfo } = useLoaderData();

  return (

    <>
    <div className={styles.div}>
      { userInfo.isGuest &&
        <>
          <a className={styles.login + " alternate-button link"} href="/login">Login</a>
          <a className={styles.signup + " tertiary-button link"} href="/signup">Sign Up</a>
        </>
      }
    </div>
    <AccountDropdown userInfo={userInfo}/>
    </>

  )

}
