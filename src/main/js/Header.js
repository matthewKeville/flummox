import React, { useRef } from 'react';
import { useLoaderData, Link } from 'react-router-dom';

export default function Header() {

  const { userInfo } = useLoaderData();
  const dropdownRef  = useRef(null);
  const dropdownContentRef = useRef(null);

  const toggleDropdownContent = function(event) {

    // don't toggle when clicking inside the dropdown
    if ( dropdownContentRef.current.contains(event.target) ) {
      return
    }

    dropdownContentRef.current.classList.toggle("show") 

  }

  //close this drop down if we click outside of it
  window.addEventListener('click', function(event) {

    const dropdown = event.target.closest('.user-info-dropdown')

    if (dropdown && dropdown == dropdownRef.current) {
      return;
    }
    if (dropdownContentRef.current && dropdownContentRef.current.classList.contains('show')) {
      dropdownContentRef.current.classList.remove('show');
    }

  });

  const guestLinks = 
  <>
    <a className="user-cluster-link" href="/login">Login</a>
    <a className="user-cluster-link" href="#">Sign Up</a>
  </>

  const dropdownContent = 
  <>
    <a className="logout-link" href="/logout">logout</a>
  </>

  const guestDropdownContent = 
  <>
    <div> No Guest Settings </div>
  </>

  const userDropdown  = 
    <>
      <div id="user-info-dropdown" className="user-info-dropdown" onClick={toggleDropdownContent} ref={dropdownRef}>
        <span className="username-span">{userInfo.username}</span>
        <img className="user-profile-icon" src="/icons/user-profile-white-full-trans.png"/>
        <div id="user-info-dropdown-content" className="user-info-dropdown-content" ref={dropdownContentRef}>
          {userInfo.isGuest ? guestDropdownContent : dropdownContent }
        </div>
      </div>
    </>

  return (
    <header>
      <div className="title-flex-container">
        <div className="title-flex">
          <span className="title-span">Reboggled</span>
          <div  className="nav-div">
            <Link className="nav-link" to="/">Home</Link>
            <Link className="nav-link" to="/lobby">Lobbies</Link>
          </div>
        </div>
      </div>
      <div className="user-cluster-flex-container">
        <div className="user-cluster-flex">
          {userInfo.isGuest ? guestLinks : <></>}
          {userDropdown}
        </div>
      </div>
    </header>
  )
}
