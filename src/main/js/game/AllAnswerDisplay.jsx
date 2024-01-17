import React from 'react';

export default function AllAnswerDisplay({words}) {

  let wordsBySize = new Map()

  wordsBySize.set(3,[])
  wordsBySize.set(4,[])
  wordsBySize.set(5,[])
  wordsBySize.set(6,[])
  wordsBySize.set(7,[])
  wordsBySize.set(8,[]) //8+

  for ( let word of words ) {

    if ( word.word.length < 3 ) {
      continue;
    }
    if ( word.word.length < 8 ) {
      wordsBySize.get(word.word.length).push(word)
      continue
    }
    wordsBySize.get(8).push(word)

  }

  function answerToDisplay(word) {
    let wordClass = word.found ? "found-word" : "missed-word"
    return (<div key={word.word} className={wordClass}>{word.word}</div>)
  }

  return (
    <div className="word-columns">
      <div className="three">
        {wordsBySize.get(3).map( w => answerToDisplay(w))}
      </div>
      <div className="four">
        {wordsBySize.get(4).map( w => answerToDisplay(w))}
      </div>
      <div className="five">
        {wordsBySize.get(5).map( w => answerToDisplay(w))}
      </div>
      <div className="six">
        {wordsBySize.get(6).map( w => answerToDisplay(w))}
      </div>
      <div className="seven">
        {wordsBySize.get(7).map( w => answerToDisplay(w))}
      </div>
      <div className="eight-plus">
        {wordsBySize.get(8).map( w => answerToDisplay(w))}
      </div>
    </div>
  );
}
