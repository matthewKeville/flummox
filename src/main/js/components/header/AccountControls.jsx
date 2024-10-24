import React from 'react';
import { useLoaderData } from 'react-router-dom';
import { Button, Avatar, Text, Menu, Group } from '@mantine/core';
import { IconUserCircle } from "@tabler/icons-react";

export default function AccountControls() {
  
  const { userInfo } = useLoaderData();
  const accountIcon = <IconUserCircle/>

  let getActions = function() {
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

  if (userInfo == null) {
    return
  }


  return (
    <Menu width="target" position='bottom-end'>
      <Menu.Target>
        <Button w="150px" justify="space-between" rightSection={accountIcon}>
          <Text ta="center" mr="2px" c="white" size="sm" fw={600}>
            {userInfo.username}
          </Text>
        </Button>
      </Menu.Target>
      <Menu.Dropdown>
        <Menu.Label>Account</Menu.Label>
        {getActions()}
      </Menu.Dropdown>
    </Menu>
  )
}
