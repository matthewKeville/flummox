//webpack alias resolves to config/local.json or  config/prod.json
import config from "config" 

import React from 'react';
import { useNavigate, useLoaderData, useRevalidator } from "react-router-dom";
import { Button, Text, Menu } from '@mantine/core';
import { IconUserCircle } from "@tabler/icons-react";

import { Logout } from "/src/main/js/services/flummox/AuthenticationService.ts";

export default function AccountControls() {
  
  const { userInfo } = useLoaderData();
  const navigate = useNavigate();
  const revalidator = useRevalidator();

  const accountIcon = <IconUserCircle/>

  let getActions = function() {
    return userInfo.isGuest ?
      <>
        <Menu.Item leftSection="L" onClick={() => {navigate("login")}}>
          Login
        </Menu.Item>
        <Menu.Item leftSection="R" onClick={() => {navigate("register")}}>
          Sign Up
        </Menu.Item>
      </>
      :
      <>
        <Menu.Item leftSection="L" onClick={
          () => {
            Logout();
            revalidator.revalidate()
            navigate("/")
          }
        }>
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
