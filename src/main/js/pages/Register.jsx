import React, { useState } from "react";
import { useRevalidator, useNavigate } from "react-router-dom";
import { TextInput, Button, Stack, Text, Center } from "@mantine/core";

import config from "config"
import { Register } from "/src/main/js/services/flummox/UserService.ts";


export default function RegisterPage() {

  const navigate = useNavigate();
  const revalidator = useRevalidator();
  const [formErrors,setFormErrors] = useState(null);

  let handleSubmit = async function(e) {
    e.preventDefault(); //don't act like a normal submit event

    let formDTO = {}
    formDTO.username = e.target[0].value
    formDTO.email = e.target[1].value
    formDTO.password = e.target[2].value
    formDTO.passwordConfirmation = e.target[3].value

    let serviceResponse = await Register(formDTO)
    console.log(serviceResponse)

    if ( serviceResponse.success && serviceResponse.data.success) {
      revalidator.revalidate();
      navigate("/login")
    } else {
      console.log("couldn't register" + serviceResponse.data)
      setFormErrors(serviceResponse.data)
    }
  }

  return (
    <>
    <Stack mt="md" mx="30%">
    <Center py="5%">
    <form onSubmit={handleSubmit}>
        <TextInput label="Username" placeholder="flummoxer" error={formErrors?.errorUsername} size="md"/>
        <TextInput mt="md" label="Email" type="email" placeholder="person@email.com" error={formErrors?.errorEmail} size="md"/>
        <TextInput mt="md" label="Password" type="password" placeholder="*****" error={formErrors?.errorPassword} size="md"/>
        <TextInput mt="md" label="Confirm" type="password" placeholder="*****" size="md"/>
        <Button mt="xl" fullWidth size="lg" type="submit">Register</Button>
    </form>
    </Center>
    <Center py="2%">
      <Text ta="center" mt="md" c="black">Have an account? <Text c="blue.8" style={{cursor: "pointer"}} span onClick={()=>{navigate("/login")}}>Login</Text></Text>
    </Center>
    </Stack>
    </>
  )
}
