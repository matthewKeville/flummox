import React from 'react';
import { Stack, Text } from '@mantine/core';
import { IconComet } from "@tabler/icons-react";


export default function SiteStats(props) {

  return (
    <Stack h="100%" justify="flex-start" mx="2%">
      <Text>Users Online {3}</Text>
      <Text>Active Lobbies {12}</Text>
      <Text>Games Played {473}</Text>
      <Text size="xs">*placeholder data</Text>
    </Stack>
  )
}
