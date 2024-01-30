import React  from 'react';

import AccountControls from '/src/main/js/header/AccountControls.jsx'
import NavBar from '/src/main/js/header/NavBar.jsx'

import styles from '/src/main/js/header/Header.module.css'

export default function Header() {

  return (
    <header className={styles.header}>
      <div className={styles.flex}>

        <div className={styles["left-flex"]}>
          <NavBar/> 
        </div>
        <div className={styles["left-flex-mobile"]}>
          <NavBar mobile={true}/> 
        </div>

        <div className={styles["right-flex"]}>
          <AccountControls/>
        </div>

      </div>
    </header>
  )
}
