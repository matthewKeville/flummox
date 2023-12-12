import React, { useRef } from 'react';
import UserDisplay from "./UserDisplay.jsx";

export default function LobbyUserDisplay(props) {

  const userActionDropdown           = useRef(null)
  const userActionDropdownContentRef = useRef(null)

  const toggleUserActionDropdownContent = function(event) {

    //don't toggle when clicking inside dropdown contents
    if ( userActionDropdownContentRef.current.contains(event.target) ) {
      return
    }

    userActionDropdownContentRef.current.classList.toggle("show") 

  }

  //close this dropdown whenever we click outside of it
  window.addEventListener('click', function(event) {

    const dropdown = event.target.closest('.user-action-dropdown')

    //is the click outside this particular user-action-dropdown
    if (dropdown && dropdown == userActionDropdown.current) {
      return;
    }

    //toggle visibility
    if (userActionDropdownContentRef.current && userActionDropdownContentRef.current.classList.contains('show')) {
      userActionDropdownContentRef.current.classList.remove('show');
    }

  })

  const allUserActions = 
    <>
      <button>Add Friend</button>
    </>

  const ownerUserActions = 
    <>
      {allUserActions}
      <button>Promote</button>
      <button>Kick</button>
    </>

  return (
    <div id="user-action-dropdown" ref={userActionDropdown} className="user-action-dropdown" onClick={toggleUserActionDropdownContent}>
      <UserDisplay username={props.username} contextBadge={props.contextBadge} isSelf={props.isSelf} />
      <div id="user-action-dropdown-content" className="user-action-dropdown-content" ref={userActionDropdownContentRef}>
      <div className="user-action-dropdown-content-actions">
        { props.isOwner ? ownerUserActions : allUserActions }
      </div>
      </div>
    </div>

  )

}

