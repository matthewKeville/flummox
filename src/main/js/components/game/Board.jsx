import React from 'react';
import { Text, SimpleGrid, Center, Flex } from "@mantine/core";

function getTileString(tile) {
  switch(tile.code) {
    case 0:
      return ""
    case 1:
      return "Qu"
    case 2:
      return "In"
    case 3:
      return "Th"
    case 4:
      return "Er"
    case 5:
      return "He"
    case 6:
      return "An"
    default:
      return String.fromCharCode(tile.code)
  }
}

/* 
  * The letters look off compared to the real life board because they are 'square spaced'
  * i.e. the width and height of each letter is equal. This is a non-standard font-type and
  * the best we can do for rn is a monospaced font. Down the line pulling in a custom square
  * font would yield a more aesthetic result
  */
function Die({tile, handleClick}) {

  let transform = "rotate("+tile.rotation+"deg)"

  return (
    <Flex  justify="center" align="center" bg="gray.2">
      <Center w="100%" h="100%" style={{borderRadius: "40%"}} bg="white">
        <Text style={{transform: `rotate(${tile.rotation}deg)`}} ff="monospace" fw="1000" ta="center" c="blue" size="2.5rem">
          {getTileString(tile)}
        </Text>
      </Center>
    </Flex>
  );
}

export default function Board({dice}) {

  return (
    <Center w="320px" style={{aspectRatio: "1/1"}} >
      <SimpleGrid style={{borderRadius: "5%"}} spacing="4" p="4%" w="100%" h="100%" bg="blue" cols={Math.sqrt(dice.length)}>
        {dice.map( (die, i ) => {
          return (
            <Die key={i} tile={dice[i]}/>
          )
        })}
      </SimpleGrid>
    </Center>
  )

}
