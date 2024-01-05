import React, { useState, useEffect } from 'react';

export default function GameTimer({gameEnd,onGameEnd}) {

  const [time, setTime]    = useState(null)

  function calculateTime() {

    let end = Date.parse(gameEnd)
    let timeSpan = Math.floor((end - Date.now()) / 1000) /*ms*/;
    setTime(timeSpan)

  }

  if ( time == null ) {
    calculateTime()
  }

  function update(timeNow) {
    if ( timeNow > 0 ) {
      //setTime(time => time -1)
      setTime(timeNow)
      const iid = setTimeout(update,1000,timeNow-1)
    } else {
      setTime(0)
      console.log("going to postgame")
      onGameEnd()
    }
  }

  useEffect(() => {
    calculateTime()
    const iid = setTimeout(update,1000,time)
    return () => {
      clearTimeout(iid)
    }
  }, []);


  return ( 
    <>
      <span className="game-timer" style={{color: "orange"}}>{time}</span>
    </>
  )
}
