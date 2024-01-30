import React from 'react';
import {  Link } from 'react-router-dom';

/* 
  * TODO :
  * Intent here is to produce different nav bars based on @Media queries
  * horizontal navbar for desktop, vertical (hide-away) navbar for mobile 
  * */
export default function NavBar() {

  return (
    <>
        <span className="title-span">Reboggled</span>
        <Link className="header-link" to="/">Home</Link>
        <Link className="header-link" to="/lobby">Lobbies</Link>
    </>
  )

}
