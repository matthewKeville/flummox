package com.keville.ReBoggled.service.registrationService;

import com.keville.ReBoggled.DTO.RegisterUserDTO;

public interface RegistrationService {

    public void registerUser(RegisterUserDTO dto) throws RegistrationServiceException;

}
