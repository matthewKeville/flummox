import React, { useEffect } from 'react';
import { useSearchParams, useNavigate } from "react-router-dom";

import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { VerifyAccount } from "/src/main/js/services/flummox/AuthenticationService.ts";

//this is clearly abuse of useEffect, perhaps this
//behaviour should be called in the route loader for this "page"
export default function AccountVerify() {

  const [searchParams, setSearchParams ] = useSearchParams();
  const navigate = useNavigate();

  let verifyAccount = async function(email,token) {

    let verifyAccountDTO = {}
    verifyAccountDTO.email = email;
    verifyAccountDTO.token = token;

    let serviceResponse = await VerifyAccount(verifyAccountDTO)
    if (serviceResponse.success) {
      toast.info("Account Verified!");
      navigate("/login");
    } else {
      toast.error("Account Verification failed");
      navigate("/");
    }

  }

  useEffect(() => {
    verifyAccount(searchParams.get("email"),searchParams.get("token"))
  },[]);

  return (<></>);

}
