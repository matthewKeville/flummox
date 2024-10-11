import React from 'react';

import AnswerDisplay from '/src/main/js/components/game/AnswerDisplay.jsx';

export default function AllAnswerDisplay({words}) {

  return (
    <AnswerDisplay words={words} postGame={true}/>
  );
}
