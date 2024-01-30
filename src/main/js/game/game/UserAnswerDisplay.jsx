import React from 'react';

import AnswerDisplay from "/src/main/js/game/AnswerDisplay.jsx"

export default function UserAnswerDisplay({words}) {

  return (
    <AnswerDisplay words={words} postGame={false}/>
  );

}
