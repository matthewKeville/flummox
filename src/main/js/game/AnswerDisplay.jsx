import React from 'react';

export default function AnswerDisplay({words,onWordClick}) {

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
    let wordClass = word.found ? "word" : "missed-word"
    return (<div key={word.word} className={wordClass}>{word.word}</div>)
  }

  function createColumnGroups(words,sizeClass) {

    let found = words.filter( w => w.found )
    let missed = words.filter( w => !w.found )

    words = found.concat(missed)

    const maxWords = 32
    let column = 0
    
    let columns = []
    while ( column * maxWords < words.length ) {

      let a = maxWords * column
      let b = Math.min( a + maxWords , words.length )
      let columnWords = words.slice(a,b)

      columns.push(
        <div className="word-column-flex">
          {columnWords.map( w => answerToDisplay(w))}
        </div>
      )

      column++

    }

    return columns

  }

  return (
    <div className="word-column-group-flex-container">
      <div className="word-column-group-flex word-group-three">
        {createColumnGroups(wordsBySize.get(3),3)}
      </div>
      <div className="word-column-group-flex word-group-four">
        {createColumnGroups(wordsBySize.get(4),4)}
      </div>
      <div className="word-column-group-flex word-group-five">
        {createColumnGroups(wordsBySize.get(5),5)}
      </div>
      <div className="word-column-group-flex word-group-six">
        {createColumnGroups(wordsBySize.get(6),6)}
      </div>
      <div className="word-column-group-flex word-group-seven">
        {createColumnGroups(wordsBySize.get(7),7)}
      </div>
      <div className="word-column-group-flex word-group-eight">
        {createColumnGroups(wordsBySize.get(8),8)}
      </div>
    </div>
  );


}
