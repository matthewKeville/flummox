import React from 'react';

export default function UserDisplay(props) {

  return (
    <div className="user-display">
      <div>{props.username}</div>
      <div className="user-badge">&nbsp;{props.contextBadge}</div>
    </div>
  )
}

