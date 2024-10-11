import React from 'react';
import { useLoaderData } from 'react-router-dom';
import { UnstyledButton, Group, Avatar, Text, Menu } from '@mantine/core';

import styles from './AccountControls.module.css'

export default function AccountControls() {
  
  const { userInfo } = useLoaderData();

  return (
    <Menu position='bottom-end'>
      <Menu.Target>
        <UnstyledButton className={styles.userbtn}>
          <Group>
            <div style={{ flex: 1 }}>
              <Text size="sm" fw={500}>
                {userInfo.username}
              </Text>
            </div>
            <Avatar
              src={userInfo.avatarUrl} //doesnt exist (yet)
              radius="xl"
            />
          </Group>
        </UnstyledButton>
      </Menu.Target>
      <Menu.Dropdown>
        <Menu.Label>Account</Menu.Label>
        {/* cant use useNavigate here since its taking us out of the react app. maybe can use once moving those pages here */}
        {userInfo.isGuest ?
          <>
            <Menu.Item leftSection="L" onClick={() => {window.location.href="/login"}}>
              Login
            </Menu.Item>
            <Menu.Item leftSection="R" onClick={() => {window.location.href="/signup"}}>
              Sign Up
            </Menu.Item>
          </>
          :
          <>
            <Menu.Item leftSection="L" onClick={() => {window.location.href="/logout"}}>
              Logout
            </Menu.Item>
          </>
        }
      </Menu.Dropdown>
    </Menu>
  )
}
