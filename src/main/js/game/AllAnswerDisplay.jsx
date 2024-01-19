import React from 'react';
import AnswerDisplay from './AnswerDisplay.jsx';

export default function AllAnswerDisplay({words}) {

  return (
    <AnswerDisplay words={words} postGame={true}/>
  );
}
