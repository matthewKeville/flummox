import React, { useState } from 'react';
import { Text, SimpleGrid, Center, Flex, Group, Stack } from "@mantine/core";
import { IconArrowForwardUp } from "@tabler/icons-react";

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
function Die({tile, handleClick, tileRotation}) {

  let transform = "rotate("+tileRotation+"deg)"

  return (
    <Flex  justify="center" align="center" bg="gray.2">
      <Center w="100%" h="100%" style={{borderRadius: "40%"}} bg="white">
        <Text style={{transform: `rotate(${tileRotation}deg)`}} ff="monospace" fw="1000" ta="center" c="blue" size="2.5rem">
          {getTileString(tile)}
        </Text>
      </Center>
    </Flex>
  );
}

export default function Board({dice,tileRotationEnabled}) {

  console.log(" is tile rotationEnabled? " + tileRotationEnabled)

  const [rotation,setRotation] = useState(0)

  function rotate(positive) {
    if ( positive ) {
      setRotation((rotation+1)%4)
      return
    }  
    setRotation( rotation == 0 ? 3 : rotation-1 )
  }

  function rotateLeft() {
    rotate(true)
  }
  function rotateRight() {
    rotate(false)
  }

  function turn() {
    return 90*rotation
  }
  //

  return (
    <Stack>
      <Center w="320px" style={{aspectRatio: "1/1"}} >
        <SimpleGrid style={{borderRadius: "5%",transform: `rotate(${turn()}deg)`}} spacing="4" p="4%" w="100%" h="100%" bg="blue" cols={Math.sqrt(dice.length)}>
          {dice.map( (die, i ) => {
            let tileRotation = tileRotationEnabled ? die.rotation : -turn()
            return ( <Die key={i} tile={dice[i]} tileRotation={tileRotation}/> )
          })}
        </SimpleGrid>
      </Center>
        <Group justify="space-between">
          <IconArrowForwardUp 
            size={24} 
            style={{transform: "rotate(-90deg)"}} 
            onClick={() => rotateLeft() } 
          />
          <IconArrowForwardUp 
            size={24} 
            style={{transform: "scaleX(-1) rotate(-90deg)"}} 
            onClick={() => rotateRight() } 
          />
        </Group>
    </Stack>
  )

}
