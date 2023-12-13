import React, { useRef } from 'react';
import UserDisplay from "./UserDisplay.jsx";
import { useRevalidator } from "react-router-dom";
import { toast } from 'react-toastify';

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

    const response = await fetch("/api/lobby/"+props.lobby.id+"/kick/"+props.player.id, {
      method: "POST",
      headers: {
      },
      body: null
    });

    const authenticateUrl = "http://localhost:8080/login"

    if ( response.status == 200 && response.url != authenticateUrl ) {

      revalidator.revalidate();

    } else if ( response.url == authenticateUrl) {

      toast.error("Authentication Error...");

    } else {
    
      const content  = await response.json();

      console.log(`unable to leave kick player because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "NOT_IN_LOBBY":
          notice = " Kick target is not in lobby "
          break;
        case "NOT_AUTHORIZED":
          notice = "You are not the lobby owner "
          break;
        default:
        case "INTERNAL_ERROR":
          //pass
      }

      toast.error(notice);

    }

  }

  const promotePlayer = async function() {

    const response = await fetch("/api/lobby/"+props.lobby.id+"/promote/"+props.player.id, {
      method: "POST",
      headers: {
      },
      body: null
    });

    const authenticateUrl = "http://localhost:8080/login"

    if ( response.status == 200 && response.url != authenticateUrl ) {

      revalidator.revalidate();

    } else if ( response.url == authenticateUrl) {

      toast.error("Authentication Error...");

    } else {
    
      const content  = await response.json();

      console.log(`unable to promote player because : ${content.message}`)

      let notice = content.status + " : Unknown error"

      switch(content.message) {
        case "NOT_IN_LOBBY":
          notice = " Kick target is not in lobby "
          break;
        case "NOT_AUTHORIZED":
          notice = "You are not the lobby owner "
          break;
        default:
        case "INTERNAL_ERROR":
          //pass
      }

      toast.error(notice);

    }

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
      <UserDisplay username={props.player.username} contextBadge={props.contextBadge} isSelf={props.isSelf} />
      <div id="user-action-dropdown-content" className="user-action-dropdown-content" ref={userActionDropdownContentRef}>
      <div className="user-action-dropdown-content-actions">
        { props.isOwner ? ownerUserActions : allUserActions }
      </div>
      </div>
    </div>

  )

}

