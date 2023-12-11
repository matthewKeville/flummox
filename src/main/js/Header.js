import React, { useRef } from 'react';
import { useLoaderData, Link } from 'react-router-dom';

export default function Header() {

  const { userInfo } = useLoaderData();
  const dropdownContentRef = useRef(null);

  const toggleDropdownContent = function(event) {

    // don't toggle visibility when clicking inside dropdown content
    if ( dropdownContentRef.current.contains(event.target) ) {
      return
    }

    dropdownContentRef.current.classList.toggle("show") 

  }

  /*
    * Whenever we click outside of the dropdown toggle the visibility of
    * all dropdowns.
    */
  window.onclick = function(event) {

    const dropdownContainer = event.target.closest('.dropdown')

    if (!dropdownContainer) {

      var dropdowns = document.getElementsByClassName("dropdown-content");
      var i;
      for (i = 0; i < dropdowns.length; i++) {
        var openDropdown = dropdowns[i];
        if (openDropdown.classList.contains('show')) {
          openDropdown.classList.remove('show');
        }
      }
    }
  } 

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
      <div id="user-info-dropdown" className="dropdown" onClick={toggleDropdownContent}>
        <span className="username-span">{userInfo.username}</span>
        <img className="user-profile-icon" src="/icons/user-profile-white-full-trans.png"/>
        <div id="user-info-dropdown-content" className="dropdown-content" ref={dropdownContentRef}>
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
