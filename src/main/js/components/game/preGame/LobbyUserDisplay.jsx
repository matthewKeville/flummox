import React, { useRef } from 'react';
import { useRevalidator } from "react-router-dom";
import { toast } from 'react-toastify';

import { KickPlayer, PromotePlayer } from "/src/main/js/services/LobbyService.ts";

export default function LobbyUserDisplay(props) {

  const userActionDropdown                = useRef(null)
  const userActionDropdownContentRef      = useRef(null)
  const revalidator                       = useRevalidator(null)

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

  const kickPlayer = async function() {

    let serviceResponse = await KickPlayer(props.lobby.id,props.player.id)

    if ( serviceResponse.success ) {
      revalidator.revalidate();
      return
    }

    toast.error(serviceResponse.errorMessage);

  }

  const promotePlayer = async function() {

    let serviceResponse = await PromotePlayer(props.lobby.id,props.player.id)

    if ( serviceResponse.success ) {
      revalidator.revalidate();
      return
    }

    toast.error(serviceResponse.errorMessage);

  }

  const allUserActions = 
    <>
      <button>Placeholder</button>
    </>

  const ownerUserActions = 
    <>
      {allUserActions}
      <button onClick={promotePlayer}>Promote</button>
      <button onClick={kickPlayer}>Kick</button>
    </>

  return (
    <div id="user-action-dropdown" ref={userActionDropdown} className="user-action-dropdown" onClick={toggleUserActionDropdownContent}>

      <div className={props.isSelf ? "user-display user-display-self" : "user-display"}>
        <div>{props.player.username}</div>
        <div className="user-badge">&nbsp;{props.contextBadge}</div>
      </div>

      <div id="user-action-dropdown-content" className="user-action-dropdown-content" ref={userActionDropdownContentRef}>
      <div className="user-action-dropdown-content-actions">
        { props.isOwner ? ownerUserActions : allUserActions }
      </div>
      </div>
    </div>

  )

}

