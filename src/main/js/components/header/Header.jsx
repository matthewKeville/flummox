import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Group, Stack, Grid } from '@mantine/core';
import { Burger } from "@mantine/core";

import AccountControls from '/src/main/js/components/header/AccountControls.jsx'
import PlayNow from '/src/main/js/components/header/PlayNow.jsx'

export default function Header(props) {

  const navigate = useNavigate();

  return (
    <Stack h="100%" justify="center" mx="2%">
      <Grid align="center">

        <Grid.Col span={3}>
          <Group justify="center">
            <Burger
              opened={props.navbarIsOpen}
              onClick={props.navbarOnClick}
              size="sm"
            />
            <label style={{cursor: "pointer"}} onClick={() => navigate("/")}>ReBoggled</label>
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
    </Stack>
  )
}
