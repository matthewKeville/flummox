import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Group } from '@mantine/core';
import { Burger } from "@mantine/core";

import AccountControls from '/src/main/js/components/header/AccountControls.jsx'
import PlayNow from '/src/main/js/components/header/PlayNow.jsx'

import styles from './Header.module.css'

export default function Header(props) {

  const navigate = useNavigate();

  return (
    <header className={styles.header}>
      <Container className={styles.inner}>

        <Group>
          <Burger
            opened={props.navbarIsOpen}
            onClick={props.navbarOnClick}
            size="sm"
          />
          <label style={{cursor: "pointer"}} onClick={() => navigate("/")}>ReBoggled</label>
        </Group>

        <Group>
          <PlayNow />
        </Group>

        <Group>
          <AccountControls />
        </Group>


      </Container>
    </header>



  )
}
