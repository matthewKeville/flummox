import React, { useState } from "react";
import { useRevalidator } from "react-router-dom";
import {  useNavigate } from "react-router-dom";
import { Text, TextInput, Button, Center, Stack } from "@mantine/core";

import config from "config"
import { Login } from "/src/main/js/services/flummox/UserService.ts";

export default function LoginPage() {

  const navigate = useNavigate();
  const revalidator = useRevalidator();
  const [badLogin,setBadLogin] = useState(false);

  let handleSubmit = async function(e) {
    e.preventDefault(); //don't act like a normal submit event

    let formDTO = {}
    formDTO.username = e.target[0].value
    formDTO.password = e.target[1].value

    let serviceResponse = await Login(formDTO)
    console.log(serviceResponse)

    if ( serviceResponse.success) {
      revalidator.revalidate();
      navigate("/")
    } else {
      setBadLogin(true)
    }
  }

  return (

    <Stack mt="md" mx="30%">
    <Center py="5%">
      <form onSubmit={handleSubmit}>
        <TextInput name="username" label="Username" placeholder="flummoxer" size="md"/>
        <TextInput mt="md" name="password" label="Password" placeholder="*****" type="password" size="md"/>
        <Button mt="xl" fullWidth type="submit" size="lg" > Login </Button>
        {badLogin &&
          <Text mt="md" c="red">Login failed</Text>
        }
      </form>
    </Center>
    <Center py="2%">
      <Text ta="center" mt="md" c="black">Don't have an account? <Text c="blue.8" style={{cursor: "pointer"}} span onClick={()=>{navigate("/register")}}>Sign up</Text></Text>
    </Center>
    </Stack>

  )
}
