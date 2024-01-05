import React from 'react';

export default function UserDisplay(props) {

  return (
    <div className={props.isSelf ? "user-display user-display-self" : "user-display"}>
      <div>{props.username}</div>
      <div className="user-badge">&nbsp;{props.contextBadge}</div>
    </div>
  )
}

