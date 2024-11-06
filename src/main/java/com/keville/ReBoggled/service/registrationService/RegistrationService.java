package com.keville.ReBoggled.service.registrationService;

import com.keville.ReBoggled.DTO.RegisterUserRequestDTO;
import com.keville.ReBoggled.DTO.UserVerifyRequestDTO;

public interface RegistrationService {

    public static final int MIN_USERNAME_LENGTH     = 3;
    public static final int MAX_USERNAME_LENGTH     = 24;
    public static final int MAX_EMAIL_LENGTH        = 255;
    public static final int MAX_PASSWORD_LENGTH     = 80;
    public static final int MIN_PASSWORD_LENGTH     = 8;

    public void registerUser(RegisterUserRequestDTO dto) throws RegistrationServiceException;
    public void verifyEmail(UserVerifyRequestDTO userVerifyRequestDTO) throws RegistrationServiceException;

}
