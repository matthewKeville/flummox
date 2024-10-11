import React, { useState } from 'react';
import { Container, Group, Button, MenuItem, NavLink } from '@mantine/core';
import { useLocation, useNavigate } from 'react-router-dom';

import styles from './NavBar.module.css'

/* 
  * TODO :
  * Intent here is to produce different nav bars based on @Media queries
  * horizontal navbar for desktop, vertical (hide-away) navbar for mobile 
  * */

const links = [
  { link: '/', label: 'Home' },
  { link: '/lobby', label: 'Lobbies' },
];

export default function NavBar() {

  const location = useLocation();
  const navigate = useNavigate();
  
  const [active, setActive] = useState(location.pathname);

  const items = links.map((link) => (
    <Button
      key={link.label}
      className={styles.navbtn}
      data-active={active === link.link}
      onClick={() => {
        if (active != link.link) {
          setActive(link.link)
          navigate(link.link)
        }
      }}
    >
      {link.label}
    </Button>
  ));
  
  return (
    <>
      {items}
    </>
  )

}
