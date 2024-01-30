import React from 'react';
import {  Link } from 'react-router-dom';
import styles from '/src/main/js/header/NavBar.module.css'

/* 
  * TODO :
  * Intent here is to produce different nav bars based on @Media queries
  * horizontal navbar for desktop, vertical (hide-away) navbar for mobile 
  * */
export default function NavBar({mobile}) {

  if (mobile) {
    return (<></>)
  }
  
  return (
    <>
        <span className={styles.span}>Reboggled</span>
        <Link className={styles.link} to="/">Home</Link>
        <Link className={styles.link} to="/lobby">Lobbies</Link>
    </>
  )

}
