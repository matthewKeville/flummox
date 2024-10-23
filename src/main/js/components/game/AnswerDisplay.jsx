import React from 'react';
import { Text, Group, Stack } from "@mantine/core";

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

    let fg="black"
    let decoration="none"

    if ( postGame ) {
      if ( word.found ) {
        if (word.counted) {
          fg="black"
        } else {
          decoration="line-through"
          fg="blue"
        }
      } else {
        fg="grey"
      }
    } else {
      fg="black"
    }

    return (<Text c={fg} td={decoration} key={word.word}>{word.word}</Text>)

  }


  function createColumnGroup(words,postGame) {

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

    return (
      <Stack justify="flex-start" align="flex-start">
        {words.map( w => answerToDisplay(w,postGame))}
      </Stack>
    )

  }

  return (
    <>
      <Group justify="flex-start" align="flex-start">
        {createColumnGroup(wordsBySize.get(3),3,postGame)}
        {createColumnGroup(wordsBySize.get(4),4,postGame)}
        {createColumnGroup(wordsBySize.get(5),5,postGame)}
        {createColumnGroup(wordsBySize.get(6),6,postGame)}
        {createColumnGroup(wordsBySize.get(7),7,postGame)}
        {createColumnGroup(wordsBySize.get(8),8,postGame)}
      </Group>
    </>
  );


}
