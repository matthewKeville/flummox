import React, { useState, useEffect } from 'react';
import { Text, Center } from '@mantine/core';

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

  /* 
    * todo pass total game time into this component to calculate percentage
    * breakpoints for time color shifts
    */

  let minutes = Math.floor(time/60);
  let seconds = time - (60*minutes)
  let color = seconds < 10 ? "red" : "yellow"

  return ( 

    /* i'm bothered by this not being exactly centered*/

    <>
      <Center>
        { ( minutes > 0 ) ?
          <Text c="black" size="1.5rem">{`${minutes} : ${seconds.toString().padStart(2,0)}`}</Text> :
          <Text c={color} size="1.5rem">{`${seconds.toString()}`}</Text>
        }
      </Center>
    </>
  )
}
