import React from 'react';
import AnswerDisplay from './AnswerDisplay.jsx';

export default function UserAnswerDisplay({words}) {

  return (
    <AnswerDisplay words={words} postGame={false}/>
  );

}
