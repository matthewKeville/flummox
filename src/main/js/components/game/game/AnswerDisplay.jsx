import React from 'react';
import { Text, Group, Stack, ScrollArea } from "@mantine/core";

export default function AnswerDisplay({answers}) {

  let answersBySize = new Map()
  answersBySize.set(3,[])
  answersBySize.set(4,[])
  answersBySize.set(5,[])
  answersBySize.set(6,[])
  answersBySize.set(7,[])
  answersBySize.set(8,[]) //8+

  for ( let answer of answers ) {

    if ( answer.length < 3 ) {
      continue;
    }
    if ( answer.length < 8 ) {
      answersBySize.get(answer.length).push(answer)
      continue
    }
    answersBySize.get(8).push(answer)

  }

  function createColumnGroup(answers) {

    answers = answers.sort( function(a,b) { return a< b} )

    return (
      <Stack justify="flex-start" align="flex-start">
        {answers.map( answer => 
          (<Text c="black" key={answer}>{answer}</Text>)
        )}
      </Stack>
    )

  }

  return (
    <>
      <ScrollArea w="480px" h="300px">
        <Group justify="flex-start" align="flex-start">
          {createColumnGroup(answersBySize.get(3),3)}
          {createColumnGroup(answersBySize.get(4),4)}
          {createColumnGroup(answersBySize.get(5),5)}
          {createColumnGroup(answersBySize.get(6),6)}
          {createColumnGroup(answersBySize.get(7),7)}
          {createColumnGroup(answersBySize.get(8),8)}
        </Group>
      </ScrollArea>
    </>
  );


}
