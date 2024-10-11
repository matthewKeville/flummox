import React, { useState } from 'react';
import { Container, Group } from '@mantine/core';

import AccountControls from '/src/main/js/components/header/AccountControls.jsx'
import NavBar from '/src/main/js/components/header/NavBar.jsx';
import PlayNow from '/src/main/js/components/header/PlayNow.jsx'

import styles from './Header.module.css'

export default function Header() {

  return (
    <header className={styles.header}>
      <Container className={styles.inner}>

        <Group>
          <h1>ReBoggled</h1>
          <NavBar />
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
