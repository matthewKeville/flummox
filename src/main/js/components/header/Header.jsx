import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Group, Stack, Grid, Text, Burger, Flex, em } from '@mantine/core';
import { useMediaQuery } from '@mantine/hooks';
import { IconComet } from "@tabler/icons-react";

import AccountControls from '/src/main/js/components/header/AccountControls.jsx'
import PlayNow from '/src/main/js/components/header/PlayNow.jsx'

export default function Header(props) {

  const navigate = useNavigate();
  const isMobile = useMediaQuery(`(max-width: ${em(750)})`);

  return (
    <Stack w="100%" h="100%" justify="center">
    <Group justify="space-between" mx="2%">
        <Group justify="center">
          <Burger
            opened={props.navbarIsOpen}
            onClick={props.navbarOnClick}
            size="sm"
          />

          <Text size={{ base: "xl", sm: "md"}} fw={700} style={{cursor: "pointer"}} onClick={() => navigate("/")}>flummox</Text>
          <IconComet/>
        </Group>

        <Group justify="center">
          <PlayNow />
        </Group>
        
        {!isMobile && <Group justify="center">
              <AccountControls hideName={isMobile}/>
            </Group>
        }
    </Group>
    </Stack>
  )
}
