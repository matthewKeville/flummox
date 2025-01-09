import React from 'react';
import { Text, Group, Stack, ScrollArea, HoverCard, Popover, Divider } from "@mantine/core";

export default function WordSummaryDisplay({summaries,onHighlightPath}) {

  let summariesBySize = new Map()
  summariesBySize.set(3,[])
  summariesBySize.set(4,[])
  summariesBySize.set(5,[])
  summariesBySize.set(6,[])
  summariesBySize.set(7,[])
  summariesBySize.set(8,[]) //8+

  for ( let summary of summaries ) {

    if ( summary.word.name.length <= 3 ) {
      summariesBySize.get(3).push(summary)
    } else if ( summary.word.name.length < 8 ) {
      summariesBySize.get(summary.word.name.length).push(summary)
    } else {
      summariesBySize.get(8).push(summary)
    }

  }

  function SummaryDisplay(summary) {

    let wordColor;
    let wordDecoration;
    if ( summary.word.counted ) {
      wordColor = "black"
      wordDecoration = "none"
    } else if ( summary.word.found ) {
      wordColor = "grey"
      wordDecoration = "line-through"
    } else {
      wordColor = "grey"
      wordDecoration = "none"
    }
        
    return (
      <HoverCard position="bottom" onOpen={ () => onHighlightPath(summary.word.paths[0])}>
      <HoverCard.Target  >
        <Text style={{textDecoration: {wordDecoration}}} c={wordColor} key={summary.word.name}>{summary.word.name}</Text>
      </HoverCard.Target>
      <HoverCard.Dropdown>
        <Stack>
          <Text ta="center">{summary.word.name}</Text>
          <Divider/>
          {summary.finders.map( f => 
            <Group>
              <Text>{f.username}</Text><Text span>{f.time}</Text>
            </Group>
          )}
        </Stack>
      </HoverCard.Dropdown>
      </HoverCard>
    )

    /*
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
    */

  }


  function createColumnGroup(summaries) {

    let found = [];
    let missed = [];

    let foundNotCounted = summaries.filter( s => s.word.found && !s.word.counted )
    let foundCounted = summaries.filter( s => s.word.found && s.word.counted )

    found = foundCounted.concat(foundNotCounted)
    missed = summaries.filter( s => !s.word.found )

    found = found.sort( function(sa,sb) { return sa.word.name < sb.word.name } )
    missed = missed.sort( function(sa,sb) { return sa.word.name < sb.word.name } )

    summaries = found.concat(missed)

    return (
      <Stack justify="flex-start" align="flex-start">
        {summaries.map( s => SummaryDisplay(s))}
      </Stack>
    )

  }

  return (
    <>
      <ScrollArea w="480px" h="300px">
        <Group justify="flex-start" align="flex-start">
          {createColumnGroup(summariesBySize.get(3),3)}
          {createColumnGroup(summariesBySize.get(4),4)}
          {createColumnGroup(summariesBySize.get(5),5)}
          {createColumnGroup(summariesBySize.get(6),6)}
          {createColumnGroup(summariesBySize.get(7),7)}
          {createColumnGroup(summariesBySize.get(8),8)}
        </Group>
      </ScrollArea>
    </>
  );


}
