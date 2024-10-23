import React from 'react';
import { useLoaderData } from 'react-router-dom';
import { Button, Avatar, Text, Menu } from '@mantine/core';

export default function AccountControls() {
  
  const { userInfo } = useLoaderData();

  let getActions = function() {
  }

  if (userInfo == null) {
    return userInfo.isGuest ?
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

  return (
    <Menu width="target" position='bottom-end'>
      <Menu.Target>
        <Button>
          <Text mr="4px" c="white" size="xs" fw={500}>
            {userInfo.username}
          </Text>
          <Avatar
            color="white"
            radius="xs"
          />
        </Button>
      </Menu.Target>
      <Menu.Dropdown>
        <Menu.Label>Account</Menu.Label>
        {getActions()}
      </Menu.Dropdown>
    </Menu>
  )
}
