import React, { useRef } from 'react';
import { useLoaderData, Link } from 'react-router-dom';
import styles from '/src/main/resources/static/css/button.module.css';

export default function Header() {

  const { userInfo } = useLoaderData();
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

    const dropdown = event.target.closest('.user-info-dropdown')

    if (dropdown && dropdown == dropdownRef.current) {
      return;
    }
    if (dropdownContentRef.current && dropdownContentRef.current.classList.contains('show-flex')) {
      dropdownContentRef.current.classList.remove('show-flex');
    }

  });

  return (
    <header>

      <div className="header-flex">

        <div className="header-left-flex">
            <span className="title-span">Reboggled</span>
            <Link className="header-link" to="/">Home</Link>
            <Link className="header-link" to="/lobby">Lobbies</Link>
        </div>

        <div className="header-left-flex-mobile">
            <span className="title-span">Reboggled</span>
        </div>

        <div className="header-right-flex">

          <div className="user-button-cluster-flex">
            { userInfo.isGuest &&
              <>
                <a className={styles["alternate-button"] + " header-login-link"} href="/login">Login</a>
                <a className={styles["tertiary-button"] + " header-signup-link"} href="/signup">Sign Up</a>
              </>
            }
          </div>

          <div id="user-info-dropdown" className="user-info-dropdown" onClick={toggleDropdownContent} ref={dropdownRef}>
            <span className="username-span">{userInfo.username}</span>
            <img className="user-profile-icon" src="/icons/user-profile-white-full-trans.png"/>
            <div id="user-info-dropdown-content" className="user-info-dropdown-content" ref={dropdownContentRef}>
              {userInfo.isGuest ? 
                <div> No Guest Settings </div>
                :
                <>
                  <a className="logout-link" href="/logout">logout</a>
                </>
              }
            </div>
          </div>
        </div>

      </div>
    </header>
  )
}