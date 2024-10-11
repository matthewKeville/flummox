import React from 'react';

/* this component expects a different  data structure depending on the 
  * value of postGame, as of right now postGame requires a word.found value */
export default function AnswerDisplay({words,onWordClick,postGame}) {

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

  function answerToDisplay(word,postGame) {

    let classes = ""
    if ( postGame ) {

      if ( word.found ) {

        classes = "word"

        if (!word.counted) {
          classes = classes + " crossed-word"
        }

      } else {

        classes = "missed-word"

      }

    } else {
      classes = "word"
    }

    return (<div key={word.word} className={classes}>{word.word}</div>)

  }


  function createColumnGroups(words,sizeClass,postGame) {

    let found = [];
    let missed = [];

    if ( postGame ) {
      let foundNotCounted = words.filter( w => w.found && !w.counted )
      let foundCounted = words.filter( w => w.found && w.counted )
      found = foundCounted.concat(foundNotCounted)
      missed = words.filter( w => !w.found )
    } else {
      found = words
    }

    found = found.sort( function(wa,wb) { return wa.word < wb.word } )
    missed = missed.sort( function(wa,wb) { return wa.word < wb.word } )

    /* potentially confusing reusing the parameter */
    words = found.concat(missed)

    const maxWords = 16
    let column = 0
    
    let columns = []
    while ( column * maxWords < words.length ) {

      let a = maxWords * column
      let b = Math.min( a + maxWords , words.length )
      let columnWords = words.slice(a,b)

      columns.push(
        <div className="word-column-flex">
          {columnWords.map( w => answerToDisplay(w,postGame))}
        </div>
      )

      column++

    }

    return columns

  }

  return (
    <div className="word-column-group-flex-container">
      <div className="word-column-group-flex word-group-three">
        {createColumnGroups(wordsBySize.get(3),3,postGame)}
      </div>
      <div className="word-column-group-flex word-group-four">
        {createColumnGroups(wordsBySize.get(4),4,postGame)}
      </div>
      <div className="word-column-group-flex word-group-five">
        {createColumnGroups(wordsBySize.get(5),5,postGame)}
      </div>
      <div className="word-column-group-flex word-group-six">
        {createColumnGroups(wordsBySize.get(6),6,postGame)}
      </div>
      <div className="word-column-group-flex word-group-seven">
        {createColumnGroups(wordsBySize.get(7),7,postGame)}
      </div>
      <div className="word-column-group-flex word-group-eight">
        {createColumnGroups(wordsBySize.get(8),8,postGame)}
      </div>
    </div>
  );


}
