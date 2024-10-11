import React from 'react';
import { useRouteLoaderData } from "react-router-dom";

import ScoreboardUserDisplay from "/src/main/js/components/game/postgame/ScoreboardUserDisplay.jsx"

export default function Scoreboard({lobby,scoreboard}) {

  const { userInfo } = useRouteLoaderData("root");

  if (lobby == null) {
    return
  }

  return (
    <div className="scoreboard-flex thick-blue-border">
      {
        scoreboard.map( (entry) => {
          let username = lobby.users.find( user => user.id == entry.userId ).username
          return (
            <ScoreboardUserDisplay scoreboardEntry={entry} username={username} />
          )
        })
      }
    </div>
  )

}
