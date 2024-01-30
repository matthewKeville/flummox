import React, { useRef } from 'react';

import styles from '/src/main/js/header/AccountDropdown.module.css'

export default function AccountDropdown({userInfo}) {

  const dropdownRef  = useRef(null);
  const dropdownContentRef = useRef(null);

  const toggleDropdownContent = function(event) {

    // don't toggle when clicking inside the dropdown
    if ( dropdownContentRef.current.contains(event.target) ) {
      return
    }

    dropdownContentRef.current.classList.toggle("show-flex") 

  }

  //close this drop down if we click outside of it
  window.addEventListener('click', function(event) {

    const dropdown = event.target.closest("." + styles.dropdown)

    if (dropdown && dropdown == dropdownRef.current) {
      return;
    }
    if (dropdownContentRef.current && dropdownContentRef.current.classList.contains('show-flex')) {
      dropdownContentRef.current.classList.remove('show-flex');
    }

  });


  return (

    <div className={styles.dropdown} onClick={toggleDropdownContent} ref={dropdownRef}>

      <span className={styles.span} >{userInfo.username}</span>
      <img className={styles.img}  src="/icons/user-profile-white-full-trans.png"/>

      <div className={styles["dropdown-content"]} ref={dropdownContentRef}>
        {userInfo.isGuest ? 
          <div> No Guest Settings </div>
          :
          <a className={styles.a}  href="/logout">logout</a>
        }
      </div>
    </div>

  )

}
