import React from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { Text, TextInput, Button, Center, Stack } from "@mantine/core";

import config from "config"

export default function LoginPage() {

  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  return (

    <Stack mt="md" mx="30%">
    <Center py="5%">
      <form action={`${config.origin}/login`} method="POST">
        <TextInput name="username" label="Username" placeholder="flummoxer" size="md"/>
        <TextInput mt="md" name="password" label="Password" placeholder="*****" type="password" size="md"/>
        <Button mt="xl" fullWidth type="submit" size="lg" > Login </Button>
        {searchParams.has("error") &&
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
