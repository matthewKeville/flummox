import React from 'react';
import { Stack, Text, Textarea } from "@mantine/core";

export default function Chat({lobby}) {

  if (lobby == null) {
    return
  }

  return (

    <>
      <Stack w="480px" h="320px" align="flex-start" justify="flex-end" gap="xs" bd="2px solid black">
        <Text> yummi : Yo what's up </Text>
        <Text> ada : Let's play </Text>
        <Text> echo : bet </Text>
        <Textarea w="100%"/>
      </Stack>
    </>
  )

}
