import React  from 'react';

import AccountControls from '/src/main/js/header/AccountControls.jsx'
import NavBar from '/src/main/js/header/NavBar.jsx'

export default function Header() {

  return (
    <header>
      <div className="header-flex">

        <div className="header-left-flex">
          <NavBar/> 
        </div>

        <div className="header-left-flex-mobile">
          <span className="title-span">Reboggled</span>
        </div>

        <div className="header-right-flex">
          <AccountControls/>
        </div>

      </div>
    </header>
  )
}
