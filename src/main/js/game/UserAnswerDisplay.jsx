import React from 'react';
import AnswerDisplay from './AnswerDisplay.jsx';

export default function UserAnswerDisplay({words}) {

  /* this a bandage to reconcile the differences in game and post game data structures */
  let adjusted = words.map( word => structuredClone(word) )
  adjusted.forEach( word => { word.found = true; word.word = word.answer} )
  console.log(" adjusted ")
  console.log(adjusted)

  return (
    <AnswerDisplay words={adjusted}/>
  );
}
