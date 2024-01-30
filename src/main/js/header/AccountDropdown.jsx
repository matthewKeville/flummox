import React, { useRef } from 'react';

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

    const dropdown = event.target.closest('.user-info-dropdown')

    if (dropdown && dropdown == dropdownRef.current) {
      return;
    }
    if (dropdownContentRef.current && dropdownContentRef.current.classList.contains('show-flex')) {
      dropdownContentRef.current.classList.remove('show-flex');
    }

  });


  return (

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

  )

}
