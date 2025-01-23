import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Group, Stack, Grid, Text, Burger, Flex } from '@mantine/core';
import { IconComet } from "@tabler/icons-react";

import AccountControls from '/src/main/js/components/header/AccountControls.jsx'
import PlayNow from '/src/main/js/components/header/PlayNow.jsx'

export default function Header(props) {

  const navigate = useNavigate();

  return (
    <Stack h="100%" justify="center" mx="2%">
      {/*
      <Grid align="center">

        <Grid.Col span={3}>
          <Group justify="center">
            <Burger
              opened={props.navbarIsOpen}
              onClick={props.navbarOnClick}
              size="sm"
            />
            <Text size="xl" fw={700} style={{cursor: "pointer"}} onClick={() => navigate("/")}>flummox</Text>
            <IconComet/>
          </Group>
        </Grid.Col>

        <Grid.Col span={6}>
          <Group justify="center">
            <PlayNow />
          </Group>
        </Grid.Col>

        <Grid.Col span={3}>
          <Group justify="center">
            <AccountControls />
          </Group>
        </Grid.Col>

      </Grid>
      */}
      <Group justify="space-between">
          <Group justify="center">
            <Burger
              opened={props.navbarIsOpen}
              onClick={props.navbarOnClick}
              size="sm"
            />
            <Text size="xl" fw={700} style={{cursor: "pointer"}} onClick={() => navigate("/")}>flummox</Text>
            <IconComet/>
          </Group>

          <Group justify="center">
            <PlayNow />
          </Group>

          <Group justify="center">
            <AccountControls />
          </Group>
      </Group>
    </Stack>
  )
}
